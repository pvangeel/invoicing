package models

import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.libs.json.{Writes, Reads, Json}

case class InvoiceSummary(id: Long, invoiceNumber: String, customer: String, date: DateTime, total: BigDecimal)
case class Invoice(id: Option[Long], invoiceNumber: String, customer: Customer, date: DateTime, invoiceLines: Seq[InvoiceLine])
case class InvoiceLine(id: Option[Long], productId: Long, price: BigDecimal, quantity: Long, vat: BigDecimal)

object InvoiceLine {
  implicit val InvoiceLineWrites = Json.writes[InvoiceLine]
  implicit val InvoiceLineReads = Json.reads[InvoiceLine]
}

object InvoiceSummary {
  implicit val invoiceSummaryWrites = Json.writes[InvoiceSummary]
}

case class InvoiceNumber(year: String, month: String, sequenceNumber: String) {
  override def toString = Seq(year, month, sequenceNumber).mkString("-")
}

object InvoiceNumber {
  def apply(invoiceNumber: String): InvoiceNumber = {
    val parts: Array[String] = invoiceNumber.split("-")
    this(parts(0), parts(1), parts(2))
  }
}


object Invoice {
  def getInvoiceLinesForInvoice(id: Long): Seq[InvoiceLine] = Seq()

  implicit val yourJodaDateReads: Reads[DateTime] = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites: Writes[DateTime] = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

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


  def updateInvoice(invoice: Invoice) = invoice //TODO: implementeren

  def findById(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from invoice where invoice.id = {id}")
        .on('id -> id)
        .as(full *)
  }

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

  val full = {
    get[Long]("id") ~ get[String]("invoiceNumber") ~ get[Long]("customerId") ~ get[DateTime]("date") map {
      case id ~ invoiceNumber ~ customerId ~ date => Invoice(Option(id), invoiceNumber, Customer.findById(customerId), date, Invoice.getInvoiceLinesForInvoice(id))
    }
  }

}
