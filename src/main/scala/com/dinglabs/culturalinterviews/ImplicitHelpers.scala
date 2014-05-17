package com.dinglabs.culturalinterviews

import scalax.file.Path

object ImplicitHelpers {

  // http://stackoverflow.com/a/16256935/187145
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  implicit class PathUtil(val path: Path) {
    def readToString = path.lines(includeTerminator = true).mkString("")
  }

}
