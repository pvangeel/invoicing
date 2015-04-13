package controllers

import models.{InvoiceLine, InvoiceNumber, Invoice}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat}
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import play.mvc.Security.Authenticated

@Authenticated
object InvoicesController extends Controller with Secured {


  def invoicesSummaryList() = Authenticated { implicit request =>
    println(request.userName)
    Ok(toJson(Invoice.findAllSummaries)).as(JSON)
  }


  def addOrUpdateInvoice = Authenticated { implicit request =>
    request.body.asJson.map(_.as[Invoice]).map { invoice =>
      Ok(toJson(Invoice.createOrUpdateInvoice(invoice))).as(JSON)
    }.getOrElse(InternalServerError)

  }

  def invoiceDetail(id: Long) = Authenticated { implicit request =>
    Ok(toJson(Invoice.findById(id))).as(JSON)
  }

  def addOrUpdateInvoiceLine(invoiceId: Long) = Authenticated { implicit request =>
    request.body.asJson.map(_.as[InvoiceLine]).map { invoiceLine =>
      Ok(toJson(Invoice.createOrUpdateInvoiceLineForInvoice(invoiceId, invoiceLine))).as(JSON)
    }.getOrElse(InternalServerError)

  }

  def getNextInvoiceNumberForDate(query: String) = Authenticated { implicit request =>
    val lastInvoiceNumberForDate: Option[String] = Invoice.findLastInvoiceNumberByInvoiceNumberStartsWith(query)
    val result = lastInvoiceNumberForDate.map {
      lastInvoiceNumber =>
        val invoiceNumber: InvoiceNumber = InvoiceNumber(lastInvoiceNumber)
        Seq(invoiceNumber.year, invoiceNumber.month, "%02d".format(invoiceNumber.sequenceNumber.toInt + 1)).mkString("-")
    }.getOrElse(query + "01")
    Ok(result)
  }

  def deleteInvoiceLine(invoiceId: Long, invoiceLineId: Long) = Authenticated { implicit request =>
    Invoice.deleteInvoiceLine(invoiceId, invoiceLineId)
    Ok
  }
}
