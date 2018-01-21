package org.tron.utils

import org.tron.core.Key
import org.tron.crypto.ECKey

object KeyUtils {

  def generateKey = Key(new ECKey())

  def fromPrivateKey(key: String) = {
    val wallet = org.tron.crypto.ECKey.fromPrivate(org.tron.core.Base58.decodeToBigInteger(key), true)
    Key(wallet)
  }

}
