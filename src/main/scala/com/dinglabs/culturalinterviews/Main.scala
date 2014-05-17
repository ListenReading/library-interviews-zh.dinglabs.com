package com.dinglabs.culturalinterviews

import com.dinglabs.culturalinterviews.ImplicitHelpers._
import java.io.{ByteArrayInputStream, InputStream, FileInputStream, File}
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import scala.io.Source
import scala.xml.{Node, Elem, XML}

object Main {

  def main(args: Array[String]) {
    println("Hello world!")

    val htmlFiles = new File("input/html").listFiles
    println(s"File count: ${htmlFiles.length}")
    for (file <- htmlFiles) {
      println(file.getName)
      val c = extractContentFromHtml(file)
      println(s"${c.filename} ${c.location} ${c.speakerName} ${c.pinyin.length} ${c.english.length} ${c.chinese_simp.length} ${c.chinese_trad.length}")
    }
  }

  val loader = XML.withSAXParser(new SAXFactoryImpl().newSAXParser())

  case class Content(
               filename:     String,
               speakerName:  String,
               location:     String,
               english:      String,
               pinyin:       String,
               chinese_simp: String,
               chinese_trad: String)

  def s2is(s: String) : InputStream = new ByteArrayInputStream(s.getBytes)

  def extractContentFromHtml(file: File) : Content = {
    val text = Source.fromFile(file).mkString

    val xml = loader.load(new FileInputStream(file))

    Content(
      """(?s).*\.\./movs/(.*?)\..*""".r.findFirstMatchIn(text).get.group(1),
      (xml\\"span").filter(s => (s\"@class").text == "shd").head.text.split(":")(0).trim,
      (xml\\"span").filter(s => (s\"@class").text == "sh2").head.text,
      selectTranscriptContent(selectDiv(xml, "e_txt")),
      selectTranscriptContent(selectDiv(xml, "rc_txt")),
      selectTranscriptContent(selectDiv(xml, "sc_txt")),
      selectTranscriptContent(selectDiv(xml, "tc_txt")))
  }

  def selectDiv(xml: Elem, id: String) = (xml\\"div").filter(div => (div\"@id").text == id).head

  def selectTranscriptContent(div: Node): String = {
    val ps = div \\ "p"
    val s = ps.filter(p => (p\"@class").text == "MsoPlainText").lastOption.getOrElse(
            ps.filter(p => (p\"@class").text == "MsoPlainText").head
    )
    s.text.trim
      .replaceAll("\\p{Z}+",    " ")  // multiple spaces
      .replaceAll("^\\p{Z}+",   "")   // leading spaces on lines
      .replaceAll("\\p{Z}+\\n", "\n") // trailing spaces on lines
      .replaceAll("\\n+",       "\n") // multiple newlines
  }

}