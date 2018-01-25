package org.tron.application

import com.google.inject.Guice

object AppFactory {

  def buildInjector = Guice.createInjector(new Module())

}
