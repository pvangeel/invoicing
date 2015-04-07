package models

import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.libs.json.Json

case class InvoiceSummary(id: Long, invoiceNumber: String, customer: String, date: DateTime, total: BigDecimal)
case class Invoice(id: Option[Long], customer: Customer, invoiceLines: Seq[InvoiceLine])


object InvoiceSummary {
  implicit val invoiceSummaryWrites = Json.writes[InvoiceSummary]
}

object Invoice {

  implicit val invoiceWrites = Json.writes[Invoice]
  implicit val invoiceReads = Json.reads[Invoice]


  def createOrUpdateInvoice(invoice: Invoice) = {
    println(invoice)
    invoice
  }


  def findAllSummaries = DB.withConnection {
    implicit c =>
      SQL(
        """select invoice.id, invoice.invoiceNumber, customer.name as customerName, invoice.date, SUM(invoiceLine.quantity * invoiceLine.price) as total
          | from invoice, invoiceLine, customer
          | where invoice.customerId = customer.id and invoiceLine.invoiceId = invoice.id
          | group by customer.name, invoice.id""".stripMargin)
      .as(summary *)
  }

  val summary = {
    get[Long]("id") ~ get[String]("invoiceNumber") ~ get[String]("customerName") ~ get[DateTime]("date") ~ get[BigDecimal]("total") map {
      case id ~ invoiceNumber ~ customerName ~ date ~ total => InvoiceSummary(id, invoiceNumber, customerName, date, total)
    }
  }

}
