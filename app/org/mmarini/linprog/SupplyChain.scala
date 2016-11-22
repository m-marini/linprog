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

import net.jcazevedo.moultingyaml.PimpedString
import net.jcazevedo.moultingyaml.YamlObject
import net.jcazevedo.moultingyaml.YamlString
import net.jcazevedo.moultingyaml.YamlValue
import net.jcazevedo.moultingyaml.deserializationError
import scalax.io.Codec
import scalax.io.Resource

object SupplyChain {
  def apply(yaml: YamlValue): Map[String, Product] = new SupplyChainBuilder(yaml).build

  def load(filename: String): Map[String, Product] =
    apply(Resource.fromFile(filename).string(Codec.UTF8).parseYaml)
}

/**
 * A builder of rule from Yaml AST object
 * @param name the name of product
 * @param yaml the Yaml AST object
 */
class SupplyChainBuilder(yaml: YamlValue) {

  /** Builds the product map */
  def build: Map[String, Product] = {
    val productsYaml = yaml match {
      case YamlObject(map) => map
      case _ => deserializationError("wrong products object")
    }

    val chain = productsYaml.map {
      case (YamlString(key), productYaml: YamlObject) => (key -> new ProductBuilder(key, productYaml).build)
      case (key, _) => deserializationError(s"wrong product $key")
    }

    validate(chain)
  }

  private def validate(chain: Map[String, Product]): Map[String, Product] = {
    for {
      product <- chain.values
      consume <- product.consumptions.keys
      if (!chain.contains(consume))
    } {
      deserializationError(s"Missing product ${consume} to produce ${product.name}")
    }
    chain
  }
}
