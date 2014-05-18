package com.dinglabs.culturalinterviews

import java.io.{FileWriter, File}
import resource._
import au.com.bytecode.opencsv.CSVWriter
import com.dinglabs.culturalinterviews.html.Parser.extractContentFromHtml

object Main {

  def main(args: Array[String]) {
    println("Hello world!")

    val htmlFiles = new File("input/html").listFiles
    println(s"File count: ${htmlFiles.length}")

    htmlFilesToCSV(htmlFiles, new File("output.csv"))
  }

  def htmlFilesToCSV(htmlFiles: Array[File], outCsv: File) {
    for (writer <- managed(new CSVWriter(new FileWriter(outCsv)));
         file <- htmlFiles) {
      val c = extractContentFromHtml(file)
      println(s"${c.filename} ${c.location} ${c.speakerName} ${c.pinyin.length} ${c.english.length} ${c.chinese_simp.length} ${c.chinese_trad.length}")
      writer.writeNext(Array(c.filename, c.location, c.speakerName, c.english, c.chinese_simp, c.chinese_trad, c.pinyin))
    }
  }
}
