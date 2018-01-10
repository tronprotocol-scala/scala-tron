package org.tron.cli

import org.tron.cli.commands.Command

case class Config(
  peerType: String,
  command: Option[Command] = None)