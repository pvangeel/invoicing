package models

import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.libs.json.Json

case class InvoiceSummary(id: Long, invoiceNumber: String, customer: String, date: DateTime, total: BigDecimal)
case class Invoice(id: Option[Long], invoiceNumber: String, customer: Customer, date: DateTime, invoiceLines: Seq[InvoiceLine])


object InvoiceSummary {
  implicit val invoiceSummaryWrites = Json.writes[InvoiceSummary]
}

object Invoice {

  implicit val invoiceWrites = Json.writes[Invoice]
  implicit val invoiceReads = Json.reads[Invoice]


  def createOrUpdateInvoice(invoice: Invoice) = {
    invoice match {
      case Invoice(Some(id), _, _, _, _) => updateInvoice(invoice)
      case Invoice(None, _, _, _, _) => createInvoice(invoice)
    }
  }

  def createInvoice(invoice: Invoice) = {
    val customer: Customer = Customer.createOrUpdateCustomer(invoice.customer)
    
    DB.withConnection {
      implicit c =>
        val id: Option[Long] = SQL("insert into invoice(customerId, invoiceNumber, date) values ({customerId}, {invoiceNumber}, {date})")
        .on('customerId -> customer.id.get)
        .on('invoiceNumber -> invoice.invoiceNumber)
        .on('date -> invoice.date)
        .executeInsert()
        //TODO: persist invoice lines
        Invoice(id, invoice.invoiceNumber, customer, invoice.date, invoice.invoiceLines)
    }

  }


  def updateInvoice(invoice: Invoice) = invoice


  def findAllSummaries = DB.withConnection {
    implicit c =>
      SQL(
        """select invoice.id, invoice.invoiceNumber, customer.name as customerName, invoice.date, COALESCE(SUM(invoiceLine.quantity * invoiceLine.price), 0) as total
          | from invoice left outer join invoiceline on invoice.id = invoiceline.invoiceId, customer where invoice.customerId = customer.id
          | group by customer.name, invoice.id""".stripMargin)
      .as(summary *)
  }

  val summary = {
    get[Long]("id") ~ get[String]("invoiceNumber") ~ get[String]("customerName") ~ get[DateTime]("date") ~ get[BigDecimal]("total") map {
      case id ~ invoiceNumber ~ customerName ~ date ~ total => InvoiceSummary(id, invoiceNumber, customerName, date, total)
    }
  }

}
