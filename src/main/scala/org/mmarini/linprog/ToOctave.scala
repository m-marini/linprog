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

class ToOctave(model: SupplyChainModel) {
  private val supplierNames = model.producers.keySet.toList.sorted
  private val productNames = model.rules.keySet.toList.sorted

  /**
   * Convert the supply chain model to octave source
   */
  override def toString: String =
    s"""$counters
$supplierIndexes
$productIndexes
$supplierByProduct
$noSuppliers
$quantities
$values
$intervals
$consumptions
"""

  val counters = s"""
noProducts = ${productNames.length};
noSuppliers = ${supplierNames.length};"""

  val supplierIndexes = """
# Supplier indexes
""" +
    (for {
      (name, idx) <- supplierNames.zipWithIndex
    } yield s"supplier_$name = ${idx + 1};").mkString("\n")

  val productIndexes: String = """
# Product indexes
""" +
    (for {
      (name, idx) <- productNames.zipWithIndex
    } yield s"product_$name = ${idx + 1};").mkString("\n")

  val supplierByProduct = """
# Supplier by product
S = zeros(noProducts, 1);
""" + (for {
    name <- productNames
  } yield s"S(product_$name) = supplier_${model.rules(name).producer};").mkString("\n")

  val noSuppliers = """
# No of suppliers
N = zeros(noSuppliers, 1);
""" + (for {
    name <- supplierNames
  } yield s"N(supplier_$name) = ${model.producers(name)};").mkString("\n")

  val quantities = """
# Quantity of products by supplier
Q = zeros(noProducts, 1);
""" + (for {
    name <- productNames
  } yield s"Q(product_$name) = ${model.rules(name).quantity};").mkString("\n")

  val values = """
# Value of products
V = zeros(noProducts, 1);
""" + (for {
    name <- productNames
  } yield s"V(product_$name) = ${model.rules(name).value};").mkString("\n")

  val consumptions = """
# Consumptions of product by product
C = zeros(noProducts, noProducts);
""" + (for {
    name <- productNames
    consName <- productNames
    qta <- model.rules(name).consumptions.get(consName)
  } yield s"C(product_$name, product_$consName) = $qta;").mkString("\n")

  val intervals = """
# Interval for product by supplier
T = zeros(noProducts, 1);
""" + (for {
    name <- productNames
  } yield s"T(product_$name) = ${model.rules(name).time.toSeconds};").mkString("\n")
}
