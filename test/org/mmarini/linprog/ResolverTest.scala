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

import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks

import breeze.linalg.DenseVector
import breeze.linalg.diag
import breeze.linalg.max
import breeze.numerics.abs

class ResolverTest extends PropSpec with PropertyChecks with Matchers {
  val NoProducts = 22
  val NoSuppliers = 11
  val NoVars = 33
  val Epsilon = 1e-4

  def buildResolver: Resolver = {
    val chain = SupplyChain.fromClasspath("/chain.yaml")
    val values = Parameters.fromClasspath("/max-values.yaml")
    val suppliers = Parameters.fromClasspath("/base-config.yaml").
      map(a => a._1 -> a._2.toInt)
    val resolver = new Resolver(chain, suppliers, values)
    resolver
  }

  def assertLike(x: DenseVector[Double], expected: Double*)(implicit epsilon: Double = Epsilon) {
    x.length shouldBe expected.length
    x.toArray.zip(expected).foreach { case (x, exp) => x shouldBe exp +- epsilon }
  }

  property("resolver counter") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.noProducts shouldBe (22)
            resolver.noSuppliers shouldBe (11)
            resolver.noVars shouldBe (33)
          }
      }
  }

  property("resolver vVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {

            assertLike(resolver.vVector,
              82.0000, 7.2000, 122.0000, 3.6000, 169.0000, 54.0000, 32.4000, 151.0000, 7.2000, 7.2000, 14.4000, 14.4000,
              14.4000, 50.4000, 201.0000, 21.6000, 72.0000, 50.0000, 82.0000, 219.0000, 10.8000, 18.0000)

          }
      }
  }

  property("resolver qVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            assertLike(resolver.qVector,
              1, 2, 1, 2, 1, 1, 1, 1, 2, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 2, 1)

            resolver.qMatrix shouldBe diag(resolver.qVector)

          }
      }
  }

  property("resolver nVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            assertLike(resolver.nVector, 1, 10.0, 1, 1, 6.0, 5.0, 1, 5.0, 1, 5.0, 1)

            assertLike(resolver.n1Vector,
              1, 10.0, 1, 10.0, 1, 5.0, 5.0, 1, 10.0, 1, 1, 1, 1, 5.0, 1, 1, 1, 1, 1, 1, 10.0, 6.0)

            resolver.nMatrix shouldBe diag(resolver.n1Vector)

          }
      }
  }

  property("resolver dMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.dMatrix should have('rows(NoProducts))
            resolver.dMatrix should have('cols(NoProducts))

          }
      }
  }

  property("resolver gVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            assertLike(
              resolver.gVector,
              17.2000, 72.0000, 24.8000, 36.0000, 25.0000, 198.0000, 90.0000, 43.0000, 72.0000, 7.2000, 18.0000,
              14.4000, 21.6000, 180.0000, 28.2000, 10.8000, 28.8000, 17.6000, 35.2000, 42.6000, 108.0000,
              64.8000)

          }
      }
  }

  property("resolver minVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            assertLike(
              resolver.minVector,
              -17.20000, -72.00000, -24.80000, -36.00000, -25.00000, -198.00000, -90.00000, -43.00000, -72.00000,
              -7.20000, -18.00000, -14.40000, -21.60000, -180.00000, -28.20000, -10.80000, -28.80000, -17.60000,
              -35.20000, -42.60000, -108.00000, -64.80000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
              0.00000, 0.00000, 0.00000, 0.00000, 0.00000)

          }
      }
  }

  property("resolver thetaMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.thetaMatrix should have('rows(NoSuppliers))
            resolver.thetaMatrix should have('cols(NoProducts))

            resolver.thetaMatrix(2, 0) shouldBe 1
            resolver.thetaMatrix(1, 1) shouldBe 1
            resolver.thetaMatrix(2, 2) shouldBe 1
            resolver.thetaMatrix(1, 3) shouldBe 1
            resolver.thetaMatrix(0, 4) shouldBe 1
            resolver.thetaMatrix(9, 5) shouldBe 1
            resolver.thetaMatrix(7, 6) shouldBe 1
            resolver.thetaMatrix(10, 7) shouldBe 1
            resolver.thetaMatrix(1, 8) shouldBe 1
            resolver.thetaMatrix(6, 9) shouldBe 1
            resolver.thetaMatrix(6, 10) shouldBe 1
            resolver.thetaMatrix(6, 11) shouldBe 1
            resolver.thetaMatrix(6, 12) shouldBe 1
            resolver.thetaMatrix(5, 13) shouldBe 1
            resolver.thetaMatrix(0, 14) shouldBe 1
            resolver.thetaMatrix(8, 15) shouldBe 1
            resolver.thetaMatrix(8, 16) shouldBe 1
            resolver.thetaMatrix(2, 17) shouldBe 1
            resolver.thetaMatrix(3, 18) shouldBe 1
            resolver.thetaMatrix(3, 19) shouldBe 1
            resolver.thetaMatrix(1, 20) shouldBe 1
            resolver.thetaMatrix(4, 21) shouldBe 1

          }
      }
  }

  property("resolver tVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            assertLike(resolver.tVector,
              1800, 600, 3600, 120, 7200, 21600, 3600, 7200, 300, 240, 1200, 600, 1800, 14400, 3600, 240,
              1800, 1200, 3600, 10800, 1200, 600)

            resolver.tMatrix shouldBe diag(resolver.tVector)

          }
      }
  }

  property("resolver uMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.uMatrix should have('rows(NoSuppliers))
            resolver.uMatrix should have('cols(NoProducts))

            resolver.uMatrix(2, 0) shouldBe 1800.0
            resolver.uMatrix(1, 1) shouldBe 600.0
            resolver.uMatrix(2, 2) shouldBe 3600.0
            resolver.uMatrix(1, 3) shouldBe 120.0
            resolver.uMatrix(0, 4) shouldBe 7200.0
            resolver.uMatrix(9, 5) shouldBe 21600.0
            resolver.uMatrix(7, 6) shouldBe 3600.0
            resolver.uMatrix(10, 7) shouldBe 7200.0
            resolver.uMatrix(1, 8) shouldBe 300.0
            resolver.uMatrix(6, 9) shouldBe 240.0
            resolver.uMatrix(6, 10) shouldBe 1200.0
            resolver.uMatrix(6, 11) shouldBe 600.0
            resolver.uMatrix(6, 12) shouldBe 1800.0
            resolver.uMatrix(5, 13) shouldBe 14400.0
            resolver.uMatrix(0, 14) shouldBe 3600.0
            resolver.uMatrix(8, 15) shouldBe 240.0
            resolver.uMatrix(8, 16) shouldBe 1800.0
            resolver.uMatrix(2, 17) shouldBe 1200.0
            resolver.uMatrix(3, 18) shouldBe 3600.0
            resolver.uMatrix(3, 19) shouldBe 10800.0
            resolver.uMatrix(1, 20) shouldBe 1200.0
            resolver.uMatrix(4, 21) shouldBe 600.0
          }
      }
  }

  property("resolver equMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.equMatrix should have('rows(NoSuppliers))
            resolver.equMatrix should have('cols(NoVars))

            resolver.equMatrix(2, 0) shouldBe 1800
            resolver.equMatrix(1, 1) shouldBe 600
            resolver.equMatrix(2, 2) shouldBe 3600
            resolver.equMatrix(1, 3) shouldBe 120
            resolver.equMatrix(0, 4) shouldBe 7200
            resolver.equMatrix(9, 5) shouldBe 21600
            resolver.equMatrix(7, 6) shouldBe 3600
            resolver.equMatrix(10, 7) shouldBe 7200
            resolver.equMatrix(1, 8) shouldBe 300
            resolver.equMatrix(6, 9) shouldBe 240
            resolver.equMatrix(6, 10) shouldBe 1200
            resolver.equMatrix(6, 11) shouldBe 600
            resolver.equMatrix(6, 12) shouldBe 1800
            resolver.equMatrix(5, 13) shouldBe 14400
            resolver.equMatrix(0, 14) shouldBe 3600
            resolver.equMatrix(8, 15) shouldBe 240
            resolver.equMatrix(8, 16) shouldBe 1800
            resolver.equMatrix(2, 17) shouldBe 1200
            resolver.equMatrix(3, 18) shouldBe 3600
            resolver.equMatrix(3, 19) shouldBe 10800
            resolver.equMatrix(1, 20) shouldBe 1200
            resolver.equMatrix(4, 21) shouldBe 600

            resolver.equMatrix(0, 22) shouldBe 1
            resolver.equMatrix(1, 23) shouldBe 1
            resolver.equMatrix(2, 24) shouldBe 1
            resolver.equMatrix(3, 25) shouldBe 1
            resolver.equMatrix(4, 26) shouldBe 1
            resolver.equMatrix(5, 27) shouldBe 1
            resolver.equMatrix(6, 28) shouldBe 1
            resolver.equMatrix(7, 29) shouldBe 1
            resolver.equMatrix(8, 30) shouldBe 1
            resolver.equMatrix(9, 31) shouldBe 1
            resolver.equMatrix(10, 32) shouldBe 1
          }
      }
  }

  property("resolver equVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.equVector shouldBe DenseVector.ones[Double](NoSuppliers)
          }
      }
  }

  property("resolver fMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.fMatrix should have('rows(NoProducts))
            resolver.fMatrix should have('cols(NoProducts))

            resolver.fMatrix(0, 0) shouldBe 1
            resolver.fMatrix(6, 0) shouldBe -2
          }
      }
  }

  property("resolver geMatrix") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.geMatrix should have('rows(NoProducts + NoVars))
            resolver.geMatrix should have('cols(NoVars))

            resolver.geMatrix(0, 0) shouldBe 1
            resolver.geMatrix(6, 0) shouldBe -2
          }
      }
  }

  property("resolver geVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.geVector shouldBe DenseVector.zeros[Double](NoProducts + NoVars)
          }
      }
  }

  property("resolver xVector") {
    forAll(
      (Gen.const(buildResolver), "resolver")) {
        resolver =>
          {
            resolver.xVector.length shouldBe NoVars

            resolver.xVector(0) shouldBe 0.00000 +- Epsilon
            resolver.xVector(1) shouldBe 0.00009 +- Epsilon
            resolver.xVector(2) shouldBe 0.00000 +- Epsilon
            resolver.xVector(3) shouldBe 0.00640 +- Epsilon
            resolver.xVector(4) shouldBe 0.00000 +- Epsilon
            resolver.xVector(5) shouldBe 0.00000 +- Epsilon
            resolver.xVector(6) shouldBe 0.00017 +- Epsilon
            resolver.xVector(7) shouldBe 0.00000 +- Epsilon
            resolver.xVector(8) shouldBe 0.00036 +- Epsilon
            resolver.xVector(9) shouldBe 0.00333 +- Epsilon
            resolver.xVector(10) shouldBe 0.00003 +- Epsilon
            resolver.xVector(11) shouldBe 0.00028 +- Epsilon
            resolver.xVector(12) shouldBe 0.00000 +- Epsilon
            resolver.xVector(13) shouldBe 0.00002 +- Epsilon
            resolver.xVector(14) shouldBe 0.00004 +- Epsilon
            resolver.xVector(15) shouldBe 0.00417 +- Epsilon
            resolver.xVector(16) shouldBe 0.00000 +- Epsilon
            resolver.xVector(17) shouldBe 0.00083 +- Epsilon
            resolver.xVector(18) shouldBe 0.00028 +- Epsilon
            resolver.xVector(19) shouldBe 0.00000 +- Epsilon
            resolver.xVector(20) shouldBe 0.00006 +- Epsilon
            resolver.xVector(21) shouldBe 0.00167 +- Epsilon
            resolver.xVector(22) shouldBe 0.85000 +- Epsilon
            resolver.xVector(23) shouldBe 0.00000 +- Epsilon
            resolver.xVector(24) shouldBe 0.00000 +- Epsilon
            resolver.xVector(25) shouldBe 0.00000 +- Epsilon
            resolver.xVector(26) shouldBe 0.00000 +- Epsilon
            resolver.xVector(27) shouldBe 0.76000 +- Epsilon
            resolver.xVector(28) shouldBe 0.00000 +- Epsilon
            resolver.xVector(29) shouldBe 0.40000 +- Epsilon
            resolver.xVector(30) shouldBe 0.00000 +- Epsilon
            resolver.xVector(31) shouldBe 1.00000 +- Epsilon
            resolver.xVector(32) shouldBe 1.00000 +- Epsilon
          }
      }
  }
}
