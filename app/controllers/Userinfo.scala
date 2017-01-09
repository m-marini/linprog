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

package controllers

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsPath
import play.api.libs.json.JsPath
import play.api.libs.json.JsValue
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Reads
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.Writes

case class Userinfo(
  id: String,
  name: String,
  givenName: String,
  familyName: String,
  email: String,
  picture: String)

object Userinfo {

  /**
   * Mapping to write a Farmer out as a JSON value.
   */
  implicit val implicitWrites = new Writes[Userinfo] {
    def writes(userinfo: Userinfo): JsValue = {
      Json.obj(
        "id" -> userinfo.id,
        "name" -> userinfo.name)
    }
  }

  /**
   * Mapping to read a JSON value int as a Farmer.
   */
  implicit val implicitReads: Reads[Userinfo] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "given_name").read[String] and
      (JsPath \ "family_name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "picture").read[String])(Userinfo.apply _)
}
