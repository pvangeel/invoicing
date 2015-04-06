package models

import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json
import anorm.SqlParser._
import anorm._


case class Product(id: Option[Long], description: String, price: BigDecimal, vat: BigDecimal)


object Product {

  def createOrUpdateProduct(product: Product) = product match {
    case Product(Some(id), _, _, _) => {
      ???
    }
    case Product(None, _, _, _) => createProduct(product)
  }

  def createProduct(product: Product) = DB.withConnection {
    implicit c =>
      val id: Option[Long]= SQL("insert into product(description, price, vat) values ({description}, {price}, {vat})")
        .on('description -> product.description)
        .on('price -> product.price)
        .on('vat -> product.vat)
        .executeInsert()
      Product(id, product.description, product.price, product.vat)
  }


  implicit val productWrites = Json.writes[Product]
  implicit val productReads = Json.reads[Product]


  val full = {
    get[Option[Long]]("id") ~ get[String]("description") ~ get[BigDecimal]("price") ~ get[BigDecimal]("vat") map {
      case id ~ description ~ price ~ vat => Product(id, description, price, vat)
    }
  }

  def findAll = DB.withConnection {
    implicit c => SQL("select * from product").as(full *)
  }

}