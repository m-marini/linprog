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

package v1.farmer

import org.scalatestplus.play.PlaySpec
import org.scalatest._
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Results
import play.i18n.MessagesApi
import play.api.libs.ws.WSClient
import akka.stream.Materializer
import play.libs.Json
import play.api.libs.json.JsString
import com.fasterxml.jackson.databind.node.TextNode
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue

class FarmerTest extends PlaySpec with Results with OneServerPerTest {

  "Create a new farmer when GET /v1/farmers/new" must {
    "result the new farmer with name Default" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/new").
        withQueryString("template" -> "any")
      val response = await(wsRequest.get)
      response.status mustBe OK

      val json = Json.parse(response.body)
      json.path("name").asText mustBe "Default"
    }
  }

  private def create(id: String, name: String)() {
    val wsClient = app.injector.instanceOf[WSClient]
    val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
    val payload = JsObject(Seq(
      "id" -> JsString("1"),
      "name" -> JsString("test"),
      "values" -> JsObject(Seq()),
      "suppliers" -> JsObject(Seq())))
    val response = await(wsRequest.put(payload))

    response.status mustBe CREATED

    val json = Json.parse(response.body)
    json.path("id").asText mustBe "1"
    json.path("name").asText mustBe "test"
  }

  private def getTest(id: String, name: String)() {
    val wsClient = app.injector.instanceOf[WSClient]
    val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/$id").
      withQueryString("template" -> "any")
    val response = await(wsRequest.get)
    response.status mustBe OK

    val json = Json.parse(response.body)
    json.path("id").asText mustBe id
    json.path("name").asText mustBe name
  }

  "Putting a new farmer" must {
    "result CREATED with the new farmer with id 1 when PUT /v1/farmers/1" in create("1", "test")
    "result OK with the new farmer with name test when GET /v1/farmers/1" in getTest("1", "test")
  }

  "Updateing a farmer" must {
    "result CREATED with the new farmer with id 1 when PUT /v1/farmers/1" in create("1", "test")
    "result OK when PUT /v1/farmers/1" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
      val payload = JsObject(Seq(
        "id" -> JsString("1"),
        "name" -> JsString("changed"),
        "values" -> JsObject(Seq()),
        "suppliers" -> JsObject(Seq())))
      val response = await(wsRequest.put(payload))

      response.status mustBe OK

      val json = Json.parse(response.body)
      json.path("id").asText mustBe "1"
      json.path("name").asText mustBe "changed"
    }
    "result OK with the new farmer with name changed when GET /v1/farmers/1" in getTest("1", "changed")
  }

  "Deleteing a farmer" must {
    "result CREATED with the new farmer with id 1 when PUT /v1/farmers/1" in create("1", "test")
    "result OK when DELETE /v1/farmers/1" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
      val response = await(wsRequest.delete)

      response.status mustBe OK
    }
    "result NOT_FOUND when GET /v1/farmers/1" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
      val response = await(wsRequest.get)

      response.status mustBe NOT_FOUND
    }
  }
}
