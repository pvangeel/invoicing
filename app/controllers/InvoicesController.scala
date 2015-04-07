package controllers

import models.Invoice
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

object InvoicesController extends Controller {


  def invoicesSummaryList() = Action { implicit request =>
    Ok(toJson(Invoice.findAllSummaries)).as(JSON)
  }

}
