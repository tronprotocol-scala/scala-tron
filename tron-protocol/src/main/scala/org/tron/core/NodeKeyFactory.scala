package org.tron.core

import java.io._
import java.util.Properties
import javax.inject.Inject

import com.typesafe.config.Config
import org.spongycastle.util.encoders.Hex
import org.tron.crypto.ECKey
import org.tron.utils.ByteArrayUtils
import scala.collection.JavaConverters._

class NodeKeyFactory @Inject() (config: Config) {
  val dbDir = config.getString("database.directory")
  val file = new File(dbDir, "nodeId.properties")

  def load(): Option[Map[String, String]] = {
    if (file.exists()) {
      val props = new Properties

      val r = new FileReader(file)
      try {
        props.load(r)
        val fields = props.propertyNames().asScala.map {
          case name: String =>
            (name, props.getProperty(name))
        }
        Some(fields.toMap)
      } finally {
        if (r != null) r.close()
      }
    } else {
      None
    }
  }

  def save(values: Map[String, String]) = {
    val props = new Properties
    file.getParentFile.mkdirs()

    values.foreach {
      case (key, value) =>
        props.setProperty(key, value)
    }

    val w = new FileWriter(file)
    try {
      props.store(w, "Generated NodeID.")
    }
    finally if (w != null) {
      w.close()
    }
  }

  def build(): ECKey = {

    val data = load() match {
      case Some(props) =>
        props
      case None =>
        val key = new ECKey()
        val privKeyBytes = key.getPrivKeyBytes

        val nodeIdPrivateKey = ByteArrayUtils.toHexString(privKeyBytes)

        val values = Map(
          "nodeIdPrivateKey" -> nodeIdPrivateKey,
          "nodeId" -> Hex.toHexString(key.getNodeId))

        save(values)
        values
    }

    val privateKey = data("nodeIdPrivateKey")
    ECKey.fromPrivate(Hex.decode(privateKey))
  }
}
