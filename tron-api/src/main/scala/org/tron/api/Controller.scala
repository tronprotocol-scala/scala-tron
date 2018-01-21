package org.tron.api

import org.tron.core.{Blockchain, PublicKey, UTXOSet}
import org.tron.crypto.ECKey
import org.tron.utils.ByteArrayUtils
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._

class Controller(
  blockchain: Blockchain,
  uTXOSet: UTXOSet) {

  val router: Router = Router.from {
    case GET(p"/wallet/$key") => walletBalance(key)
  }

  def walletBalance(key: String) = Action {

    val ecKEy = ECKey.fromPublicOnly(ByteArrayUtils.fromHexString(key))
    val balance = uTXOSet.getBalance(PublicKey(ecKEy))

    Ok(Json.obj(
      "address" -> ByteArrayUtils.toHexString(ecKEy.getAddress),
      "balance" -> balance
    ))
  }

}
