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
// copies of the Software, and to permit pesursons to whom the
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

class SupplyChainConfTest extends PropSpec with PropertyChecks with Matchers {

  property("config") {
    forAll(
      (Gen.const("/hayday.yaml"), "confFile")) {
        confFile =>
          {
            val chain = SupplyChain.fromClasspath("/chain.yaml")
            val values = Parameters.fromClasspath("/max-values.yaml")
            val suppliers = Parameters.fromClasspath("/base-config.yaml").
              map(a => a._1 -> a._2.toInt)
            chain should have size 22
            values should not be empty
            suppliers should not be empty

            val conf = new Resolver(chain, suppliers, values).resolve
            val prod = conf.productions
            prod should not be empty

            prod.get("grano").map(_.fixed) should contain(7)
            prod.get("mais").map(_.fixed) should contain(1)
            prod.get("latte").map(_.fixed) should contain(3)
            prod.get("pancetta").map(_.fixed) should contain(1)
            prod.get("pane").map(_.fixed) should contain(1)
            prod.get("panna").map(_.fixed) should contain(1)
            prod.get("pasticcio_carote").map(_.fixed) should contain(1)
            prod.get("uova").map(_.fixed) should contain(6)

            val cons = conf.consumptions
            cons should not be empty
            cons should contain("" -> 0)
          }
      }
  }
}
