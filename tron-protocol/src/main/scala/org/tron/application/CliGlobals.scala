package org.tron.application

import org.tron.core.Key

trait CliGlobals {

  this: Application =>

  var activeWallet: Option[Key] = None
}
