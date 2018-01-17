package org.tron.utils

import org.tron.core.PublicKey
import org.tron.crypto.ECKey

object KeyUtils {

  def newKey = {
    PublicKey(new ECKey())
  }

}
