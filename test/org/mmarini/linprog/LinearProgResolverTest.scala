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

import scala.annotation.migration

import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks

import net.jcazevedo.moultingyaml.DeserializationException
import net.jcazevedo.moultingyaml.PimpedString
import scala.concurrent.duration._
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import breeze.numerics.abs
import breeze.linalg.max

class LinearProgResolverTest extends PropSpec with PropertyChecks with Matchers {
  val Delta = 1e-3
  val Epsilon = 1e-10

  property("""given
  minimize(x + y)
  subject to x - y = 0
  subject to x >= 0
  subject to x <= 1
  subject to y >= 0
when
  resolved
then results
  x = 1
  y = 1
""") {
    val a = DenseMatrix(
      (1.0, -1.0))
    val b = DenseVector.zeros[Double](1)
    val c = DenseVector(1.0, 1.0)
    val d = DenseMatrix(
      (1.0, 0.0),
      (-1.0, 0.0),
      (0.0, 1.0))
    val f = DenseVector(0.0, -1.0, 0.0)
    val n = c.length

    val deltaGen = for {
      x <- Gen.listOfN(n, Gen.choose(-Delta, Delta))
    } yield {
      DenseVector(x.toArray: _*)
    }

    forAll(
      (deltaGen, "delta")) {
        delta =>
          {
            val x = LinearProgResolver.minimize(c).
              =:=(a, b).
              >=(d, f).
              resolve

            x shouldBe DenseVector(0.0, 0.0)

            val eq = max(abs(a * x - b))
            eq should be <= Epsilon

            val gt = d * x - f
            gt.foreach { x => x should be >= -Epsilon }
          }
      }
  }

  property("""given
  maximize(x + y)
  subject to x - y = 0
  subject to x >= 0
  subject to x <= 1
  subject to y >= 0
when
  resolved
then results
  x = 1
  y = 1
""") {
    val a = DenseMatrix(
      (1.0, -1.0))
    val b = DenseVector.zeros[Double](1)
    val c = DenseVector(1.0, 1.0)
    val d = DenseMatrix(
      (1.0, 0.0),
      (-1.0, 0.0),
      (0.0, 1.0))
    val f = DenseVector(0.0, -1.0, 0.0)
    val n = c.length

    val deltaGen = for {
      x <- Gen.listOfN(n, Gen.choose(-Delta, Delta))
    } yield {
      DenseVector(x.toArray: _*)
    }

    forAll(
      (deltaGen, "delta")) {
        delta =>
          {
            val x = LinearProgResolver.maximize(c).
              =:=(a, b).
              >=(d, f).
              resolve

            x shouldBe DenseVector(1.0, 1.0)

            val eq = max(abs(a * x - b))
            eq should be <= Epsilon

            val gt = d * x - f
            gt.foreach { x => x should be >= -Epsilon }
          }
      }
  }

}
