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

import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks
import org.scalatest.PropSpec
import org.scalacheck.Gen
import net.jcazevedo.moultingyaml.PimpedString
import net.jcazevedo.moultingyaml.YamlObject
import net.jcazevedo.moultingyaml.YamlString
import net.jcazevedo.moultingyaml.deserializationError
import scalax.file.Path
import scalax.io.Codec
import scalax.io.Resource
import net.jcazevedo.moultingyaml.YamlArray
import net.jcazevedo.moultingyaml.YamlNumber
import scala.concurrent.duration._
import scala.concurrent.duration.DurationInt

class RuleTest extends PropSpec with PropertyChecks with Matchers {

  property("valid rule") {
    val text = """
value: 3.6
quantity: 2
interval: 2 min
consumptions:
  grano: 1
producer: campo
"""

    forAll(
      (Gen.const(text), "confFile")) {
        confFile =>
          {
            val confYaml = confFile.parseYaml.asYamlObject

            val rule = Rule("grano", confYaml)

            rule should have('name("grano"))
            rule should have('value(3.6))
            rule should have('quantity(2))
            rule should have('time(2 minutes))
            rule should have('productivity(2.0 / (2 * 60)))
            rule should have('producer("campo"))
            rule.incomingFlow shouldBe (3.6 * 2.0 / (2.0 * 60)) +- 1e-3

            val consump = rule.consumptions

            consump should have size 1
            consump should contain("grano" -> 1.0)
          }
      }
  }
}
