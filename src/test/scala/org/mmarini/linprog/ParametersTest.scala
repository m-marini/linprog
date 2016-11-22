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

class ParametersTest extends PropSpec with PropertyChecks with Matchers {

  property("valid parameters") {
    val text = """
grano: 2
mais: 3.4
"""

    forAll(
      (Gen.const(text), "text")) {
        text =>
          {
            val parms = Parameters(text.parseYaml)

            parms should have size 2
            parms should contain(("grano" -> 2.0))
            parms should contain(("mais" -> 3.4))
            parms("aaa") shouldBe 0.0
          }
      }
  }

  property("parameters file") {
    forAll(
      (Gen.const("src/test/resources//parameters.yaml"), "confFile")) {
        confFile =>
          {
            val parms = Parameters.load(confFile)

            parms should have size 2
            parms should contain(("grano" -> 2.0))
            parms should contain(("mais" -> 3.4))
          }
      }
  }
}
