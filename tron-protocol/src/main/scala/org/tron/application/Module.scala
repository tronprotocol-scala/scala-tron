package org.tron.application

import javax.inject.Singleton

import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.{Config, ConfigFactory}

class Module(mode: String = "test") extends AbstractModule {
  def configure() = {

  }

  @Provides
  @Singleton
  def buildConfig(): Config = {
    mode match {
      case "test" =>
        ConfigFactory.load("tron-test.conf")
      case _ =>
        ConfigFactory.load("tron.conf")
    }
  }
}
