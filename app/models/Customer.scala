package models

import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json
import anorm.SqlParser._
import anorm._


case class Address(street: String, number: String, postalCode: String, city: String)

case class Customer(id: Option[Long], name: String, address: Address, vat: String) {

  lazy val tetten = "Test Test Test"
}

object Customer {



  implicit val addressWrites = Json.writes[Address]
  implicit val addressReads = Json.reads[Address]

  implicit val customerWrites = Json.writes[Customer]
  implicit val customerReads = Json.reads[Customer]

  def findAll = DB.withConnection {
    implicit c =>
      SQL("select * from customer").as(full *)
  }

  def findByNameLike(query: String) = DB.withConnection {
    implicit c =>
      SQL("select * from customer where name ilike {query}")
        .on('query -> s"%$query%")
        .as(full *)
  }

  def createOrUpdateCustomer(customer: Customer) = customer match {
    case Customer(Some(id), _, _, _) => updateCustomer(customer)
    case Customer(None, _, _, _) => createCustomer(customer)
  }

  def createCustomer(customer: Customer) = DB.withConnection {
    implicit c =>
      val id: Option[Long]= SQL("insert into customer(name, street, number, postalCode, city, vat) values ({name}, {street}, {number}, {postalCode}, {city}, {vat})")
        .on('name -> customer.name)
        .on('street -> customer.address.street)
        .on('number -> customer.address.number)
        .on('postalCode -> customer.address.postalCode)
        .on('city -> customer.address.city)
        .on('vat -> customer.vat)
        .executeInsert()
      Customer(id, customer.name, customer.address, customer.vat)
  }

  def updateCustomer(customer: Customer) = DB.withConnection {
    implicit c =>
      SQL("update customer set (name, street, number, postalCode, city, vat) = ({name}, {street}, {number}, {postalCode}, {city}, {vat}) where id = {id}")
        .on('id -> customer.id.get)
        .on('name -> customer.name)
        .on('street -> customer.address.street)
        .on('number -> customer.address.number)
        .on('postalCode -> customer.address.postalCode)
        .on('city -> customer.address.city)
        .on('vat -> customer.vat)
        .executeUpdate()
      customer
  }


  val full = {
    get[Option[Long]]("id") ~ get[String]("name") ~ get[String]("vat") ~ get[String]("street") ~ get[String]("number") ~ get[String]("postalCode") ~ get[String]("city") map {
      case id ~ name ~ vat ~ street ~ number ~ postalCode ~ city => Customer(id, name, Address(street, number, postalCode, city), vat)
    }
  }


}
