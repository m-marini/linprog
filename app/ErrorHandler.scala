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

import javax.inject.{ Inject, Provider }

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.core.SourceMapper

import scala.concurrent._

/**
 * Provides a stripped down error handler that does not use HTML in error pages, and
 * prints out debugging output.
 *
 * https://www.playframework.com/documentation/2.5.x/ScalaErrorHandling
 */
class ErrorHandler(environment: Environment,
  configuration: Configuration,
  sourceMapper: Option[SourceMapper] = None,
  optionRouter: => Option[Router] = None)
    extends DefaultHttpErrorHandler(environment,
      configuration,
      sourceMapper,
      optionRouter) {

  private val logger =
    org.slf4j.LoggerFactory.getLogger("application.ErrorHandler")

  // This maps through Guice so that the above constructor can call methods.
  @Inject
  def this(environment: Environment,
    configuration: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router]) = {
    this(environment,
      configuration,
      sourceMapper.sourceMapper,
      Some(router.get))
  }

  override def onClientError(request: RequestHeader,
    statusCode: Int,
    message: String): Future[Result] = {
    logger.debug(
      s"onClientError: statusCode = $statusCode, uri = ${request.uri}, message = $message")

    Future.successful {
      val result = statusCode match {
        case BAD_REQUEST =>
          Results.BadRequest(message)
        case FORBIDDEN =>
          Results.Forbidden(message)
        case NOT_FOUND =>
          Results.NotFound(message)
        case clientError if statusCode >= 400 && statusCode < 500 =>
          Results.Status(statusCode)
        case nonClientError =>
          val msg =
            s"onClientError invoked with non client error status code $statusCode: $message"
          throw new IllegalArgumentException(msg)
      }
      result
    }
  }

  override protected def onDevServerError(
    request: RequestHeader,
    exception: UsefulException): Future[Result] = {
    Future.successful(
      InternalServerError(Json.obj("exception" -> exception.toString)))
  }

  override protected def onProdServerError(
    request: RequestHeader,
    exception: UsefulException): Future[Result] = {
    Future.successful(InternalServerError)
  }
}
