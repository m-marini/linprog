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
  val suppliersText = """
campo: 10
mangimificio: 2
"""

  val valuesText = """
grano: 3.6
mangime: 5
"""

  val chainText = """
grano:
  quantity: 2
  interval: 5 min
  consumptions:
    grano: 1
  producer: campo
mangime:
  quantity: 1
  interval: 1 hours
  consumptions:
    grano: 1
  producer: mangimificio
"""

  property("counters") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.counters should include("""
noProducts = 2;
noSuppliers = 2;""")
          }
      }
  }

  property("supplierIndexes") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.supplierIndexes should include("""
# Supplier indexes
supplier_campo = 1;
supplier_mangimificio = 2;""")
          }
      }
  }

  property("supplierNamesDefs") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.supplierNamesDefs should include("""
# Supplier names
supplierNames = {
"campo",
"mangimificio"
};""")
          }
      }
  }

  property("productIndexes") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.productIndexes should include("""
# Product indexes
product_grano = 1;
product_mangime = 2;""")
          }
      }
  }

  property("productNamesDefs") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.productNamesDefs should include("""
# Product names
productNames = {
"grano",
"mangime"
};""")
          }
      }
  }

  property("supplierByProduct") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.supplierByProduct should include("""
# Supplier by product
s = zeros(noProducts, 1);
s(product_grano) = supplier_campo;
s(product_mangime) = supplier_mangimificio;""")

          }
      }
  }

  property("noSuppliers") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.noSuppliers should include("""
# No of suppliers
n = zeros(noSuppliers, 1);
n(supplier_campo) = 10.0;
n(supplier_mangimificio) = 2.0;""")

          }
      }
  }

  property("quantities") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.quantities should include("""
# Quantity of products by supplier
q = zeros(noProducts, 1);
q(product_grano) = 2.0;
q(product_mangime) = 1.0;""")

          }
      }
  }

  property("valuesDef") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.valuesDef should include("""
# Value of products
v = zeros(noProducts, 1);
v(product_grano) = 3.6;
v(product_mangime) = 5.0;""")

          }
      }
  }

  property("consumptions") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.consumptions should include("""
# Consumptions of product by product
D = zeros(noProducts, noProducts);
D(product_grano, product_grano) = 1.0;
D(product_mangime, product_grano) = 1.0;""")

          }
      }
  }

  property("intervals") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.intervals should include("""
# Interval for product by supplier
t = zeros(noProducts, 1);
t(product_grano) = 300;
t(product_mangime) = 3600;""")
          }
      }
  }

  property("toString") {

    forAll(
      (Gen.const(suppliersText), "suppliersText"),
      (Gen.const(valuesText), "valuesText"),
      (Gen.const(chainText), "chainText")) {
        (suppliersText, valuesText, chainText) =>
          {
            val suppliers = Parameters.fromYamlString(suppliersText)
            val values = Parameters.fromYamlString(valuesText)
            val chain = SupplyChain.fromYamlString(chainText)

            val toOctave = new ToOctave(chain, suppliers, values)

            toOctave.toString should include("""
noProducts = 2;
noSuppliers = 2;

# Supplier indexes
supplier_campo = 1;
supplier_mangimificio = 2;

# Supplier names
supplierNames = {
"campo",
"mangimificio"
};

# Product indexes
product_grano = 1;
product_mangime = 2;

# Product names
productNames = {
"grano",
"mangime"
};

# Supplier by product
s = zeros(noProducts, 1);
s(product_grano) = supplier_campo;
s(product_mangime) = supplier_mangimificio;

# No of suppliers
n = zeros(noSuppliers, 1);
n(supplier_campo) = 10.0;
n(supplier_mangimificio) = 2.0;

# Quantity of products by supplier
q = zeros(noProducts, 1);
q(product_grano) = 2.0;
q(product_mangime) = 1.0;

# Value of products
v = zeros(noProducts, 1);
v(product_grano) = 3.6;
v(product_mangime) = 5.0;

# Interval for product by supplier
t = zeros(noProducts, 1);
t(product_grano) = 300;
t(product_mangime) = 3600;

# Consumptions of product by product
D = zeros(noProducts, noProducts);
D(product_grano, product_grano) = 1.0;
D(product_mangime, product_grano) = 1.0;
""")
          }
      }
  }
}
