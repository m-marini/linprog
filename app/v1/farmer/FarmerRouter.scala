package v1.farmer

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird.GET
import play.api.routing.sird.PUT
import play.api.routing.sird.UrlContext

class FarmerRouter @Inject() (
    controller: FarmerController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/new") => controller.build("")
    case GET(p"/$id") => controller.getById(id)
    case PUT(p"/$id") => controller.put(id)
  }

}