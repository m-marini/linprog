package controllers

import java.util.Collections

import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.sun.org.apache.xml.internal.security.utils.Base64

import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.libs.ws.WSRequest
import com.typesafe.scalalogging.LazyLogging

@Singleton
class LoginController @Inject() (ws: WSClient)(implicit ec: ExecutionContext) extends Controller with LazyLogging {

  private val AuthorizationServerUrl = "https://www.googleapis.com/oauth2/v4/token"
  private val ClientId = "356010545588-60gdq4m1us25ikg3cudppuaf0qioic1o.apps.googleusercontent.com"
  private val ClientSecret = "f5bQ2NN80Ai8kw6Jd8qcybuB"
  private val Scopes = Collections.singleton("email")

  private lazy val dataStoreFactory = new MemoryDataStoreFactory

  private def initFlow =
    new GoogleAuthorizationCodeFlow.Builder(
      //      buildTransport,
      new NetHttpTransport(),
      JacksonFactory.getDefaultInstance,
      ClientId,
      ClientSecret,
      Scopes).
      setDataStoreFactory(dataStoreFactory).
      setAccessType("online").
      build

  def login = Action {
    Redirect(initFlow.
      newAuthorizationUrl.
      setState("/profile").
      setRedirectUri("http://localhost:9000/oauthcallback").
      build).withNewSession
  }

  def logged = Action { request =>
    val res = for {
      id <- request.session.get("id")
      tk <- Option(initFlow.loadCredential(id))
    } yield {
      //      val wsReq = buildProxyRequest("https://www.googleapis.com/auth/userinfo.email")
      //      val wsReq = buildProxyRequest("https://www.googleapis.com/auth/plus.me")
      val auth = "Bearer " + tk.getAccessToken

      val wsReq = buildProxyRequest("https://www.googleapis.com/plus/v1/people/me").
        withHeaders("authorization" -> auth).
        withQueryString("key" -> "AIzaSyD-a9IF8KKYgoC3cpgS-Al7hLQDbugrDcw")

      println(s"url=${wsReq.url}")
      println(s"query=${wsReq.queryString}")
      println(s"headers=${wsReq.headers}")
      println(s"body=${wsReq.body}")
      //            tk.getClientAuthentication + " " + tk.getAccessToken
      //            "Bearer ya29.CjLKAz59eVLV8kutOzOxgwm5IpJm6jgiuoBKTAx9Ub8UqpM1teu2GJIQpLgxwm9h3gqBnA")

      for {
        res <- wsReq.get
      } {
        println(s"${res.status}")
        println(s"${res.body}")
        //        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
        //      return requestFactory.buildGetRequest(url).execute();
      }
      Ok(s"Logged $id, ${tk.getAccessToken}").withSession(request.session)
    }
    res.getOrElse(Redirect("http://localhost:9000/login"))
  }

  def oauthcallback = Action.async { request =>

    def validateRequest =
      request.getQueryString("code") match {
        case None => Some("Missing code")
        case _ => request.getQueryString("error")
      }

    validateRequest.map(msg => Future.successful {
      Unauthorized(msg).withNewSession
    }).getOrElse {
      val code = request.getQueryString("code").get
      requestToken(code).map(processTokenResponse)
    }
  }

  private def processTokenResponse(response: WSResponse) = {

    def toOkResponse(tokenResponse: TokenResponse) = {
      initFlow.createAndStoreCredential(tokenResponse, "a@a.org")
      logger.info(s"token=${tokenResponse.getAccessToken}")
      println(s"token=${tokenResponse.getAccessToken}")
      Redirect("http://localhost:9000/logged").withSession("id" -> "a@a.org")
    }

    if (response.status != 200) {
      Unauthorized(response.body)
    } else {
      val tokenResponse = JacksonFactory.getDefaultInstance.
        createJsonParser(response.body).
        parse(classOf[TokenResponse])
      toOkResponse(tokenResponse)
    }
  }

  private def requestToken(code: String): Future[WSResponse] =
    buildProxyRequest(AuthorizationServerUrl).
      post(Map(
        "code" -> Seq(code),
        "client_id" -> Seq(ClientId),
        "client_secret" -> Seq(ClientSecret),
        "redirect_uri" -> Seq("http://localhost:9000/oauthcallback"),
        "grant_type" -> Seq("authorization_code")))

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