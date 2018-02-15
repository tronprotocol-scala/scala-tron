package org.tron.application

import com.google.inject.Injector
import com.typesafe.config.Config

case class Application(
  injector: Injector,
  config: Config,
  services: List[Service] = List.empty) {

  def withService(service: Service) = {
    copy(services = services :+ service)
  }

  def start() = {
    services.foreach(_.start())
  }

  def stop() = {
    services.foreach(_.stop())
  }

}
