package controllers

import models.Customer
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

object CustomersController extends Controller with Secured {

  def customerList() = Authenticated { implicit request =>
    Ok(toJson(Customer.findAll)).as(JSON)
  }

  def findByNameLike(query: String) = Authenticated { implicit request =>
    Ok(toJson(Customer.findByNameLike(query))).as(JSON)
  }

  def addOrUpdateCustomer() = Authenticated { implicit request =>
    request.body.asJson.map(_.as[Customer]).map { customer =>
      Ok(toJson(Customer.createOrUpdateCustomer(customer))).as(JSON)
    }.getOrElse(InternalServerError)

  }
}
