package org.tron.api

import play.core.server.AkkaHttpServerComponents

import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._
import play.api.{BuiltInComponents, NoHttpFiltersComponents}
import play.core.server.AkkaHttpServerComponents

import scala.concurrent.Future

class HttpServer extends AkkaHttpServerComponents with BuiltInComponents with NoHttpFiltersComponents {

  lazy val controller = injector.instanceOf[Controller]

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