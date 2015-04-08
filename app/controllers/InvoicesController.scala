package controllers

import models.{InvoiceNumber, Invoice}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat}
import play.api.libs.json.Json._
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, Controller}

object InvoicesController extends Controller {


  def invoicesSummaryList() = Action { implicit request =>
    Ok(toJson(Invoice.findAllSummaries)).as(JSON)
  }


  def addOrUpdateInvoice = Action { implicit request =>
    request.body.asJson.map(_.as[Invoice]).map { invoice =>
      Ok(toJson(Invoice.createOrUpdateInvoice(invoice))).as(JSON)
    }.getOrElse(InternalServerError)

  }

  def invoiceDetail(id: Long) = Action { implicit request =>
    Ok(toJson(Invoice.findById(id))).as(JSON)
  }

  def getNextInvoiceNumberForDate(query: String) = Action { implicit request =>
    val lastInvoiceNumberForDate: Option[String] = Invoice.findLastInvoiceNumberByInvoiceNumberStartsWith(query)
    val result = lastInvoiceNumberForDate.map {
      lastInvoiceNumber =>
        val invoiceNumber: InvoiceNumber = InvoiceNumber(lastInvoiceNumber)
        Seq(invoiceNumber.year, invoiceNumber.month, "%02d".format(invoiceNumber.sequenceNumber.toInt + 1)).mkString("-")
    }.getOrElse(query + "01")
    Ok(result)
  }

}
