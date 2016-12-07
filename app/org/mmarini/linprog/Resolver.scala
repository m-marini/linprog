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

import breeze.optimize.linear.LinearProgram
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import breeze.linalg.diag
import sun.misc.VM

class Resolver(chain: Map[String, Product], suppliers: Map[String, Int], values: Map[String, Double]) {

  /** Resolves the configuration */
  def resolve(): SupplyChainConf = {

    SupplyChainConf(Map(), Map())
  }

  /** */
  val productNames: Seq[String] = chain.keys.toIndexedSeq.sorted

  /** */
  val suppliersNames: Seq[String] = chain.values.map(_.producer).toSet.toIndexedSeq.sorted

  /** */
  val noProducts: Int = productNames.size

  /** */
  val noSuppliers: Int = suppliersNames.size

  /** */
  val noVars: Int = noProducts + noSuppliers

  /** */
  val vVector: DenseVector[Double] =
    DenseVector(productNames.map(values).toArray: _*)

  /** */
  val qVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).quantity).toArray: _*)

  /** */
  val qMatrix: DenseMatrix[Double] = diag(qVector)

  /** */
  val nVector: DenseVector[Double] =
    DenseVector(suppliersNames.map(suppliers(_).toDouble).toArray: _*)

  /** */
  val n1Vector: DenseVector[Double] =
    DenseVector(productNames.
      map {
        p => suppliers(chain(p).producer).toDouble
      }.
      toArray: _*)

  /** */
  val nMatrix: DenseMatrix[Double] = diag(n1Vector)

  /** */
  val dMatrix: DenseMatrix[Double] = {
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
  val minVector: DenseVector[Double] =
    DenseVector.vertcat(-gVector, DenseVector.zeros[Double](noSuppliers))

  /** */
  val thetaMatrix: DenseMatrix[Double] = {
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
  val tVector: DenseVector[Double] =
    DenseVector(productNames.map(chain(_).time.toSeconds.toDouble).toArray: _*)

  /** */
  val tMatrix: DenseMatrix[Double] = diag(tVector)

  /** */
  val uMatrix: DenseMatrix[Double] = thetaMatrix * tMatrix

  /** */
  val equMatrix: DenseMatrix[Double] =
    DenseMatrix.horzcat(uMatrix, DenseMatrix.eye[Double](noSuppliers))

  /** */
  val equVector: DenseVector[Double] = DenseVector.ones[Double](noSuppliers)

  /** */
  val fMatrix: DenseMatrix[Double] = (qMatrix - dMatrix.t) * nMatrix

  /** */
  val geMatrix: DenseMatrix[Double] = {
    DenseMatrix.zeros(0, 0)
  }

  /** */
  val geVector: DenseVector[Double] = {
    DenseVector.zeros(0)
  }

  /** */
  val xVector: DenseVector[Double] = {
    DenseVector.zeros(0)
  }
}
