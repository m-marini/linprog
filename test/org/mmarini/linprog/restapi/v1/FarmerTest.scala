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

import org.scalatestplus.play.OneServerPerTest
import org.scalatestplus.play.PlaySpec

import play.api.libs.json.JsLookupResult.jsLookupResultToJsLookup
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.test.Helpers.CREATED
import play.api.test.Helpers.NOT_FOUND
import play.api.test.Helpers.OK
import play.api.test.Helpers.await
import play.api.test.Helpers.defaultAwaitTimeout

class FarmerTest extends PlaySpec with Results with OneServerPerTest {

  "Create a new farmer when GET /v1/farmers/new" must {
    "result the new farmer with name Default" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/new").
        withQueryString("t" -> "base")
      val response = await(wsRequest.get)
      response.status mustBe OK

      val json = Json.parse(response.body)
      (json \ "name").as[String] mustBe "Default"
      (json \ "suppliers").as[Map[String, JsValue]] must not be empty
      (json \ "suppliers" \ "campo").as[Int] mustBe (10)
      (json \ "suppliers" \ "gallina").as[Int] mustBe (6)
    }
    "result the new farmer with defined product values" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/new").
        withQueryString("t" -> "base")
      val response = await(wsRequest.get)
      response.status mustBe OK

      val json = Json.parse(response.body)
      (json \ "values").as[Map[String, JsValue]] must not be empty
      (json \ "values" \ "grano").as[Double] mustBe (3.6)
      (json \ "values" \ "mais").as[Double] mustBe (7.2)
    }
  }

  private def checkFarmer(id: String, name: String) {
    val wsClient = app.injector.instanceOf[WSClient]
    val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/$id")
    val response = await(wsRequest.get)

    response.status mustBe OK

    val json = Json.parse(response.body)
    (json \ "id").as[String] mustBe id
    (json \ "name").as[String] mustBe name
  }

  private def createFarmer(id: String, name: String) {
    val wsClient = app.injector.instanceOf[WSClient]
    val putResponse = await(
      wsClient.url(s"http://localhost:$port/v1/farmers/1").
        put(
          JsObject(Seq(
            "id" -> JsString(id),
            "name" -> JsString(name),
            "values" -> JsObject(Seq()),
            "suppliers" -> JsObject(Seq())))))
    putResponse.status mustBe CREATED

    val json = Json.parse(putResponse.body)
    (json \ "id").as[String] mustBe id
    (json \ "name").as[String] mustBe name
  }

  "Putting a new farmer" must {
    "result CREATED with the new farmer with id 1 when PUT /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
    }
    "result OK with the new farmer with name test when GET /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
      checkFarmer(id = "1", name = "test")
    }
  }

  "Updateing a farmer" must {

    def updateFarmer(id: String, name: String) {
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
      (json \ "id").as[String] mustBe "1"
      (json \ "name").as[String] mustBe "changed"
    }

    "result OK when PUT /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
      updateFarmer(id = "1", name = "changed")
    }
    "result OK with the new farmer with name changed when GET /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
      updateFarmer(id = "1", name = "changed")
      checkFarmer(id = "1", name = "changed")
    }
  }

  "Deleteing a farmer" must {

    def deleteFarmer(id: String) {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/$id")
      val response = await(wsRequest.delete)

      response.status mustBe OK
    }

    "result OK when DELETE /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
      deleteFarmer(id = "1")
    }
    "result NOT_FOUND when GET /v1/farmers/1" in {
      createFarmer(id = "1", name = "test")
      deleteFarmer(id = "1")

      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
      val response = await(wsRequest.get)

      response.status mustBe NOT_FOUND
    }
  }

  "Deleteing a not existing farmer" must {
    "result NOT_FOUND when DELETE /v1/farmers/1" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/1")
      val response = await(wsRequest.delete)

      response.status mustBe NOT_FOUND
    }
  }

  "Getting a farmer by name" must {
    "result OK when GET /v1/farmers/find?n=test" in {
      createFarmer(id = "1", name = "test")
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/").
        withQueryString("n" -> "test")
      val response = await(wsRequest.get)

      response.status mustBe OK

      val json = Json.parse(response.body)
      json.as[Seq[JsValue]] must have size (1)
      (json(0) \ "name").as[String] mustBe "test"
      (json(0) \ "id").as[String] mustBe "1"
    }
    "result OK and empty list when GET /v1/farmers/find?n=dummy" in {
      createFarmer(id = "1", name = "test")
      val wsClient = app.injector.instanceOf[WSClient]
      val wsRequest = wsClient.url(s"http://localhost:$port/v1/farmers/").
        withQueryString("n" -> "dummy")
      val response = await(wsRequest.get)

      response.status mustBe OK

      val json = Json.parse(response.body)
      json.as[Seq[JsValue]] mustBe empty
    }
  }
}
