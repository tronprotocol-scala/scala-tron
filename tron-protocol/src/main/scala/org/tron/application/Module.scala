package org.tron.application

import java.nio.file.Paths
import javax.inject.Singleton

import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.{Config, ConfigFactory}
import org.tron.storage.DbFactory

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

  @Provides
  @Singleton
  def buildDbFactory(): DbFactory = {
    val config = buildConfig()
    val file = config.getString("database.directory")
    new DbFactory(Paths.get(file))
  }
}
