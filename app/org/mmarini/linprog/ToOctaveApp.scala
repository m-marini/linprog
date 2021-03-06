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

import scalax.io.Resource
import scalax.file.Path
import com.typesafe.scalalogging.LazyLogging

object ToOctaveApp extends App with LazyLogging {
  require(args.length >= 4, "at least 4 arguments needed")

  val chainFile = args(0)
  val suppliersFile = args(1)
  val valuesFile = args(2)
  val outFile = args(3)

  logger.info(s"Reading")
  logger.info(s" chain     $chainFile")
  logger.info(s" suppliers $suppliersFile")
  logger.info(s" values    $valuesFile")

  val chain = SupplyChain.fromFile(chainFile)
  val suppliers = Parameters.fromFile(suppliersFile)
  val values = Parameters.fromFile(valuesFile)

  logger.info(s"Writting $outFile ...")

  Path.fromString(outFile).deleteIfExists()
  Resource.fromFile(outFile).write(new ToOctave(chain, suppliers, values).toString)
  logger.info(s"Completed")
}
