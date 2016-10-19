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

import net.jcazevedo.moultingyaml.PimpedString
import net.jcazevedo.moultingyaml.YamlNumber
import net.jcazevedo.moultingyaml.YamlObject
import net.jcazevedo.moultingyaml.YamlString
import net.jcazevedo.moultingyaml.YamlValue
import net.jcazevedo.moultingyaml.deserializationError
import scalax.io.Codec
import scalax.io.Resource

case class SupplyChainModel(rules: Map[String, Rule], producers: Map[String, Double]) {
}

object SupplyChainModel {
  def apply(yaml: YamlValue): SupplyChainModel =
    yaml match {
      case x: YamlObject => new SupplyChainModelBuilder(x).build
      case _ => deserializationError("wrong supply chain model")
    }

  def load(file: String): SupplyChainModel =
    apply(Resource.fromFile(file).string(Codec.UTF8).parseYaml)
}

class SupplyChainModelBuilder(yaml: YamlObject) {
  def build: SupplyChainModel = {
    validate
    SupplyChainModel(rules, producers)
  }

  private lazy val producers: Map[String, Double] = {
    val producersYaml = yaml.fields.get(YamlString("producers")) match {
      case Some(YamlObject(map)) => map
      case None => deserializationError("missing producers")
      case _ => deserializationError("wrong producers")
    }
    producersYaml.map {
      case (YamlString(key), YamlNumber(value: Number)) => (key -> value.doubleValue)
      case (key, _) => deserializationError(s"wrong producer constraint $key")
    }
  }

  private lazy val rules: Map[String, Rule] = {
    val rulesYaml = yaml.fields.get(YamlString("rules")) match {
      case Some(YamlObject(map)) => map
      case None => deserializationError("missing rules")
      case _ => deserializationError("wrong rules")
    }

    rulesYaml.map {
      case (YamlString(key), ruleYaml: YamlObject) => (key -> Rule(key, ruleYaml))
      case (key, _) => deserializationError(s"wrong rules $key")
    }
  }

  private def validate() {
    for {
      (product, rule) <- rules
      producer = rule.producer
    } {
      if (!producers.contains(producer)) {
        deserializationError(s"Missing producer ${producer} to produce $product")
      }
      for {
        consumedProduct <- rule.consumptions.keys
      } {
        if (!rules.contains(consumedProduct)) {
          deserializationError(s"Missing product ${consumedProduct} to produce $product")
        }
      }
    }
  }
}
