package org.tron.application

import com.google.inject.Injector

case class Application(
  injector: Injector,
  services: List[Service] = List.empty) {

  def withService(service: Service) = copy(services = services :+ service)

  def start() = {
    services.foreach(_.start())
  }

  def stop() = {
    services.foreach(_.stop())
  }

}
