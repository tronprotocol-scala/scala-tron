package org.tron.api

import org.tron.core.{Blockchain, PublicKey, UTXOSet}
import org.tron.crypto.ECKey
import org.tron.utils.ByteArrayUtils
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
    case GET(p"/address/$address") => addressBalance(address)
    case POST(p"/address") => addressCreate
  }

  def addressBalance(address: String) = Action {

    val balance = uTXOSet.getBalance(address)

    Ok(Json.obj(
      "address" -> address,
      "balance" -> balance
    ))
  }

  def addressCreate = Action {

    val key = KeyUtils.generateKey

    Ok(Json.obj(
      "address" -> key.addressHex,
      "private_key" -> key.privateKeyCompressed
    ))
  }

}
