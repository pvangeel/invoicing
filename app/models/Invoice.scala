package models

import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.libs.json.{Writes, Reads, Json}

case class InvoiceSummary(id: Long, invoiceNumber: String, customer: String, date: DateTime, total: BigDecimal)
case class Invoice(id: Option[Long], invoiceNumber: String, customer: Customer, date: DateTime, invoiceLines: Seq[InvoiceLine])
case class InvoiceLine(id: Option[Long], product: Product, price: BigDecimal, quantity: Long, vat: BigDecimal)

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

  def deleteInvoiceLine(invoiceId: Long, invoiceLineId: Long) = DB.withConnection {
    implicit c =>
      SQL("delete from invoiceLine where id = {id} and invoiceId = {invoiceId}")
      .on('id -> invoiceLineId)
      .on('invoiceId -> invoiceId)
      .execute()
  }


  def createInvoiceLine(invoiceId: Long, invoiceLine: InvoiceLine) = DB.withConnection {
    implicit c =>
      val product: Product = Product.createOrUpdateProduct(invoiceLine.product)
      val id: Option[Long]= SQL("insert into invoiceLine(invoiceId, productId, quantity, price, vat) values ({invoiceId}, {productId}, {quantity}, {price}, {vat})")
        .on('invoiceId -> invoiceId)
        .on('quantity -> invoiceLine.quantity)
        .on('productId -> product.id.get)
        .on('price -> invoiceLine.price)
        .on('vat -> invoiceLine.vat)
        .executeInsert()
      InvoiceLine(id, product, invoiceLine.price, invoiceLine.quantity, invoiceLine.vat)
  }

  def updateInvoiceLine(invoiceLine: InvoiceLine) = DB.withConnection {
    implicit c =>
      val product: Product = Product.createOrUpdateProduct(invoiceLine.product)
      SQL("update invoiceLine set (productId, quantity, price, vat) = ({productId}, {quantity}, {price}, {vat}) where id = {id}")
        .on('id -> invoiceLine.id)
        .on('productId -> product.id)
        .on('quantity -> invoiceLine.quantity)
        .on('price -> invoiceLine.price)
        .on('vat -> invoiceLine.vat)
        .executeUpdate()

      InvoiceLine(invoiceLine.id, product, invoiceLine.price, invoiceLine.quantity, invoiceLine.vat)
  }

  def createOrUpdateInvoiceLineForInvoice(invoiceId: Long, invoiceLine: InvoiceLine) = invoiceLine match {
    case InvoiceLine(Some(id), _, _, _, _) => updateInvoiceLine(invoiceLine)
    case InvoiceLine(None, _, _, _, _) => createInvoiceLine(invoiceId, invoiceLine)
  }

  def findLastInvoiceNumberByInvoiceNumberStartsWith(query: String) = DB.withConnection {
    implicit c =>
      SQL("select invoiceNumber from invoice where invoice.invoiceNumber like {query} order by invoiceNumber desc limit 1")
        .on('query -> s"$query%")
        .as(str("invoiceNumber") singleOpt)
  }

  def getInvoiceLinesForInvoice(invoiceId: Long): Seq[InvoiceLine] = DB.withConnection {
    implicit c =>
      SQL("select * from invoiceline where invoiceId = {invoiceId}")
        .on('invoiceId -> invoiceId)
        .as(fullInvoiceLine *)
  }

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
        .as(fullInvoice single)
  }

  def findAllSummaries = DB.withConnection {
    implicit c =>
      SQL(
        """select invoice.id, invoice.invoiceNumber, customer.name as customerName, invoice.date, COALESCE(SUM(invoiceLine.quantity * invoiceLine.price), 0) as total
          | from invoice left outer join invoiceline on invoice.id = invoiceline.invoiceId, customer where invoice.customerId = customer.id
          | group by customer.name, invoice.id order by id desc""".stripMargin)
      .as(summary *)
  }

  val summary = {
    get[Long]("id") ~ get[String]("invoiceNumber") ~ get[String]("customerName") ~ get[DateTime]("date") ~ get[BigDecimal]("total") map {
      case id ~ invoiceNumber ~ customerName ~ date ~ total => InvoiceSummary(id, invoiceNumber, customerName, date, total)
    }
  }

  val fullInvoice = {
    get[Long]("id") ~ get[String]("invoiceNumber") ~ get[Long]("customerId") ~ get[DateTime]("date") map {
      case id ~ invoiceNumber ~ customerId ~ date => Invoice(Option(id), invoiceNumber, Customer.findById(customerId), date, Invoice.getInvoiceLinesForInvoice(id))
    }
  }

  val fullInvoiceLine = {
    get[Long]("id") ~ get[Long]("productId") ~ get[Int]("quantity") ~ get[BigDecimal]("price") ~ get[BigDecimal]("vat") map {
      case id ~ productId ~ quantity ~ price ~ vat => InvoiceLine(Option(id), Product.findById(productId), price, quantity, vat)
    }
  }

}
