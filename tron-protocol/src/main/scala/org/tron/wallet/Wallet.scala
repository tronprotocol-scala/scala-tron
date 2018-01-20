package org.tron.wallet

import org.tron.core.PublicKey
import org.tron.crypto.ECKey
import org.tron.utils.Utils

case class Wallet(
  key: ECKey = new ECKey(Utils.getRandom)) {
  def address = PublicKey(key)
}