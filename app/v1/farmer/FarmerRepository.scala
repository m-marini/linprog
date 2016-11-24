package v1.farmer

import scala.concurrent.Future

import javax.inject.Singleton
import org.mmarini.linprog.Product
import org.mmarini.linprog.SupplyChain
import net.jcazevedo.moultingyaml.YamlObject
import scala.concurrent.ExecutionContext

/**
 *
 */
@Singleton
class FarmerRepository {

  private var store: Map[String, Farmer] = Map()

  // TODO load from classpath
  //  private def chain = SupplyChain.fromFile("/home/us00852/git/linprog/app/v1/farmer/chain.yaml")
  private def chain = SupplyChain.fromFile("app/v1/farmer/chain.yaml")

  /**
   * Creates a Farmer from a template name
   */
  def build(template: String)(implicit ec: ExecutionContext): Future[Farmer] =
    Future.successful {
      val values = chain.keys.map((_, 0.0)).toMap
      val y = chain.values.toList.
        map(x => x.producer).toSet
      val suppliers = y.map((_, 0.0)).toMap
      Farmer(
        id = java.util.UUID.randomUUID.toString,
        name = "Default",
        suppliers = suppliers,
        values = values)
    }

  def put(farmer: Farmer): Future[(Farmer, Boolean)] = Future.successful {
    // TODO Access MUST be serialized
    val created = !store.contains(farmer.id)
    store = store + (farmer.id -> farmer)
    (farmer, created)
  }

  def delete(id: String): Future[Option[Farmer]] = Future.successful {
    // TODO Access MUST be serialized
    val ret = store.get(id)
    store = store - id
    ret
  }

  /**
   *
   */
  def retrieveById(id: String): Future[Option[Farmer]] =
    Future.successful { store.get(id) }

  /**
   *
   */
  def retrieveByQuery(query: String): Future[Seq[Farmer]] = Future.successful {
    store.values.filter(_.name == query).toSeq
  }
}