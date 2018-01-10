package org.tron.core

import java.io._
import java.util.Properties
import javax.inject.Inject

import com.typesafe.config.Config
import org.spongycastle.util.encoders.Hex
import org.tron.crypto.ECKey
import org.tron.utils.ByteArray

class NodeKeyFactory @Inject() (config: Config) {

  def build(): ECKey = {
    val dbDir = config.getString("database.directory")

    val file = new File(dbDir, "nodeId.properties")
    val props = new Properties
    if (file.exists()) {
      val r = new FileReader(file)
      try {
        props.load(r)
      } finally {
        if (r != null) r.close()
      }
    } else {
      file.getParentFile.mkdirs()

      val key = new ECKey()
      val privKeyBytes = key.getPrivKeyBytes

      val nodeIdPrivateKey = ByteArray.toHexString(privKeyBytes)

      props.setProperty("nodeIdPrivateKey", nodeIdPrivateKey)
      props.setProperty("nodeId", Hex.toHexString(key.getNodeId))

      val w = new FileWriter(file)
      try {
        props.store(w, "Generated NodeID.")
      }
      finally if (w != null) {
        w.close()
      }
    }
    val privateKey = props.getProperty("nodeIdPrivateKey")
    ECKey.fromPrivate(Hex.decode(privateKey))
  }
}
