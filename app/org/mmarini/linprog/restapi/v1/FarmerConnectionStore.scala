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

import java.sql.Connection
import java.sql.ResultSet

import scala.annotation.tailrec

import com.typesafe.scalalogging.LazyLogging

/**
 *
 */
class FarmerConnectionStore(val conn: Connection) extends LazyLogging {

  def configureDatabase: FarmerConnectionStore =
    if (!checkForDatabase) {
      createTables
    } else {
      this
    }

  private def createTables: FarmerConnectionStore = {
    val s = conn.createStatement
    s.executeUpdate("""
CREATE TABLE IF NOT EXISTS Farmers (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  token VARCHAR(255) NOT NULL,
  refreshToken VARCHAR(255) NOT NULL,
  level INTEGER NOT NULL
)""")
    s.executeUpdate("""
CREATE TABLE IF NOT EXISTS Prices (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  farmerId VARCHAR(36) NOT NULL,
  name VARCHAR(255) NOT NULL,
  price DECIMAL NOT NULL
)""")
    s.executeUpdate("""
CREATE TABLE IF NOT EXISTS Suppliers (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  farmerId VARCHAR(36) NOT NULL,
  name VARCHAR(255) NOT NULL,
  quantity INTEGER NOT NULL
)""")
    this
  }

  private def checkForDatabase: Boolean = {
    @tailrec
    def readResultSet(list: Seq[(String, String)], rs: ResultSet): Seq[(String, String)] = {
      if (!rs.next()) {
        list
      } else {
        val rec = (rs.getString(1), rs.getString(2))
        val l1 = list :+ rec
        readResultSet(l1, rs)
      }
    }

    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SHOW TABLES")
    //      val meta = rs.getMetaData
    //      for { i <- 1 to meta.getColumnCount } {
    //        meta.getColumnName(i)
    //      }
    val list = readResultSet(Seq(), rs)
    val ok = list.exists { case (_, name) => name == "Farmers" }
    ok
  }

  /**  */
  def put(farmer: Farmer): (Farmer, Boolean) = {
    val ret = updateFarmer(farmer)
    if (ret == 0) {
      insertFarmer(farmer, "", "")
      insertSuppliers(farmer)
      insertValues(farmer)
      (farmer, true)
    } else {
      deleteSuppliers(farmer.id)
      deleteValues(farmer.id)
      insertSuppliers(farmer)
      insertValues(farmer)
      (farmer, false)
    }
  }

  /**  */
  def delete(id: String): Option[Farmer] = {
    val old = selectFarmerById(id)
    deleteSuppliers(id)
    deleteValues(id)
    deleteFarmer(id)
    old
  }

  /** */
  def retrieveById(id: String): Option[Farmer] = selectFarmerById(id)

  /**  */
  def retrieveByName(name: String): Seq[Farmer] = selectFarmersByName(name)

  private def selectFarmerById(id: String): Option[Farmer] = {
    val stmt = conn.prepareStatement("""
SELECT
 name,
 level
FROM Farmers
WHERE
 id=?""")
    stmt.setString(1, id)
    val rs = stmt.executeQuery()
    if (rs.next()) {
      val name = rs.getString("name")
      val level = rs.getInt("level")
      val farmer = Farmer(
        id = id,
        level = level,
        name = name,
        suppliers = selectSuppliers(id),
        values = selectValues(id))
      Some(farmer)
    } else {
      None
    }
  }

  private def selectFarmersByName(name: String): Seq[Farmer] = {
    @tailrec
    def readResultSet(list: Seq[Farmer], rs: ResultSet): Seq[Farmer] =
      if (!rs.next()) {
        list
      } else {
        val id = rs.getString("id")
        val level = rs.getInt("level")

        val farmer = Farmer(
          id = id,
          level = level,
          name = name,
          suppliers = selectSuppliers(id),
          values = selectValues(id))
        readResultSet(list :+ farmer, rs)
      }

    val stmt = conn.prepareStatement("""
SELECT
 id,
 level
FROM Farmers
WHERE
 name=?""")
    stmt.setString(1, name)
    val rs = stmt.executeQuery()
    readResultSet(Seq(), rs)
  }

  private def selectValues(farmerId: String): Map[String, Double] = {
    @tailrec
    def readResultSet(map: Map[String, Double], rs: ResultSet): Map[String, Double] =
      if (!rs.next()) {
        map
      } else {
        val name = rs.getString("name")
        val price = rs.getDouble("price")

        readResultSet(map + (name -> price), rs)
      }

    val stmt = conn.prepareStatement("""
SELECT
 name,
 price
FROM Prices
WHERE
 farmerId=?""")
    stmt.setString(1, farmerId)
    val rs = stmt.executeQuery()
    readResultSet(Map(), rs)
  }

  private def selectSuppliers(farmerId: String): Map[String, Int] = {
    @tailrec
    def readResultSet(map: Map[String, Int], rs: ResultSet): Map[String, Int] =
      if (!rs.next()) {
        map
      } else {
        val name = rs.getString("name")
        val qty = rs.getInt("quantity")

        readResultSet(map + (name -> qty), rs)
      }

    val stmt = conn.prepareStatement("""
SELECT
 name,
 quantity
FROM Suppliers
WHERE
 farmerId=?""")
    stmt.setString(1, farmerId)
    val rs = stmt.executeQuery()
    readResultSet(Map(), rs)
  }

  private def deleteFarmer(id: String) {
    val stmt = conn.prepareStatement("DELETE FROM Farmers WHERE id=?")
    stmt.setString(1, id)
    stmt.executeUpdate()
  }

  private def deleteValues(farmerId: String) {
    val stmt = conn.prepareStatement("DELETE FROM Prices WHERE farmerId=?")
    stmt.setString(1, farmerId)
    stmt.executeUpdate()
  }

  private def insertValues(farmer: Farmer) {
    val ValueParmIdx = 4
    val stmt = conn.prepareStatement("INSERT INTO Prices (id,farmerId,name,price) VALUES(?,?,?,?)")
    for {
      (name, value) <- farmer.values
    } {
      stmt.setString(1, java.util.UUID.randomUUID.toString)
      stmt.setString(2, farmer.id)
      stmt.setString(3, name)
      stmt.setDouble(ValueParmIdx, value)
      stmt.executeUpdate()
    }
  }

  private def deleteSuppliers(farmerId: String) {
    val stmt = conn.prepareStatement("DELETE FROM Suppliers WHERE farmerId=?")
    stmt.setString(1, farmerId)
    stmt.executeUpdate()
  }

  private def insertSuppliers(farmer: Farmer) {
    val ValueParmIdx = 4
    val stmt = conn.prepareStatement("INSERT INTO Suppliers (id,farmerId,name,quantity) VALUES(?,?,?,?)")
    for {
      (name, value) <- farmer.suppliers
    } {
      stmt.setString(1, java.util.UUID.randomUUID.toString)
      stmt.setString(2, farmer.id)
      stmt.setString(3, name)
      stmt.setInt(ValueParmIdx, value)
      stmt.executeUpdate()
    }
  }

  private def insertFarmer(farmer: Farmer, token: String, refreshToken: String) {
    val TokenParmIdx = 4
    val RefreshTokenParmIdx = 5
    val stmt = conn.prepareStatement("INSERT INTO Farmers (id,name,level,token,refreshToken) VALUES(?,?,?,?,?)")
    stmt.setString(1, farmer.id)
    stmt.setString(2, farmer.name)
    stmt.setInt(3, farmer.level)
    stmt.setString(TokenParmIdx, token)
    stmt.setString(RefreshTokenParmIdx, refreshToken)
    val n = stmt.executeUpdate()
  }

  private def updateFarmer(farmer: Farmer): Int = {
    val stmt = conn.prepareStatement("UPDATE Farmers SET name=?,level=? WHERE id=?")
    stmt.setString(1, farmer.name)
    stmt.setInt(2, farmer.level)
    stmt.setString(3, farmer.id)
    val n = stmt.executeUpdate()
    n
  }
}
