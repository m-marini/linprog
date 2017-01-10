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

import java.util.Collections

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.collection.JavaConverters._

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.sun.org.apache.xml.internal.security.utils.Base64
import com.typesafe.scalalogging.LazyLogging

import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.ws.WSResponse
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.Configuration
import java.util.Collection
import play.api.Play
import play.twirl.api.Html
import play.api.http.Status._
import play.api.mvc.Request
import org.mmarini.linprog.restapi.v1.SecurityAccess

@Singleton
class LoginController @Inject() (
  access: SecurityAccess,
  configuration: Configuration)(
    implicit ec: ExecutionContext)
    extends Controller with LazyLogging {

  private val loggedUrl = configuration.underlying.getString("loginController.loggedUrl")

  /** Creates log in action */
  def login: Action[AnyContent] = Action {
    Redirect(access.newAuthorizationUrl).withNewSession
  }

  /** Creates authorized action */
  def oauthcallback: Action[AnyContent] = Action.async { request =>
    // Validates authorization code
    def validateRequest =
      request.getQueryString("code") match {
        case None => Some("Missing code")
        case _ => request.getQueryString("error")
      }

    validateRequest.map(msg => Future.successful {
      Unauthorized(msg).withNewSession
    }).getOrElse {
      val code = request.getQueryString("code").get
      access.requestTokenWithCode(code).flatMap {
        case Some(token) =>
          access.requestUserinfoWithToken(token.getAccessToken).map {
            case Some(userinfo) =>
              val credential = access.storeCredential(userinfo.email, token)
              loggedRedirect(userinfo.email, credential)
            case None => Unauthorized("Bho2").withNewSession
          }
        case None => Future.successful(Unauthorized("Bho1").withNewSession)
      }
    }
  }

  private def loggedRedirect(id: String, credential: Credential): Result = {
    val enc = (p: String) => java.net.URLEncoder.encode(p, "utf-8")
    Redirect(s"$loggedUrl?id=${enc(id)}").withSession(
      "tk" -> credential.getAccessToken)
  }

}
