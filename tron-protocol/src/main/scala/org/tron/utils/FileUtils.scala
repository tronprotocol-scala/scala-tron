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
 */
package org.tron.utils

import java.io.{File, IOException}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util


object FileUtils {

  def getRelativeDirectory(): File = {
    new File(".").getCanonicalFile
  }

  def recursiveList(file: File): util.List[String] = {
    recursiveList(file.getAbsolutePath)
  }

  def recursiveList(path: String): util.List[String] = {
    val file = new File(path)
    require(file.exists(), "path must exist")

    val files = new util.ArrayList[String]

    Files.walkFileTree(file.toPath, new FileVisitor[Path]() {

      def preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = FileVisitResult.CONTINUE

      def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        files.add(file.toString)
        FileVisitResult.CONTINUE
      }
      def visitFileFailed(file: Path, exc: IOException) = FileVisitResult.CONTINUE

      def postVisitDirectory(dir: Path, exc: IOException) = FileVisitResult.CONTINUE
    })

    files
  }

  def recursiveDelete(file: File): Boolean = {
    recursiveDelete(file.getAbsolutePath)
  }

  def recursiveDelete(path: String): Boolean = {
    val file = new File(path)
    if (file.exists) { // check if the file is a directory
      if (file.isDirectory) if (file.list.length > 0) {
        for (s <- file.list) { // call deletion of file individually
          recursiveDelete(s"$path${System.getProperty("file.separator")}$s")
        }
      }
      file.setWritable(true)
      file.delete()
    } else {
      false
    }
  }
}
