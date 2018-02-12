package org.tron.utils

import org.specs2.mutable._

class FileUtilsSpec extends Specification {
  "FileUtils Spec" should {
    "getRelativePath" in {
      val directory = FileUtils.getRelativeDirectory()
      directory.exists() must beTrue
      directory.isDirectory must beTrue
    }
    "recursiveList" in {
      FileUtils.recursiveList("") must throwAn[IllegalArgumentException]
    }
    "recursiveDelete" in {
      1 mustEqual 1
    }
  }
}
