package com.dinglabs.culturalinterviews

import scalax.file.Path
import scala.xml.NodeSeq

object ImplicitHelpers {

  // http://stackoverflow.com/a/16256935/187145
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  implicit class PathUtil(val path: Path) {
    def readToString = path.lines(includeTerminator = true).mkString("")
  }

  implicit def XmlSelectors(ns: NodeSeq) = new {
    /** Find nodes having an attribute with the given value  */
    def \@(params: (String, String)) : NodeSeq = {
      val (attribName, expectedValue) = params
      ns filter { _ \ ("@" + attribName) exists (_.text == expectedValue) }
    }
  }
}
