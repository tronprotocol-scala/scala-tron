package org.tron.wallet

import org.tron.core.PublicKey
import org.tron.crypto.ECKey
import org.tron.utils.Utils

case class Wallet(
  address: PublicKey = PublicKey(new ECKey(Utils.getRandom).getAddress))
