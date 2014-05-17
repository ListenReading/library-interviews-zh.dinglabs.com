package com.dinglabs.culturalinterviews

import java.io.File
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import scala.io.Source
import com.dinglabs.culturalinterviews.ImplicitHelpers._

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

    //val filename : String = text match { case r"(?s).*\.\./movs/(.*?)$filename\..*" => filename }
    val driver = new HtmlUnitDriver
    driver.get(file.toURI.toString)
    Content(
      """(?s).*\.\./movs/(.*?)\..*""".r.findFirstMatchIn(text).get.group(1),
      driver.findElementByXPath("//span[@class='shd']").getText.split(":")(0).trim,//driver.findElementByTagName("title").getText.split(":")(1).trim,
      driver.findElementByXPath("//span[@class='sh2']").getText,
      selectTranscriptContent(selectDiv(text, "e_txt")),
      selectTranscriptContent(selectDiv(text, "rc_txt")),
      selectTranscriptContent(selectDiv(text, "sc_txt")),
      selectTranscriptContent(selectDiv(text, "tc_txt")))
  }

  def selectDiv(text: String, id: String) : String = {
    s"(?s).*<DIV id=.$id.>(.*?)</DIV>.*".r.findFirstMatchIn(text).get.group(1)
  }

  def selectTranscriptContent(input: String): String = {
    val s = """(?s).*\n\s*\n(.*)\n\s*\n.*""".r.findFirstMatchIn(input).map(_.group(1)).orElse(
            """(?s).*((\s*[^<].*?\n)*).*""".r.findFirstMatchIn(input).map(_.group(1)))
    s.get.trim.replaceAll("\\s+", " ")
  }

}
