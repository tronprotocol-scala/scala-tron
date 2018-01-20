package org.tron.application

import java.nio.file.Paths
import javax.inject.Singleton

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.{Config, ConfigFactory}
import org.tron.core.{Blockchain, BlockchainImpl, Constant, PublicKey}
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

  @Provides
  @Singleton
  def buildBlockchain(): Blockchain = {
    val dbFactory = buildDbFactory()
    new BlockchainImpl(dbFactory.build(Constant.BLOCK_DB_NAME))
  }

  @Singleton
  @Provides
  def buildActorSystem(): ActorSystem = {
    ActorSystem(Constant.SYSTEM_NAME)
  }
}
