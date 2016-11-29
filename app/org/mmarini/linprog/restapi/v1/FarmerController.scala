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

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import play.api.http.HttpVerbs
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ActionBuilder
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.WrappedRequest
import play.api.data.Form
import org.mmarini.linprog.restapi.v1._

/**
 * The [[FarmerController]] returns an action to handle a specific request
 */
class FarmerController @Inject() (action: FarmerAction, repo: FarmerRepository)(implicit ec: ExecutionContext)
    extends Controller {

  def computeSuppliers(id: String): Action[AnyContent] = action.async {
    implicit request =>
      repo.createSupplierMap(id).map {
        case Some(conf) => Ok(Json.toJson(conf))
        case None => BadRequest
      }
  }

  /** Creates the action to build a new farmer from a template */
  def build(template: String): Action[AnyContent] = action.async {
    implicit request =>
      for {
        farmerOpt <- repo.build(template)
      } yield {
        farmerOpt match {
          case Some(farmer) => Ok(Json.toJson(farmer))
          case None => NotFound
        }
      }
  }

  /** Creates the action to create a new farmer in repository */
  def put(id: String): Action[AnyContent] = action.async {
    implicit request =>
      {
        request.body.asJson match {
          case None => Future { BadRequest }
          case Some(json) =>
            // Extracts farmer from body
            val farmer = json.as[Farmer]

            if (farmer.id == id) {
              // Creates farmer in repository
              repo.put(farmer).map {
                case (farmer, true) => Created(Json.toJson(farmer))
                case (farmer, false) => Ok(Json.toJson(farmer))
              }
            } else {
              Future { BadRequest }
            }

        }
      }
  }

  /** Creates the action to get a farmer by id */
  def get(id: String): Action[AnyContent] = action.async {
    implicit request =>
      for {
        farmerOpt <- repo.retrieveById(id)
      } yield farmerOpt match {
        case None => NotFound
        case Some(farmer) => Ok(Json.toJson(farmerOpt.get))
      }
  }

  /** Creates the action to delete a farmer */
  def delete(id: String): Action[AnyContent] = action.async {
    implicit request =>
      repo.delete(id).map {
        case None => NotFound
        case _ => Ok
      }
  }

  /** Creates the action to find a farmer by name */
  def find(name: String): Action[AnyContent] = action.async {
    implicit request =>
      {
        for { list <- repo.retrieveByName(name) } yield {
          Ok(Json.toJson(list))
        }
      }
  }

}

/** FarmerRequest wraps a request of type A adding messages */
class FarmerRequest[A](request: Request[A], val messages: Messages)
  extends WrappedRequest(request)

/** Farmer Action ??? */
class FarmerAction @Inject() (messagesApi: MessagesApi)(implicit ec: ExecutionContext)
    extends ActionBuilder[FarmerRequest]
    with HttpVerbs {

  // ???
  type FarmerRequestBlock[A] = FarmerRequest[A] => Future[Result]

  // ???
  override def invokeBlock[A](request: Request[A],
    block: FarmerRequestBlock[A]): Future[Result] = {
    val messages = messagesApi.preferred(request)
    val future = block(new FarmerRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }

}
