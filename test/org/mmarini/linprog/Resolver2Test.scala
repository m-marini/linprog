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
import breeze.linalg.csvwrite
import java.io.File
import breeze.stats.distributions.RandBasis
import org.apache.commons.math3.random.MersenneTwister

class Resolver2Test extends PropSpec with PropertyChecks with Matchers {
  val NoProducts = 4
  val NoSuppliers = 3
  val NoVars = 7
  val Epsilon = 1e-4

  def buildResolverSimple(seed: Long): Resolver = buildResolver("/chain-simple.yaml",
    "/max-values.yaml", "/simple-config.yaml", seed)

  /** Builds a resolver */
  def buildResolver(chainName: String,
    valuesName: String,
    confName: String,
    seed: Long): Resolver = {
    val chain = SupplyChain.fromClasspath(chainName)
    val values = Parameters.fromClasspath(valuesName)
    val suppliers = Parameters.fromClasspath(confName).
      map(a => a._1 -> a._2.toInt)
    val resolver = new Resolver(chain, suppliers, values)(new RandBasis(new MersenneTwister(seed)))
    resolver
  }

  def assertLike(x: DenseVector[Double], expected: Double*)(implicit epsilon: Double = Epsilon) {
    x.length shouldBe expected.length
    x.toArray.zip(expected).foreach { case (x, exp) => x shouldBe exp +- epsilon }
  }

  val seedGen = Gen.choose(0L, 10000L)

  property("counter") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.noProducts shouldBe NoProducts
      resolver.noSuppliers shouldBe NoSuppliers
      resolver.noVars shouldBe NoVars
    }
  }

  property("vVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.vVector.length shouldBe NoProducts

      resolver.vVector(0) shouldBe 3.6000 +- Epsilon
      resolver.vVector(1) shouldBe 7.2000 +- Epsilon
      resolver.vVector(2) shouldBe 7.2000 +- Epsilon
      resolver.vVector(3) shouldBe 18.0000 +- Epsilon
    }
  }

  property("qVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.qVector.length shouldBe NoProducts

      resolver.qVector(0) shouldBe 2.00000 +- Epsilon
      resolver.qVector(1) shouldBe 2.00000 +- Epsilon
      resolver.qVector(2) shouldBe 3.00000 +- Epsilon
      resolver.qVector(3) shouldBe 1.00000 +- Epsilon

      resolver.qMatrix shouldBe diag(resolver.qVector)

    }
  }

  property("nVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.nVector.length shouldBe NoSuppliers

      resolver.nVector(0) shouldBe 10.00000 +- Epsilon
      resolver.nVector(1) shouldBe 6.00000 +- Epsilon
      resolver.nVector(2) shouldBe 1.00000 +- Epsilon

      resolver.n1Vector.length shouldBe NoProducts

      resolver.n1Vector(0) shouldBe 10.00000 +- Epsilon
      resolver.n1Vector(1) shouldBe 10.00000 +- Epsilon
      resolver.n1Vector(2) shouldBe 1.00000 +- Epsilon
      resolver.n1Vector(3) shouldBe 6.00000 +- Epsilon

      resolver.nMatrix shouldBe diag(resolver.n1Vector)

    }
  }

  property("dMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.dMatrix should have('rows(NoProducts))
      resolver.dMatrix should have('cols(NoProducts))
    }
  }

  property("gVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.gVector.length shouldBe NoProducts

      resolver.gVector(0) shouldBe 36.0000 +- Epsilon
      resolver.gVector(1) shouldBe 72.0000 +- Epsilon
      resolver.gVector(2) shouldBe 7.2000 +- Epsilon
      resolver.gVector(3) shouldBe 64.8000 +- Epsilon
    }
  }

  property("minVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.minVector.length shouldBe NoVars

      resolver.minVector(0) shouldBe -36.00000 +- Epsilon
      resolver.minVector(1) shouldBe -72.00000 +- Epsilon
      resolver.minVector(2) shouldBe -7.20000 +- Epsilon
      resolver.minVector(3) shouldBe -64.80000 +- Epsilon
      resolver.minVector(4) shouldBe 0.00000 +- Epsilon
      resolver.minVector(5) shouldBe 0.00000 +- Epsilon
      resolver.minVector(6) shouldBe 0.00000 +- Epsilon
    }
  }

  property("thetaMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.thetaMatrix should have('rows(NoSuppliers))
      resolver.thetaMatrix should have('cols(NoProducts))

      resolver.thetaMatrix(0, 0) shouldBe 1
      resolver.thetaMatrix(0, 1) shouldBe 1
      resolver.thetaMatrix(2, 2) shouldBe 1
      resolver.thetaMatrix(1, 3) shouldBe 1
    }
  }

  property("tVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.tVector.length shouldBe NoProducts

      resolver.tVector(0) shouldBe 120.0 +- Epsilon
      resolver.tVector(1) shouldBe 300.0 +- Epsilon
      resolver.tVector(2) shouldBe 240.0 +- Epsilon
      resolver.tVector(3) shouldBe 600.0 +- Epsilon

      resolver.tMatrix shouldBe diag(resolver.tVector)

    }
  }

  property("uMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.uMatrix should have('rows(NoSuppliers))
      resolver.uMatrix should have('cols(NoProducts))

      resolver.uMatrix(0, 0) shouldBe 120.0 +- Epsilon
      resolver.uMatrix(0, 1) shouldBe 300.0 +- Epsilon
      resolver.uMatrix(2, 2) shouldBe 240.0 +- Epsilon
      resolver.uMatrix(1, 3) shouldBe 600.0 +- Epsilon
    }
  }

  property("equMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.equMatrix should have('rows(NoSuppliers))
      resolver.equMatrix should have('cols(NoVars))

      resolver.equMatrix(0, 0) shouldBe 120.0 +- Epsilon
      resolver.equMatrix(0, 1) shouldBe 300.0 +- Epsilon
      resolver.equMatrix(2, 2) shouldBe 240.0 +- Epsilon
      resolver.equMatrix(1, 3) shouldBe 600.0 +- Epsilon
      resolver.equMatrix(0, 4) shouldBe 1
      resolver.equMatrix(1, 5) shouldBe 1
      resolver.equMatrix(2, 6) shouldBe 1
    }
  }

  property("equVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.equVector shouldBe DenseVector.ones[Double](NoSuppliers)
    }
  }

  property("fMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.fMatrix should have('rows(NoProducts))
      resolver.fMatrix should have('cols(NoProducts))

      resolver.fMatrix(0, 0) shouldBe 10.0 +- Epsilon
      resolver.fMatrix(0, 1) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(0, 2) shouldBe -2.0 +- Epsilon
      resolver.fMatrix(0, 3) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(1, 0) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(1, 1) shouldBe 10.0 +- Epsilon
      resolver.fMatrix(1, 2) shouldBe -1.0 +- Epsilon
      resolver.fMatrix(1, 3) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(2, 0) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(2, 1) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(2, 2) shouldBe 3.0 +- Epsilon
      resolver.fMatrix(2, 3) shouldBe -6.0 +- Epsilon
      resolver.fMatrix(3, 0) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(3, 1) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(3, 2) shouldBe 0.0 +- Epsilon
      resolver.fMatrix(3, 3) shouldBe 6.0 +- Epsilon
    }
  }

  property("geMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.geMatrix should have('rows(NoProducts + NoVars))
      resolver.geMatrix should have('cols(NoVars))

      resolver.geMatrix(0, 0) shouldBe 10.0 +- Epsilon
      resolver.geMatrix(0, 1) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(0, 2) shouldBe -2.0 +- Epsilon
      resolver.geMatrix(0, 3) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(1, 0) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(1, 1) shouldBe 10.0 +- Epsilon
      resolver.geMatrix(1, 2) shouldBe -1.0 +- Epsilon
      resolver.geMatrix(1, 3) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(2, 0) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(2, 1) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(2, 2) shouldBe 3.0 +- Epsilon
      resolver.geMatrix(2, 3) shouldBe -6.0 +- Epsilon
      resolver.geMatrix(3, 0) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(3, 1) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(3, 2) shouldBe 0.0 +- Epsilon
      resolver.geMatrix(3, 3) shouldBe 6.0 +- Epsilon
    }
  }

  property("geVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.geVector.length shouldBe (NoProducts + NoVars)
      for { i <- 0 until NoProducts + NoVars } {
        resolver.geVector(i) shouldBe 0.0
      }
    }
  }

  property("xVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      resolver.xVector.length shouldBe NoVars

      csvwrite(new File("x.csv"), resolver.xVector.toDenseMatrix)

      resolver.xVector(0) shouldBe 0.0072917 +- Epsilon
      resolver.xVector(1) shouldBe 0.0004167 +- Epsilon
      resolver.xVector(2) shouldBe 0.0041667 +- Epsilon
      resolver.xVector(3) shouldBe 0.0016667 +- Epsilon
      resolver.xVector(4) shouldBe 0.0000000 +- Epsilon
      resolver.xVector(5) shouldBe 0.0000000 +- Epsilon
      resolver.xVector(6) shouldBe 0.0000000 +- Epsilon
    }
  }

  property("== constraints") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.equMatrix * resolver.xVector

      c.length shouldBe (NoSuppliers)

      c(0) shouldBe (resolver.equVector(0)) +- Epsilon
      c(1) shouldBe (resolver.equVector(1)) +- Epsilon
      c(2) shouldBe (resolver.equVector(2)) +- Epsilon
    }
  }

  property(">= constraints") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.geMatrix * resolver.xVector

      c.length shouldBe (NoProducts + NoVars)

      c(0) shouldBe >=(resolver.geVector(0) - Epsilon)
      c(1) shouldBe >=(resolver.geVector(1) - Epsilon)
      c(2) shouldBe >=(resolver.geVector(2) - Epsilon)
      c(3) shouldBe >=(resolver.geVector(3) - Epsilon)
      c(4) shouldBe >=(resolver.geVector(4) - Epsilon)
      c(5) shouldBe >=(resolver.geVector(5) - Epsilon)
      c(6) shouldBe >=(resolver.geVector(6) - Epsilon)
      c(7) shouldBe >=(resolver.geVector(7) - Epsilon)
      c(8) shouldBe >=(resolver.geVector(8) - Epsilon)
      c(9) shouldBe >=(resolver.geVector(9) - Epsilon)
      c(10) shouldBe >=(resolver.geVector(10) - Epsilon)
    }
  }

  property("wVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.wVector

      c.length shouldBe NoProducts
      c(0) shouldBe 7.2917e-03 +- Epsilon
      c(1) shouldBe 4.1667e-04 +- Epsilon
      c(2) shouldBe 4.1667e-03 +- Epsilon
      c(3) shouldBe 1.6667e-03 +- Epsilon
    }
  }

  property("zVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.zVector

      c.length shouldBe NoSuppliers
      c(0) shouldBe 0.0 +- Epsilon
      c(1) shouldBe 0.0 +- Epsilon
      c(2) shouldBe 0.0 +- Epsilon
    }
  }

  property("npVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.npVector

      c.length shouldBe NoProducts

      c(0) shouldBe 8.7500 +- Epsilon
      c(1) shouldBe 1.2500 +- Epsilon
      c(2) shouldBe 1.0000 +- Epsilon
      c(3) shouldBe 6.0000 +- Epsilon
    }
  }

  property("np1Vector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.np1Vector

      c.length shouldBe NoProducts

      c(0) shouldBe 8.000 +- Epsilon
      c(1) shouldBe 1.000 +- Epsilon
      c(2) shouldBe 1.000 +- Epsilon
      c(3) shouldBe 6.000 +- Epsilon
    }
  }

  property("rVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.rVector

      c.length shouldBe NoProducts

      c(0) shouldBe 0.750 +- Epsilon
      c(1) shouldBe 0.250 +- Epsilon
      c(2) shouldBe 0.000 +- Epsilon
      c(3) shouldBe 0.000 +- Epsilon
    }
  }

  property("resolve") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.resolve

      c.productions("grano") should have('fixed(8))
      c.productions("mais") should have('fixed(1))
      c.productions("mangime_galline") should have('fixed(1))
      c.productions("uova") should have('fixed(6))

      c.productions("grano").random should (be >= (0) and be <= (1))
      c.productions("mais").random should (be >= (0) and be <= (1))
      c.productions("mais") should have('fixed(1))
      c.productions("mangime_galline") should have('random(0))
      c.productions("uova") should have('random(0))

      //            c.consumptions should contain("grano" -> (2 + 8))
      //            c.consumptions should contain("mais" -> (1 + 1))
      //            c.consumptions should contain("mangime_galline" -> 6)
    }
  }

  property("r1Matrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.r1Vector

      c.length shouldBe NoSuppliers

      c(0) shouldBe 1.0 +- Epsilon
      c(1) shouldBe 0.0 +- Epsilon
      c(2) shouldBe 0.0 +- Epsilon
    }
  }

  property("totRndMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.totRndVector

      c.length shouldBe NoSuppliers

      c(0) shouldBe 1.0 +- Epsilon
      c(1) shouldBe 0.0 +- Epsilon
      c(2) shouldBe 0.0 +- Epsilon
    }
  }

  property("tot2RndMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.tot2RndVector

      c.length shouldBe NoSuppliers

      c(0) shouldBe 1.0 +- Epsilon
      c(1) shouldBe 1.0 +- Epsilon
      c(2) shouldBe 1.0 +- Epsilon
    }
  }

  property("tot3RndMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.tot3RndVector

      c.length shouldBe NoProducts

      c(0) shouldBe 1.0 +- Epsilon
      c(1) shouldBe 1.0 +- Epsilon
      c(3) shouldBe 1.0 +- Epsilon
      c(3) shouldBe 1.0 +- Epsilon
    }
  }

  property("probVector") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.probVector

      c.length shouldBe NoProducts

      c(0) shouldBe 0.75 +- Epsilon
      c(1) shouldBe 0.25 +- Epsilon
      c(3) shouldBe 0.0 +- Epsilon
      c(3) shouldBe 0.0 +- Epsilon
    }
  }

  property("probMatrix") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.probMatrix

      c.rows shouldBe NoSuppliers
      c.cols shouldBe NoProducts

      c(0, 0) shouldBe 0.75 +- Epsilon
      c(0, 1) shouldBe 0.25 +- Epsilon
      c(0, 2) shouldBe 0.0 +- Epsilon
      c(0, 3) shouldBe 0.0 +- Epsilon
      c(1, 0) shouldBe 0.0 +- Epsilon
      c(1, 1) shouldBe 0.0 +- Epsilon
      c(1, 2) shouldBe 0.0 +- Epsilon
      c(1, 3) shouldBe 0.0 +- Epsilon
      c(2, 0) shouldBe 0.0 +- Epsilon
      c(2, 1) shouldBe 0.0 +- Epsilon
      c(2, 2) shouldBe 0.0 +- Epsilon
      c(2, 3) shouldBe 0.0 +- Epsilon
    }
  }

  property("rndProduct") {
    forAll { (seed: Long) =>
      val resolver = buildResolverSimple(seed)
      val c = resolver.rndProducts

      c should have size (1)
    }
  }

  property("rndProduct check for total") {
    forAll { (seed: Long) =>
      val resolver = buildResolver(
        "/chain-15.yaml", "/max-values.yaml", "/config-15.yaml", seed)
      val c = resolver.resolve
      val x = c.productions.toSeq.
        map {
          case (k, v) => v.supplierName -> (v.fixed + v.random)
        }.groupBy(_._1).
        map { case (name, list) => name -> list.map(_._2).sum }

      for {
        (name, n) <- x
      } {
        n should be <= resolver.suppliers.getOrElse(name, 0)
      }
    }
  }
}