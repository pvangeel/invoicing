package controllers

import play.api.mvc._

trait Secured {

  case class AuthenticatedRequest(val userName: String, request: Request[AnyContent]) extends WrappedRequest(request)

  def Authenticated(f: AuthenticatedRequest => Result) = {
    Action { request =>
      request.session.get(Security.username).map { user =>
        f(AuthenticatedRequest(user, request))
      }.getOrElse(Results.Unauthorized)
    }
  }
}