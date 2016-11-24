package v1.farmer

import javax.inject.{ Inject, Provider }

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json._

/**
 * DTO for displaying post information.
 */
case class Farmer(id: String,
  name: String,
  suppliers: Map[String, Double] = Map(),
  values: Map[String, Double] = Map())

object Farmer {

  /**
   * Mapping to write a PostResource out as a JSON value.
   */
  implicit val implicitWrites = new Writes[Farmer] {
    def writes(post: Farmer): JsValue = {
      Json.obj(
        "id" -> post.id,
        "name" -> post.name,
        "suppliers" -> post.suppliers,
        "values" -> post.values)
    }
  }
}

