package com.dinglabs.culturalinterviews

import scalax.file.Path
import scala.xml.NodeSeq
import java.io.File

object ImplicitHelpers {

  // http://stackoverflow.com/a/16256935/187145
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  implicit class PathUtil(val path: Path) {
    def readToString = path.lines(includeTerminator = true).mkString("")
    def toFile = new File(path.toURI)
  }

  implicit def PathToFile(p: Path) = p.toFile

  implicit def FilenameToFile(fn: String) = new File(fn)
  implicit def FileToPath(f: File) = Path(f)

  implicit class FileUtil(f: File) {
    def /(path: String) : File = new File(f, path)
  }

  implicit class XmlSelectors(ns: NodeSeq) {
    /** Find nodes having an attribute with the given value  */
    def \@(params: (String, String)) : NodeSeq = {
      val (attribName, expectedValue) = params
      ns filter { _ \ ("@" + attribName) exists (_.text == expectedValue) }
    }
  }
}
