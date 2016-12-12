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
import breeze.linalg.inv
import breeze.linalg.sum
import breeze.numerics.floor
import breeze.numerics.ceil
import breeze.stats.distributions.Rand
import breeze.stats.distributions.RandBasis

/** */
class Resolver(
    chain: Map[String, Product],
    suppliers: Map[String, Int],
    values: Map[String, Double])(
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

  /** */
  lazy val productNames: Seq[String] = chain.keys.toIndexedSeq.sorted

  /** */
  lazy val suppliersNames: Seq[String] = chain.values.map(_.producer).toSet.toIndexedSeq.sorted

  /** */
  lazy val noProducts: Int = productNames.size

  /** */
  lazy val noSuppliers: Int = suppliersNames.size

  /** */
  lazy val noVars: Int = noProducts + noSuppliers

  /** */
  lazy val vVector: DenseVector[Double] =
    DenseVector(productNames.map(values).toArray: _*)

  /** */
  lazy val qVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).quantity).toArray: _*)

  /** */
  val qMatrix: DenseMatrix[Double] = diag(qVector)

  /** */
  lazy val nVector: DenseVector[Double] =
    DenseVector(suppliersNames.map(suppliers(_).toDouble).toArray: _*)

  /** */
  lazy val n1Vector: DenseVector[Double] =
    DenseVector(productNames.
      map {
        p => suppliers(chain(p).producer).toDouble
      }.
      toArray: _*)

  /** */
  lazy val nMatrix: DenseMatrix[Double] = diag(n1Vector)

  /** */
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

  /** */
  val gVector: DenseVector[Double] = nMatrix * (qMatrix - dMatrix) * vVector

  /** */
  lazy val minVector: DenseVector[Double] =
    DenseVector.vertcat(-gVector, DenseVector.zeros[Double](noSuppliers))

  /** */
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

  /** */
  lazy val tVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).time.toSeconds.toDouble).toArray: _*)

  /** */
  lazy val tMatrix: DenseMatrix[Double] = diag(tVector)

  /** */
  lazy val uMatrix: DenseMatrix[Double] = thetaMatrix * tMatrix

  /** */
  lazy val equMatrix: DenseMatrix[Double] =
    DenseMatrix.horzcat(uMatrix, DenseMatrix.eye[Double](noSuppliers))

  /** */
  lazy val equVector: DenseVector[Double] = DenseVector.ones[Double](noSuppliers)

  /** */
  lazy val fMatrix: DenseMatrix[Double] = (qMatrix - dMatrix.t) * nMatrix

  /** */
  lazy val geMatrix: DenseMatrix[Double] = {
    val m1 = DenseMatrix.horzcat(fMatrix, DenseMatrix.zeros[Double](noProducts, noSuppliers))
    val m2 = DenseMatrix.eye[Double](noVars)
    DenseMatrix.vertcat(m1, m2)
  }

  /** */
  lazy val geVector: DenseVector[Double] =
    DenseVector.zeros[Double](noVars + noProducts)

  /**  */
  lazy val xVector: DenseVector[Double] =
    LinearProgResolver.
      minimize(minVector).
      =:=(equMatrix, equVector).
      >=(geMatrix, geVector).
      resolve

  /** */
  lazy val wVector: DenseVector[Double] = xVector(0 until noProducts)

  /** */
  lazy val zVector: DenseVector[Double] = xVector(noProducts until noProducts + noSuppliers)

  /** */
  lazy val npVector: DenseVector[Double] = nMatrix * tMatrix * wVector

  /** */
  lazy val np1Vector: DenseVector[Double] = floor(npVector)

  /** */
  lazy val rVector: DenseVector[Double] = npVector - np1Vector

  /** */
  lazy val r1Vector: DenseVector[Double] = thetaMatrix * rVector

  /** */
  lazy val totRndVector: DenseVector[Double] = ceil(r1Vector)

  /** */
  lazy val tot2RndVector: DenseVector[Double] = max(totRndVector, DenseVector.ones[Double](noSuppliers))

  /** */
  lazy val tot3RndVector: DenseVector[Double] = thetaMatrix.t * tot2RndVector

  /** */
  lazy val probVector: DenseVector[Double] = inv(diag(tot3RndVector)) * rVector

  /** */
  lazy val probMatrix: DenseMatrix[Double] = thetaMatrix * diag(probVector)

  /** */
  lazy val rndProducts: Map[String, Int] = {
    val rndSuppliers = for {
      i <- 0 until noSuppliers
      p = probMatrix(i, ::).t
      if (sum(p) > 0)
      j <- 0 until totRndVector(i).toInt
      idx <- choose(p)
    } yield idx
    rndSuppliers.
      groupBy(x => x).
      map(x => productNames(x._1) -> x._2.length)
  }

  def choose(p: DenseVector[Double]): Option[Int] = {
    val rr = random.uniform.draw
    val cf = DenseVector.zeros[Double](p.length)
    for { i <- 0 until p.length } {
      cf(i) = sum(p(0 to i))
    }
    val idx = cf.toArray.indexWhere { x => rr < x }
    val y = if (idx >= 0) Some(idx) else None
    y
  }
}
