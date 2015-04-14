package controllers

import controllers.InvoicesController._
import models.Invoice
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc._


object ApplicationController extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  case class LoginForm(username: String, password: String)

  implicit val LoginFormReads = Json.reads[LoginForm]

  def login = Action { implicit request =>
    request.body.asJson.map(_.as[LoginForm]).map { login =>
      Ok(login.username).withSession(Security.username -> login.username).as(JSON)
    }.getOrElse(InternalServerError)
  }

  def isLoggedIn = Authenticated { implicit request =>
    Ok(request.userName).as(JSON)
  }

  def logout = Authenticated { implicit request =>
    Ok.withNewSession
  }
}