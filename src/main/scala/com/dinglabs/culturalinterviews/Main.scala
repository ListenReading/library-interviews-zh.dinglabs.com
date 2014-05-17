package com.dinglabs.culturalinterviews

import com.dinglabs.culturalinterviews.ImplicitHelpers._
import java.io.{FileWriter, FileInputStream, File}
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import resource._
import scala.io.Source
import scala.xml.{Node, Elem, XML}
import au.com.bytecode.opencsv.CSVWriter

object Main {

  def main(args: Array[String]) {
    println("Hello world!")

    val htmlFiles = new File("input/html").listFiles
    println(s"File count: ${htmlFiles.length}")
    for (writer <- managed(new CSVWriter(new FileWriter("output.csv")));
         file <- htmlFiles) {
      val c = extractContentFromHtml(file)
      println(s"${c.filename} ${c.location} ${c.speakerName} ${c.pinyin.length} ${c.english.length} ${c.chinese_simp.length} ${c.chinese_trad.length}")
      writer.writeNext(Array(c.filename, c.location, c.speakerName, c.english, c.chinese_simp, c.chinese_trad, c.pinyin))
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

  def extractContentFromHtml(file: File) : Content = {
    val text = Source.fromFile(file).mkString

    val xml = loader.load(new FileInputStream(file))

    var speakerName = (xml\\"span"\@("class","shd")).head.text.split(":")(0).trim
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
    val s = (div\\"p"\@("class","MsoPlainText")).lastOption
            .orElse((div\\"span"\@("class","subj")).headOption)
            .orElse((div\\"span"\@("lang","CH")).headOption)
    s.get.text.trim
      .replaceAll("\\p{Z}+",    " ")  // multiple spaces
      .replaceAll("^\\p{Z}+",   "")   // leading spaces on lines
      .replaceAll("\\p{Z}+\\n", "\n") // trailing spaces on lines
      .replaceAll("\\n+",       "\n") // multiple newlines
  }

}
