package controllers

import models.Product
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, Action}

object ProductsController extends Controller {

  def productList() = Action { implicit request =>
    Ok(toJson(Product.findAll)).as(JSON)
  }

  def addOrUpdateProduct() = Action { implicit request =>
    request.body.asJson.map(_.as[Product]).map { product =>
      Ok(toJson(Product.createOrUpdateProduct(product))).as(JSON)
    }.getOrElse(InternalServerError)

  }


}
