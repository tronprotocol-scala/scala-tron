package org.tron.application

import java.nio.file.Paths
import javax.inject.Singleton

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.{Config, ConfigFactory}
import org.tron.core.{Blockchain, BlockchainImpl, Constant, Key}
import org.tron.storage.{DbFactory, LevelDbFactory, RedisDbFactory}

class Module(mode: String = "test") extends AbstractModule {

  def configure() = {

  }

  @Provides
  @Singleton
  def buildConfig(): Config = {
    mode match {
      case Constant.TEST =>
        ConfigFactory.load(Constant.TEST_CONF)
      case _ =>
        ConfigFactory.load(Constant.NORMAL_CONF)
    }
  }

  @Provides
  @Singleton
  def buildDbFactory(): DbFactory = {
    val config = buildConfig()
    val file = config.getString(Constant.DATABASE_DIR)
    val name = Paths.get(file)

    val dbType = config.getString(Constant.DATABASE_TYPE)
    dbType match {
      case Constant.DATABASE_TYPE_LEVELDB =>
        new LevelDbFactory(name)
      case Constant.DATABASE_TYPE_REDIS =>
        new RedisDbFactory(name)
    }
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
