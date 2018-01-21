package org.tron.utils

import org.tron.core.Key
import org.tron.crypto.ECKey

object KeyUtils {

  def generateKey = Key(new ECKey())

}
