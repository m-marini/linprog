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

import scala.annotation.migration
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.mmarini.linprog.Parameters
import org.mmarini.linprog.Product
import org.mmarini.linprog.Resolver
import org.mmarini.linprog.SupplyChain
import org.mmarini.linprog.SupplyChainConf

import javax.inject.Singleton
import javax.inject.Inject

/**
 *
 */
@Singleton
class FarmerRepository @Inject() (store: JdbcFarmerStore) {

  def chain(level: Int): Map[String, Product] = SupplyChain.fromClasspath(s"/chains/chain-$level.yaml")

  /** */
  def createSupplierMap(id: String)(implicit ec: ExecutionContext): Future[Option[SupplyChainConf]] =
    for {
      farmerOpt <- retrieveById(id)
    } yield for {
      farmer <- farmerOpt
    } yield {
      new Resolver(chain(farmer.level), farmer.suppliers, farmer.values).resolve
    }

  /** Creates a Farmer from a template name */
  def build(template: String, level: Int)(implicit ec: ExecutionContext): Future[Option[Farmer]] =
    Future.successful {
      val ch = chain(level)
      val values = Parameters.fromClasspath("/chains/max-values.yaml").filter(item => ch.contains(item._1))
      val suppliers = Parameters.
        fromClasspath(s"/chains/config-$level.yaml").
        map { case (k, v) => k -> v.toInt }
      val y = ch.values.toList.
        map(x => x.producer).toSet
      Option(Farmer(
        id = java.util.UUID.randomUUID.toString,
        level = level,
        name = "Default",
        suppliers = suppliers,
        values = values))
    }

  /**  */
  def put(farmer: Farmer): Future[(Farmer, Boolean)] = store.put(farmer)

  /**  */
  def delete(id: String): Future[Option[Farmer]] = store.delete(id)

  /** */
  def retrieveById(id: String): Future[Option[Farmer]] = store.retrieveById(id)

  /**  */
  def retrieveByName(name: String): Future[Seq[Farmer]] = store.retrieveByName(name)
}
