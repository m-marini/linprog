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

import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.sun.org.apache.xml.internal.security.utils.Base64
import com.typesafe.scalalogging.LazyLogging

import controllers.Userinfo
import javax.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request

@Singleton
class SecurityAccess @Inject() (ws: WSClient, configuration: Configuration)(implicit ec: ExecutionContext) extends Controller with LazyLogging {

  private val authorizationServerUrl = configuration.underlying.getString("securityAccess.authorizationServerUrl")
  private val oauth2CallbackUrl = configuration.underlying.getString("securityAccess.callbackUrl")
  private val userProfileUrl = configuration.underlying.getString("securityAccess.userProfileUrl")
  private val clientId = configuration.underlying.getString("securityAccess.clientId")
  private val clientSecret = configuration.underlying.getString("securityAccess.clientSecret")
  private val accessType = configuration.underlying.getString("securityAccess.accessType")
  private val scopes = configuration.underlying.getStringList("securityAccess.scopes")

  private val dataStoreFactory = new MemoryDataStoreFactory

  println("Init SecurityAccess")

  /** Builds initial authorization flow */
  private def initFlow =
    new GoogleAuthorizationCodeFlow.Builder(
      //      buildTransport,
      new NetHttpTransport(),
      JacksonFactory.getDefaultInstance,
      clientId,
      clientSecret,
      scopes).
      setDataStoreFactory(dataStoreFactory).
      setAccessType(accessType).
      build

  def validateUser(id: String, request: Request[AnyContent]): Option[Credential] = {
    val tkOpt = request.session.get("tk")
    val idCredOpt = Option(initFlow.loadCredential(id))
    for {
      cr <- idCredOpt
      tk <- tkOpt
      if (tk == cr.getAccessToken)
    } yield {
      cr
    }
  }

  def deleteCredential(id: String) {
    initFlow.getCredentialDataStore.delete(id)
  }

  def newAuthorizationUrl: String =
    initFlow.
      newAuthorizationUrl.
      setState("/profile").
      setRedirectUri(oauth2CallbackUrl).
      build

  def requestUserinfoWithToken(token: String): Future[Option[Userinfo]] =
    buildProxyRequest(userProfileUrl).
      withHeaders("authorization" -> ("Bearer " + token)).
      get.
      map { res =>
        // Validates user profile response
        res.status match {
          case OK => Some(Json.parse(res.body).as[Userinfo])
          case _ => None
        }
      }

  def requestTokenWithCode(code: String): Future[Option[TokenResponse]] =
    buildProxyRequest(authorizationServerUrl).
      post(Map(
        "code" -> Seq(code),
        "client_id" -> Seq(clientId),
        "client_secret" -> Seq(clientSecret),
        "redirect_uri" -> Seq(oauth2CallbackUrl),
        "grant_type" -> Seq("authorization_code"))).
      map { res =>
        res.status match {
          case OK => Some(JacksonFactory.getDefaultInstance.
            createJsonParser(res.body).
            parse(classOf[TokenResponse]))
          case _ => None
        }
      }

  def storeCredential(id: String, response: TokenResponse): Credential =
    initFlow.createAndStoreCredential(response, id)

  private def buildProxyRequest(url: String): WSRequest = {
    val baseReq = ws.url(url)
    val auth = for {
      user <- Option(System.getProperty("http.proxyUser"))
      psw <- Option(System.getProperty("http.proxyPassword"))
    } yield {
      "Basic " + Base64.encode(s"$user:$psw".getBytes).toString
    }
    auth.map { auth =>
      baseReq.withHeaders("Proxy-Authorization" -> auth)
    }.getOrElse(baseReq)
  }
}
