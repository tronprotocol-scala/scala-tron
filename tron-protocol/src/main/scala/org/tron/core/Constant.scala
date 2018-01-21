/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *//*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.core

import org.tron.utils.ByteArrayUtils

object Constant { // whole
  val LAST_HASH = ByteArrayUtils.fromString("lastHash")
  val DIFFICULTY = "2001"

  // DB
  val BLOCK_DB_NAME = "block_data"
  val TRANSACTION_DB_NAME = "transaction_data"
  // kafka
  val TOPIC_BLOCK = "block"
  val TOPIC_TRANSACTION = "transaction"
  val PARTITION = 0
  //config
  val NORMAL = "normal"
  val TEST = "test"
  val NORMAL_CONF = "tron.conf"
  val TEST_CONF = "tron-test.conf"
  val DATABASE_DIR = "database.directory"

  val GENESIS_COINBASE_DATA = "0x10"

  val SYSTEM_NAME = "TronCluster"
}
