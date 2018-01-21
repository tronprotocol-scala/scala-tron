package org.tron.api

import org.tron.protos.core.TronBlock.Block
import play.api.libs.json.{JsSuccess, Json, Writes}
import org.tron.utils.ByteStringUtils._


object JsonFormat {

  implicit val txOutputWrites = new Writes[Block] {
    override def writes(block: Block) = {
      Json.obj(
        "hash" -> block.blockHeader.get.hash.hex,
        "transactions" -> block.transactions.map { transaction =>
          Json.obj(
            "hash" -> transaction.id.hex,
            "vin" -> transaction.vin.map { vin =>
              Json.obj(
                "key" -> vin.pubKey.hex,
                "id" -> vin.txID.hex,
                "vout" -> vin.vout,
                "signature" -> vin.signature.hex,
              )
            },
            "vout" -> transaction.vout.map { vout =>
              Json.obj(
                "key" -> vout.pubKeyHash.hex,
                "value" -> vout.value,
              )
            }
          )
        }
      )
    }
  }

}
