package org.tron.wallet

import org.tron.core.Key
import org.tron.crypto.ECKey
import org.tron.utils.Utils

case class Wallet(
  key: ECKey = new ECKey(Utils.getRandom)) {
  def address = Key(key)
}