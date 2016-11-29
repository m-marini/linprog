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

package org.mmarini.linprog.restapi.v1

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.mmarini.linprog.Parameters
import org.mmarini.linprog.SupplyChain
import org.mmarini.linprog.SupplyChainConf
import org.mmarini.linprog.ProductConf


import javax.inject.Singleton

/**
 *
 */
@Singleton
class FarmerRepository {

  private var store: Map[String, Farmer] = Map()
  private val chain = SupplyChain.fromClasspath("/chain.yaml")
  private val templates = Map(
    "base" -> ((
      Parameters.fromClasspath("/max-values.yaml"),
      Parameters.fromClasspath("/base-config.yaml").map { case (k, v) => (k, v.toInt) })))

  def createSupplierMap(id: String)(implicit ec: ExecutionContext): Future[Option[SupplyChainConf]] =
    for {
      farmerOpt <- retrieveById(id)
    } yield for {
      farmer <- farmerOpt
    } yield {
      val sup = Map("grano" -> ProductConf("grano", "campo", 3, 1),
        "mais" -> ProductConf("mais", "campo", 4, 0),
        "carote" -> ProductConf("carote", "campo", 0, 2))
      SupplyChainConf(
        productions = sup,
        consumptions = Map("grano" -> 10))
    }

  /** Creates a Farmer from a template name */
  def build(template: String)(implicit ec: ExecutionContext): Future[Option[Farmer]] =
    Future.successful {
      for {
        (values, suppliers) <- templates.get(template)
      } yield {
        val y = chain.values.toList.
          map(x => x.producer).toSet
        Farmer(
          id = java.util.UUID.randomUUID.toString,
          name = "Default",
          suppliers = suppliers,
          values = values)
      }
    }

  /**  */
  def put(farmer: Farmer): Future[(Farmer, Boolean)] = Future.successful {
    // TODO Access MUST be serialized
    val created = !store.contains(farmer.id)
    store = store + (farmer.id -> farmer)
    (farmer, created)
  }

  /**  */
  def delete(id: String): Future[Option[Farmer]] = Future.successful {
    // TODO Access MUST be serialized
    val ret = store.get(id)
    store = store - id
    ret
  }

  /** */
  def retrieveById(id: String): Future[Option[Farmer]] =
    Future.successful { store.get(id) }

  /**  */
  def retrieveByName(name: String): Future[Seq[Farmer]] = Future.successful {
    store.values.filter(_.name == name).toSeq
  }
}
