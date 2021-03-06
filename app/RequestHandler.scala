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

import javax.inject.Inject

import play.api.http._
import play.api.mvc._
import play.api.routing.Router

/**
 * Handles all requests.
 *
 * https://www.playframework.com/documentation/2.5.x/ScalaHttpRequestHandlers#extending-the-default-request-handler
 */
class RequestHandler @Inject() (router: Router,
  errorHandler: HttpErrorHandler,
  configuration: HttpConfiguration,
  filters: HttpFilters)
    extends DefaultHttpRequestHandler(router,
      errorHandler,
      configuration,
      filters) {

  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {
    super.handlerForRequest {
      // ensures that REST API does not need a trailing "/"
      if (isREST(request)) {
        addTrailingSlash(request)
      } else {
        request
      }
    }
  }

  private def isREST(request: RequestHeader) = {
    request.uri match {
      case uri: String if uri.contains("post") => true
      case _ => false
    }
  }

  private def addTrailingSlash(origReq: RequestHeader): RequestHeader = {
    if (!origReq.path.endsWith("/")) {
      val path = origReq.path + "/"
      if (origReq.rawQueryString.isEmpty) {
        origReq.copy(path = path, uri = path)
      } else {
        origReq.copy(path = path, uri = path + s"?${origReq.rawQueryString}")
      }
    } else {
      origReq
    }
  }
}
