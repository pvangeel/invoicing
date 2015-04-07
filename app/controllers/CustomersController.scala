package controllers

import models.Customer
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

object CustomersController extends Controller {

  def customerList() = Action { implicit request =>
    Ok(toJson(Customer.findAll)).as(JSON)
  }

  def findByNameLike(query: String) = Action { implicit request =>
    Ok(toJson(Customer.findByNameLike(query))).as(JSON)
  }

  def addOrUpdateCustomer() = Action { implicit request =>
    request.body.asJson.map(_.as[Customer]).map { customer =>
      Ok(toJson(Customer.createOrUpdateCustomer(customer))).as(JSON)
    }.getOrElse(InternalServerError)

  }
}
