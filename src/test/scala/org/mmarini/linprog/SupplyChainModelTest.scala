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

import scala.concurrent.duration.DurationInt

import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks

import net.jcazevedo.moultingyaml.PimpedString
import net.jcazevedo.moultingyaml.deserializationError
import net.jcazevedo.moultingyaml.`package`.DeserializationException

class SupplyChainModelTest extends PropSpec with PropertyChecks with Matchers {
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
