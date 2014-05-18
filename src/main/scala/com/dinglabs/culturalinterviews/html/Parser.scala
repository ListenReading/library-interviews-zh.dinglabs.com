package com.dinglabs.culturalinterviews.html

import scala.xml.{Node, Elem, XML}
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import java.io.{FileInputStream, File}
import scala.io.Source
import com.dinglabs.culturalinterviews.ImplicitHelpers.XmlSelectors

object Parser {

  val loader = XML.withSAXParser(new SAXFactoryImpl().newSAXParser())

  def extractContentFromHtml(file: File) : Content = {
    val text = Source.fromFile(file).mkString

    val xml = loader.load(new FileInputStream(file))

    val speakerName = (xml\\"span"\@("class","shd")).head.text.split(":")(0).trim
    Content(
      """(?s).*\.\./movs/(.*?)\..*""".r.findFirstMatchIn(text).get.group(1),
      speakerName.split(",").reverse.map(_.trim).mkString(" "),
      (xml\\"span"\@("class", "sh2")).head.text,
      selectTranscriptContent(selectDiv(xml, "e_txt")),
      selectTranscriptContent(selectDiv(xml, "rc_txt")),
      selectTranscriptContent(selectDiv(xml, "sc_txt")),
      selectTranscriptContent(selectDiv(xml, "tc_txt")))
  }

  def selectDiv(xml: Elem, id: String) = (xml\\"div"\@("id",id)).head

  def selectTranscriptContent(div: Node): String = {
    val node = (div\\"p"\@("class","MsoPlainText")).lastOption orElse
               (div\\"span"\@("class","subj")).headOption      orElse
               (div\\"span"\@("lang","CH")).headOption

    node.get.text.trim
      .replaceAll("\\p{Z}+",    " ")  // multiple spaces
      .replaceAll("^\\p{Z}+",   "")   // leading spaces on lines
      .replaceAll("\\p{Z}+\\n", "\n") // trailing spaces on lines
      .replaceAll("\\n+",       "\n") // multiple newlines
  }
}
