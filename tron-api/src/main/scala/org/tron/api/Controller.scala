package org.tron.api

import org.tron.core.{Blockchain, UTXOSet}
import org.tron.utils.KeyUtils
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
    case POST(p"/wallet") => walletCreate
  }

  def walletBalance(address: String) = Action {

    val balance = uTXOSet.getBalance(address)

    Ok(Json.obj(
      "address" -> address,
      "balance" -> balance
    ))
  }

  def walletCreate = Action {

    val key = KeyUtils.generateKey

    Ok(Json.obj(
      "address" -> key.addressHex,
      "private_key" -> key.privateKeyCompressed
    ))
  }

}
