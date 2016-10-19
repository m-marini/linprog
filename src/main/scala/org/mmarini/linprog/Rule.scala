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

import scala.concurrent.duration.Duration

import net.jcazevedo.moultingyaml.YamlNumber
import net.jcazevedo.moultingyaml.YamlObject
import net.jcazevedo.moultingyaml.YamlString
import net.jcazevedo.moultingyaml.deserializationError

case class Rule(
    name: String,
    producer: String,
    value: Double,
    quantity: Double,
    time: Duration,
    consumptions: Map[String, Double]) {

  val productivity: Double = quantity / time.toSeconds

  val incomingFlow: Double = productivity * value
}

object Rule {
  def apply(name: String, yaml: YamlObject): Rule =
    new RuleBuilder(name, yaml).build
}

class RuleBuilder(name: String, yaml: YamlObject) {

  def build: Rule =
    Rule(
      name = name,
      producer = producer,
      value = value,
      quantity = qty,
      time = elaps,
      consumptions = consumptions)

  private def producer = yaml.fields.get(YamlString("producer")) match {
    case Some(YamlString(x)) => x
    case None => deserializationError(s"missing producer for product $name")
    case _ => deserializationError(s"wrong producer type for product $name")
  }

  private def value = yaml.fields.get(YamlString("value")) match {
    case Some(YamlNumber(x: Number)) => x.doubleValue()
    case None => deserializationError(s"missing value for product $name")
    case _ => deserializationError(s"wrong value type for product $name")
  }

  private def qty = yaml.fields.get(YamlString("quantity")) match {
    case Some(YamlNumber(x: Number)) => x.doubleValue()
    case None => deserializationError(s"missing quantity for product $name")
    case _ => deserializationError(s"wrong quantity type for product $name")
  }

  private def elaps = yaml.fields.get(YamlString("interval")) match {
    case Some(YamlString(x)) => Duration(x)
    case None => deserializationError(s"missing interval for product $name")
    case _ => deserializationError(s"wrong interval type for product $name")
  }

  private def consumptions = {
    val consumptionsYaml = yaml.fields.get(YamlString("consumptions")) match {
      case Some(YamlObject(x)) => x
      case None => deserializationError(s"missing consumptions for product $name")
      case _ => deserializationError(s"wrong consumptions type for product $name")
    }
    consumptionsYaml map {
      case (YamlString(name), YamlNumber(x: Number)) => (name -> x.doubleValue())
      case (key, _) => deserializationError(s"wrong consumption $key for product $name")
    }
  }
}
