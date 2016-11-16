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

class ToOctaveTest extends PropSpec with PropertyChecks with Matchers {

  property("valid rule") {
    val text = """
producers:
  campo: 10
  mangimificio: 2
rules:
  grano:
    value: 3.6
    quantity: 2
    interval: 5 min
    consumptions:
      grano: 1
    producer: campo
  mangime:
    value: 5
    quantity: 1
    interval: 1 hours
    consumptions:
      grano: 1
    producer: mangimificio
"""

    forAll(
      (Gen.const(text), "text")) {
        text =>
          {
            val chain = SupplyChainModel(text.parseYaml)

            val toOctave = new ToOctave(chain)

            toOctave.counters should include("""
noProducts = 2;
noSuppliers = 2;""")

            toOctave.supplierIndexes should include("""
# Supplier indexes
supplier_campo = 1;
supplier_mangimificio = 2;""")

            toOctave.productIndexes should include("""
# Product indexes
product_grano = 1;
product_mangime = 2;""")

            toOctave.supplierByProduct should include("""
# Supplier by product
S = zeros(noProducts, 1);
S(product_grano) = supplier_campo;
S(product_mangime) = supplier_mangimificio;""")

            toOctave.noSuppliers should include("""
# No of suppliers
N = zeros(noSuppliers, 1);
N(supplier_campo) = 10.0;
N(supplier_mangimificio) = 2.0;""")

            toOctave.quantities should include("""
# Quantity of products by supplier
Q = zeros(noProducts, 1);
Q(product_grano) = 2.0;
Q(product_mangime) = 1.0;""")

            toOctave.values should include("""
# Value of products
V = zeros(noProducts, 1);
V(product_grano) = 3.6;
V(product_mangime) = 5.0;""")

            toOctave.consumptions should include("""
# Consumptions of product by product
C = zeros(noProducts, noProducts);
C(product_grano, product_grano) = 1.0;
C(product_mangime, product_grano) = 1.0;""")

            toOctave.intervals should include("""
# Interval for product by supplier
T = zeros(noProducts, 1);
T(product_grano) = 300;
T(product_mangime) = 3600;""")

            toOctave.toString should include("""
noProducts = 2;
noSuppliers = 2;

# Supplier indexes
supplier_campo = 1;
supplier_mangimificio = 2;

# Product indexes
product_grano = 1;
product_mangime = 2;

# Supplier by product
S = zeros(noProducts, 1);
S(product_grano) = supplier_campo;
S(product_mangime) = supplier_mangimificio;

# No of suppliers
N = zeros(noSuppliers, 1);
N(supplier_campo) = 10.0;
N(supplier_mangimificio) = 2.0;

# Quantity of products by supplier
Q = zeros(noProducts, 1);
Q(product_grano) = 2.0;
Q(product_mangime) = 1.0;

# Value of products
V = zeros(noProducts, 1);
V(product_grano) = 3.6;
V(product_mangime) = 5.0;

# Interval for product by supplier
T = zeros(noProducts, 1);
T(product_grano) = 300;
T(product_mangime) = 3600;

# Consumptions of product by product
C = zeros(noProducts, noProducts);
C(product_grano, product_grano) = 1.0;
C(product_mangime, product_grano) = 1.0;
""")
          }
      }
  }
}
