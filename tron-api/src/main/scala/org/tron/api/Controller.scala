package org.tron.api

import org.tron.core.{Blockchain, Key, UTXOSet}
import org.tron.crypto.ECKey
import org.tron.utils.ByteArray
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._

class Controller(
  blockchain: Blockchain,
  uTXOSet: UTXOSet) {

  val router: Router = Router.from {
    case GET(p"/wallet/$address") => walletBalance(address)
  }

  def walletBalance(address: String) = Action {

    val balance = uTXOSet.getBalance(address)

    Ok(Json.obj(
      "address" -> address,
      "balance" -> balance
    ))
  }

}
