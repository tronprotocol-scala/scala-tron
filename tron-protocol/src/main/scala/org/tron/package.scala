package org

import org.tron.storage.DataSource

package object tron {

  type BlockChainDb = DataSource[Array[Byte], Array[Byte]]

}
