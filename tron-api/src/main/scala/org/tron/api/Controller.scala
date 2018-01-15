package org.tron.api

import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._
import Results._

class Controller {

  val router: Router = Router.from {
    case GET(p"/hello") => main
  }

  def main = Action {
    Ok("Hello World")
  }

}
