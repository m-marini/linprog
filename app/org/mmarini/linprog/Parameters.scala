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
import scalax.io.Input
import scalax.io.Resource
import java.io.InputStream

object Parameters {
  def fromYaml(yaml: YamlValue): Map[String, Double] = new ParmsBuilder(yaml).build

  def fromYamlString(text: String): Map[String, Double] =
    fromYaml(text.parseYaml)

  def fromInput(resource: Input): Map[String, Double] =
    fromYamlString(resource.string(Codec.UTF8))

  def fromFile(filename: String): Map[String, Double] =
    fromInput(Resource.fromFile(filename))

  def fromClasspath(name: String, clazz: Class[_]): Map[String, Double] = {
    val stream = clazz.getResourceAsStream(name)
    require(!Option(stream).isEmpty, s"Resource $name not found")
    fromInput(Resource.fromInputStream(stream))
  }

  def fromClasspath(name: String): Map[String, Double] =
    fromClasspath(name, getClass)

  def fromClassLoader(name: String, classLoader: ClassLoader): Map[String, Double] = {
    val stream = classLoader.getResourceAsStream(name)
    require(!Option(stream).isEmpty, s"Resource $name not found")
    fromInput(Resource.fromInputStream(stream))
  }
}

class ParmsBuilder(yaml: YamlValue) {

  def build: Map[String, Double] = {
    val parmsYamlMap = yaml match {
      case YamlObject(map) => map
      case _ => deserializationError("wrong parameters object")
    }
    parmsYamlMap.map {
      case (YamlString(key), YamlNumber(n: Number)) => (key, n.doubleValue())
      case (key, _) => deserializationError(s"wrong value for $key")
    }.withDefault { _ => 0.0 }
  }
}

