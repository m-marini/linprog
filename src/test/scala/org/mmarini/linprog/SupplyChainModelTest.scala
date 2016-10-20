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

class SupplyChainModelTest extends PropSpec with PropertyChecks with Matchers {
  val Nanos = 1e-9
  val MaxInterval = 10

  property("supply chain outcomes") {

    def rule(name: String): Gen[Rule] =
      for {
        quantity <- Gen.choose(1.0, 10.0)
        value <- Gen.choose(0.0, 10.0)
        interval <- Gen.choose(1, MaxInterval)
      } yield Rule(
        name = name,
        producer = "campo",
        quantity = quantity,
        value = value,
        time = interval seconds,
        consumptions = Map())

    forAll(
      (rule("grano"), "grano"),
      (rule("mais"), "mais"),

      (Gen.choose(0.0, 1.0), "granoUsage"),
      (Gen.choose(0.0, 1.0), "maisUsage"),
      (Gen.choose(1.0, 10.0), "campoProducers")) {
        (grano, mais, granoUsage, maisUsage, campoProducers) =>
          {
            val rules = Map(
              "grano" -> grano,
              "mais" -> mais)

            val producers = Map("campo" -> campoProducers)

            val chain = SupplyChainModel(rules, producers)

            val config = Map(
              ("campo", "grano") -> granoUsage,
              ("campo", "mais") -> maisUsage)

            val map = chain.computeOutcomes(config)

            map should have size 2

            val granoOutcome = map.get("grano")

            granoOutcome.value should have('name("grano"))

            val interval = (granoUsage * grano.time + maisUsage * mais.time).toNanos * Nanos

            granoOutcome.value.quantityFlow shouldBe (campoProducers * grano.quantity * granoUsage / interval) +- 1e-6

            granoOutcome.value.valueFlow shouldBe (grano.value * campoProducers * grano.quantity * granoUsage / interval) +- 1e-6

            val maisOutcome = map.get("mais")

            maisOutcome.value should have('name("mais"))

            maisOutcome.value.quantityFlow shouldBe (campoProducers * mais.quantity * maisUsage / interval) +- 1e-6

            maisOutcome.value.valueFlow shouldBe (mais.value * campoProducers * mais.quantity * maisUsage / interval) +- 1e-6

          }
      }
  }

  property("valid supply chain model") {
    val text = """
producers:
  campo: 10
rules:
  grano:
    value: 3.6
    quantity: 2
    interval: 2 min
    consumptions:
      grano: 1
    producer: campo
"""

    forAll(
      (Gen.const(text), "text")) {
        text =>
          {
            val chain = SupplyChainModel(text.parseYaml)

            chain.producers should have size 1
            chain.rules should have size 1
          }
      }
  }

  property("missing producer supply chain model") {
    val text = """
producers:
  gallina: 10
rules:
  grano:
    value: 3.6
    quantity: 2
    interval: 2 min
    consumptions:
      grano: 1
    producer: campo
"""

    forAll(
      (Gen.const(text), "text")) {
        text =>
          {
            the[DeserializationException] thrownBy SupplyChainModel(text.parseYaml) should have message (
              "Missing producer campo to produce grano")
          }
      }
  }

  property("missing consumption supply chain model") {
    val text = """
producers:
  campo: 10
rules:
  grano:
    value: 3.6
    quantity: 2
    interval: 2 min
    consumptions:
      acqua: 1
    producer: campo
"""

    forAll(
      (Gen.const(text), "text")) {
        text =>
          {
            the[DeserializationException] thrownBy SupplyChainModel(text.parseYaml) should have message (
              "Missing product acqua to produce grano")
          }
      }
  }

  property("file supply chain model") {
    forAll(
      (Gen.const("docs/hayday.yaml"), "confFile")) {
        confFile =>
          {
            val chain = SupplyChainModel.load(confFile)

            chain.producers should have size 6
            chain.rules should have size 12
          }
      }
  }
}
