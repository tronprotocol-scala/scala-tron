package org

import org.tron.storage.DataSource

package object tron {

  /**
    * Blockchain DataSource
    */
  type BlockChainDb = DataSource[Array[Byte], Array[Byte]]

  /**
    * Transaction Identifier
    */
  type TXID = Array[Byte]

}
