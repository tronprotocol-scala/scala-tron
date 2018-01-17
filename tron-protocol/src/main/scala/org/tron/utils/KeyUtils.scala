package org.tron.utils

import org.tron.core.PublicKey
import org.tron.crypto.ECKey

object KeyUtils {

  def newKey = {
    val key = new ECKey()
    PublicKey(key.getAddress)
  }

}
