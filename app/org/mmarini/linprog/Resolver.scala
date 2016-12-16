// Copyright (c) 2016 Marco Marini, marco.marini@mmarini.org
//
// Licensed under the MIT License (MIT);
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://opensource.org/licenses/MIT
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

package org.mmarini.linprog

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.diag
import breeze.linalg.max
import breeze.linalg.min
import breeze.linalg.inv
import breeze.linalg.sum
import breeze.numerics.floor
import breeze.numerics.ceil
import breeze.stats.distributions.Rand
import breeze.stats.distributions.RandBasis

/** */
class Resolver(
    val chain: Map[String, Product],
    val suppliers: Map[String, Int],
    val values: Map[String, Double])(
        implicit random: RandBasis = Rand) {

  /** Resolves the configuration */
  def resolve(): SupplyChainConf = {
    val rp = rndProducts
    val pc = (for {
      (pName, i) <- productNames.zipWithIndex
    } yield {
      (pName,
        ProductConf(
          name = pName,
          supplierName = chain(pName).producer,
          fixed = np1Vector(i).toInt,
          random = rp.getOrElse(pName, 0)))
    }).toMap

    SupplyChainConf(pc, computeConsumptions(pc.values.map {
      case ProductConf(name, _, fix, random) => (name, fix + random)
    }.toMap))
  }

  /** Computes the consumptions */
  private def computeConsumptions(productions: Map[String, Int]): Map[String, Int] =
    productions.
      // Extracts total # of product consumed by supplier
      map {
        case (name, n) =>
          chain(name).
            consumptions.
            map {
              case (name, cons) => (name, (cons * n).toInt)
            }.toSeq
      }
      .flatten
      // Groups by product
      .groupBy(_._1)
      // Computes grand total
      .map {
        case (name, list) => (name, list.map(_._2).sum)
      }

  /** Returns the product names */
  lazy val productNames: Seq[String] = chain.keys.toIndexedSeq.sorted

  /** Returns the suppliers names*/
  lazy val suppliersNames: Seq[String] = chain.values.map(_.producer).toSet.toIndexedSeq.sorted

  /** Returns the number of products */
  lazy val noProducts: Int = productNames.size

  /** Returns the number of suppliers */
  lazy val noSuppliers: Int = suppliersNames.size

  /** Returns the number of variables of the linear system */
  lazy val noVars: Int = noProducts + noSuppliers

  /** Returns the product values vector */
  lazy val vVector: DenseVector[Double] =
    DenseVector(productNames.map(values).toArray: _*)

  /** Returns the product quantities vector */
  lazy val qVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).quantity).toArray: _*)

  /** Returns the product quantities matrix */
  val qMatrix: DenseMatrix[Double] = diag(qVector)

  /** Returns the number of suppliers vector */
  lazy val nVector: DenseVector[Double] =
    DenseVector(suppliersNames.map(suppliers(_).toDouble).toArray: _*)

  /** Returns the number of suppliers by product vector */
  lazy val n1Vector: DenseVector[Double] =
    DenseVector(productNames.
      map {
        p => suppliers(chain(p).producer).toDouble
      }.
      toArray: _*)

  /** Returns the number of suppliers by product matrix */
  lazy val nMatrix: DenseMatrix[Double] = diag(n1Vector)

  /** Returns the product consumptions by produced product matrix */
  lazy val dMatrix: DenseMatrix[Double] = {
    val x = DenseMatrix.zeros[Double](noProducts, noProducts)
    for {
      (name, i) <- productNames.zipWithIndex
      (consName, j) <- productNames.zipWithIndex
      value <- chain(name).consumptions.get(consName)
    } {
      x(i, j) = value
    }
    x
  }

  /** Returns the production values vector */
  val gVector: DenseVector[Double] = nMatrix * (qMatrix - dMatrix) * vVector

  /** */
  lazy val minVector: DenseVector[Double] =
    DenseVector.vertcat(-gVector, DenseVector.zeros[Double](noSuppliers))

  /** Returns the supplier, product map matrix */
  lazy val thetaMatrix: DenseMatrix[Double] = {
    val theta = DenseMatrix.zeros[Double](noSuppliers, noProducts)
    for {
      (name, i) <- productNames.zipWithIndex
    } {
      val j = suppliersNames.indexOf(chain(name).producer)
      theta(j, i) = 1
    }
    theta
  }

  /** Returns the production time by product vector */
  lazy val tVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).time.toSeconds.toDouble).toArray: _*)

  /** Returns the production time by product matrix */
  lazy val tMatrix: DenseMatrix[Double] = diag(tVector)

  /** Returns the production time by supplier matrix */
  lazy val uMatrix: DenseMatrix[Double] = thetaMatrix * tMatrix

  /** Returns the equality constraint transformation matrix */
  lazy val equMatrix: DenseMatrix[Double] =
    DenseMatrix.horzcat(uMatrix, DenseMatrix.eye[Double](noSuppliers))

  /** Returns the equality constraint value vector */
  lazy val equVector: DenseVector[Double] = DenseVector.ones[Double](noSuppliers)

  /** */
  lazy val fMatrix: DenseMatrix[Double] = (qMatrix - dMatrix.t) * nMatrix

  /** Returns the greater euqal constraint transformation matrix */
  lazy val geMatrix: DenseMatrix[Double] = {
    val m1 = DenseMatrix.horzcat(fMatrix, DenseMatrix.zeros[Double](noProducts, noSuppliers))
    val m2 = DenseMatrix.eye[Double](noVars)
    DenseMatrix.vertcat(m1, m2)
  }

  /** Returns the greater equal constraint values vector */
  lazy val geVector: DenseVector[Double] =
    DenseVector.zeros[Double](noVars + noProducts)

  /** Returns the solutions vector */
  lazy val xVector: DenseVector[Double] =
    LinearProgResolver.
      minimize(minVector).
      =:=(equMatrix, equVector).
      >=(geMatrix, geVector).
      resolve

  /** Returns the productivity rate by product vector */
  lazy val wVector: DenseVector[Double] = xVector(0 until noProducts)

  /** Returns the inactivity rate by product vector */
  lazy val zVector: DenseVector[Double] = xVector(noProducts until noProducts + noSuppliers)

  /** Returns the number of assigned suppliers by product vector */
  lazy val npVector: DenseVector[Double] = nMatrix * tMatrix * wVector

  /** Returns the number of fixed suppliers by product vector */
  lazy val np1Vector: DenseVector[Double] = floor(npVector)

  /** Returns the fractional number of suppliers by product vector */
  lazy val rVector: DenseVector[Double] = npVector - np1Vector

  /** Returns the fractional number of suppliers by supplier vector */
  lazy val r1Vector: DenseVector[Double] = thetaMatrix * rVector

  /** Returns the random number of suppliers by supplier vector */
  lazy val totRndVector: DenseVector[Double] = ceil(r1Vector)

  /** Returns the random number of suppliers by supplier vector leverage to 1  */
  lazy val tot2RndVector: DenseVector[Double] = max(totRndVector, DenseVector.ones[Double](noSuppliers))

  /** Returns the random number of suppliers by product vector leverage to 1  */
  lazy val tot3RndVector: DenseVector[Double] = thetaMatrix.t * tot2RndVector

  /** Returns the choice probability by product vector  */
  lazy val probVector: DenseVector[Double] = inv(diag(tot3RndVector)) * rVector

  /** Returns the choice probability by supplier, product matrix  */
  lazy val probMatrix: DenseMatrix[Double] = thetaMatrix * diag(probVector)

  /** Returns the randomized production assignment map */
  lazy val rndProducts: Map[String, Int] = {
    val fixed = thetaMatrix * np1Vector
    val nSuppliers = min(totRndVector, nVector - fixed).toArray.map(_.toInt)
    val rndSuppliers = for {
      i <- 0 until noSuppliers
      p = probMatrix(i, ::).t
      if (sum(p) > 0)
      _ <- 1 to nSuppliers(i)
      idx <- choose(p)
    } yield idx
    rndSuppliers.
      groupBy(x => x).
      map(x => productNames(x._1) -> x._2.length)
  }

  /**
   * Chooses a integer with a given partial probabilities.
   * If the sum of partial probabilities is < 0 then returns a None value with probability equal to
   * 1 - sum(p)
   *
   * @param p probabilities vector; sum(p) must be <= 1
   */
  def choose(p: DenseVector[Double]): Option[Int] = {
    // Computes the cumulative distribution
    val cf = DenseVector.zeros[Double](p.length)
    for { i <- 0 until p.length } {
      cf(i) = sum(p(0 to i))
    }
    // Selects index with cumulative distribution greater than random value
    val rr = random.uniform.draw
    val idx = cf.toArray.indexWhere { x => rr < x }
    if (idx >= 0) Some(idx) else None
  }
}
