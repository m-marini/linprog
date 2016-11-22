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
import net.jcazevedo.moultingyaml.YamlArray

/**
 * A product which defines how to be produced
 *
 *  @constructor create a rule with production parameters
 *  @param name the product name
 *  @param producer the producer name
 *  @param quantity the produced quantity during a time interval
 *  @param time the time interval of slot production
 *  @param consumptions the quantities of products consumed to produce the named product
 *
 *  @author us00852
 */
case class Product(
  name: String,
  producer: String,
  quantity: Double,
  time: Duration,
  consumptions: Map[String, Double])

/**
 * A builder of rule from Yaml AST object
 * @param name the name of product
 * @param yaml the Yaml AST object
 */
class ProductBuilder(name: String, yaml: YamlObject) {

  /** Builds the rule */
  def build: Product =
    Product(
      name = name,
      producer = producer,
      quantity = quantity,
      time = interval,
      consumptions = consumptions)

  /** Extracts the producer name from yaml */
  private def producer = yaml.fields.get(YamlString("producer")) match {
    case Some(YamlString(x)) => x
    case None => deserializationError(s"missing producer for product $name")
    case _ => deserializationError(s"wrong producer type for product $name")
  }

  /** Extracts the product quantity from yaml */
  private def quantity = yaml.fields.get(YamlString("quantity")) match {
    case Some(YamlNumber(x: Number)) => x.doubleValue()
    case None => deserializationError(s"missing quantity for product $name")
    case _ => deserializationError(s"wrong quantity type for product $name")
  }

  /** Extracts the product interval from yaml */
  private def interval = yaml.fields.get(YamlString("interval")) match {
    case Some(YamlString(x)) => Duration(x)
    case None => deserializationError(s"missing interval for product $name")
    case _ => deserializationError(s"wrong interval type for product $name")
  }

  /** Extracts the product consumptions from yaml */
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
