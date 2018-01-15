package org.tron.cli

import org.tron.cli.commands.Command

case class CommandConfig(
  command: Option[Command] = None)
