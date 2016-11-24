package v1.farmer

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

/**
 * The [[FarmerController]] returns an action to handle a specific request
 */
class FarmerController @Inject() (action: FarmerAction, repo: FarmerRepository)(implicit ec: ExecutionContext)
    extends Controller {

  /** Creates the action to build a new farmer from a template */
  def build(template: String): Action[AnyContent] = action.async {
    implicit request =>
      repo.build(template).map(farmer => Ok(Json.toJson(farmer)))
  }

  /** Creates the action to create a new farmer in repository */
  def put(id: String): Action[AnyContent] = action.async {
    implicit request =>
      {
        // Extracts farmer from body
        val farmer = Farmer(id, id)
        // Validates farmer

        // Creates farmer in repository
        repo.put(farmer).map {
          case (farmer, true) => Created(Json.toJson(farmer))
          case (farmer, false) => Ok(Json.toJson(farmer))
        }
      }
  }

  def getById(id: String): Action[AnyContent] = action.async {
    implicit request =>
      for {
        farmerOpt <- repo.retrieveById(id)
      } yield farmerOpt match {
        case None => NotFound
        case Some(farmer) => Ok(Json.toJson(farmerOpt.get))
      }
  }

  def find(query: String): Action[AnyContent] = action.async {
    implicit request =>
      for { list <- repo.retrieveByQuery(query) } yield {
        Ok(Json.toJson(list))
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
