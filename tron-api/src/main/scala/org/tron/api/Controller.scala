package org.tron.api

import org.tron.api.JsonFormat._
import org.tron.core.{Blockchain, BlockchainIterator, PublicKey, UTXOSet}
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
    case GET(p"/wallet/$key") => walletBalance(key)
    case GET(p"/block") => blocks
  }

  def walletBalance(key: String) = Action {

    val ecKEy = ECKey.fromPublicOnly(ByteArray.fromHexString(key))
    val balance = uTXOSet.getBalance(PublicKey(ecKEy))

    Ok(Json.obj(
      "address" -> ByteArray.toHexString(ecKEy.getAddress),
      "balance" -> balance
    ))
  }


  def blocks = Action { request =>

    val limit = request.getQueryString("limit").map(_.toInt).getOrElse(50)

    val iterator = new BlockchainIterator(blockchain)

    val blockList = iterator.toList.take(limit)

    Ok(Json.obj(
      "blocks" -> blockList
    ))
  }

}
