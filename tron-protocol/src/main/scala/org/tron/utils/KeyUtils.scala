package org.tron.utils

import org.tron.core.Key
import org.tron.crypto.ECKey

object KeyUtils {

  def generateKey = Key(new ECKey())

  def fromPrivateKey(key: String) = {
    require(key.nonEmpty, "private key can't be empty")

    val wallet = org.tron.crypto.ECKey.fromPrivate(org.tron.core.Base58.decodeToBigInteger(key))
    Key(wallet)
  }

}
