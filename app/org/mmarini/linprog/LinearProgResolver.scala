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
import breeze.linalg.operators.DenseVector_GenericOps
import scala.util.Try

/**
 * A linear program resolver resolves linear system in X variable of type
 *    minimize(C * X)
 * where C is a vector
 * given the constraints
 *    A * X = B
 * where A is a matrix and B is a vector
 * and
 *    D * X >= F
 * where D is a matrix and F is a vector
 */
class LinearProgResolver(
    a: DenseMatrix[Double],
    b: DenseVector[Double],
    c: DenseVector[Double],
    d: DenseMatrix[Double],
    f: DenseVector[Double]) {

  def =:=(m: DenseMatrix[Double], y: DenseVector[Double]): LinearProgResolver = {
    require(m.cols == c.length, s"# columns (${m.cols}) should match # of variables (${c.length})")
    require(m.rows == y.length, s"# rows (${m.cols}) should match # of constraints (${y.length})")
    new LinearProgResolver(
      a = m,
      b = y,
      c = c,
      d = d,
      f = f)
  }

  def >=(m: DenseMatrix[Double], y: DenseVector[Double]): LinearProgResolver = {
    require(m.cols == c.length, s"# columns (${m.cols}) should match # of variables (${c.length})")
    require(m.rows == y.length, s"# rows (${m.cols}) should match # of constraints (${y.length})")
    new LinearProgResolver(
      a = a,
      b = b,
      c = c,
      d = m,
      f = y)
  }

  def resolve: DenseVector[Double] = {
    val lp = new LinearProgram()
    import lp._
    val x = (1 to c.length).map { _ => Real() }

    def linExp(a: DenseVector[Double]) =
      x.
        zipWithIndex.
        map { case (x, i) => x * a(i) }.reduce(_ + _)

    def linExpSeq(m: DenseMatrix[Double]) =
      for { i <- 0 until m.rows } yield {
        linExp(m(i, ::).t)
      }

    val exp = linExp(c)

    val equConstrs = for {
      (exp, i) <- linExpSeq(a).zipWithIndex
    } yield exp =:= b(i)

    val gtConstrs = for {
      (exp, i) <- linExpSeq(d).zipWithIndex
    } yield exp >= f(i)

    val constrs = equConstrs ++ gtConstrs
    
    minimize(exp.subjectTo(constrs.toArray: _*)).result
  }
}

/** */
object LinearProgResolver {

  /** */
  def minimize(c: DenseVector[Double]): LinearProgResolver = new LinearProgResolver(
    a = DenseMatrix.zeros(0, 0),
    b = DenseVector.zeros(0),
    c = c,
    d = DenseMatrix.zeros(0, 0),
    f = DenseVector.zeros(0))

  /** */
  def maximize(c: DenseVector[Double]): LinearProgResolver = new LinearProgResolver(
    a = DenseMatrix.zeros(0, 0),
    b = DenseVector.zeros(0),
    c = -c,
    d = DenseMatrix.zeros(0, 0),
    f = DenseVector.zeros(0))

}
