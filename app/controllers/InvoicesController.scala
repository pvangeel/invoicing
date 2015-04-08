package controllers

import models.Invoice
import org.joda.time.DateTime
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

}
