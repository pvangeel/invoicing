package controllers

import models.{Customer, Product}
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, Action}

object ProductsController extends Controller with Secured {

  def productList() = Authenticated { implicit request =>
    Ok(toJson(Product.findAll)).as(JSON)
  }

  def addOrUpdateProduct() = Authenticated { implicit request =>
    request.body.asJson.map(_.as[Product]).map { product =>
      Ok(toJson(Product.createOrUpdateProduct(product))).as(JSON)
    }.getOrElse(InternalServerError)

  }

  def findByDescriptionLike(query: String) = Authenticated { implicit request =>
    Ok(toJson(Product.findByDescriptionLike(query))).as(JSON)
  }


}
