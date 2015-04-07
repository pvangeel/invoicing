package models

import play.api.libs.json.Json

case class InvoiceLine(id: Option[Long], productId: Long, price: BigDecimal, quantity: Long, vat: BigDecimal)

object InvoiceLine {


  implicit val InvoiceLineWrites = Json.writes[InvoiceLine]
  implicit val InvoiceLineReads = Json.reads[InvoiceLine]



}
