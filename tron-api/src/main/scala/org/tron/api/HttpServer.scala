package org.tron.api

import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._
import play.api.routing.Router
import play.api.{BuiltInComponents, NoHttpFiltersComponents}
import play.core.server.AkkaHttpServerComponents

import scala.concurrent.Future

class HttpServer(controller: Controller) extends AkkaHttpServerComponents with BuiltInComponents with NoHttpFiltersComponents {

  override lazy val router: Router = controller.router

  override lazy val httpErrorHandler = new DefaultHttpErrorHandler(
    environment,
    configuration,
    sourceMapper,
    Some(router)) {
    override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
      Future.successful(Results.NotFound("Nothing was found!"))
    }


  }
}