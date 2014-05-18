package com.dinglabs.culturalinterviews

import java.io.{PrintWriter, FileWriter, File}
import resource._
import au.com.bytecode.opencsv.CSVWriter
import com.dinglabs.culturalinterviews.ImplicitHelpers.{FileUtil, FilenameToFile}
import com.dinglabs.culturalinterviews.html.Parser.extractContentFromHtml
import com.dinglabs.culturalinterviews.html.Content


object Main {

  def main(args: Array[String]) {
    println("Hello world!")

    val htmlFiles = "input/html".listFiles
    println(s"File count: ${htmlFiles.length}")

    val contents = htmlFiles.map(extractContentFromHtml)
    //htmlFilesToCSV(contents, "output.csv")
    val outputDir = new File("output")
    for ((content, i) <- contents.zipWithIndex) {
      writeContentToFilesystem(content, outputDir/s"$i")
    }
  }

  def writeContentToFilesystem(c: Content, dir: File) {
    dir.mkdirs()
    writeToFile(c.chineseSimp, dir/"chinese_s.txt")
    writeToFile(c.chineseTrad, dir/"chinese_t.txt")
    writeToFile(c.english,     dir/"english.txt")
    writeToFile(c.pinyin,      dir/"pinyin.txt")
    writeToFile(c.location,    dir/"location.txt")
    writeToFile(c.speaker,     dir/"speaker.txt")
    writeToFile(c.filename,    dir/"filename.txt")
  }

  def writeToFile(s: String, f: File) {
    for (out <- managed(new PrintWriter(f, "UTF-8"))) {
      out.print(s)
    }
  }

  def htmlFilesToCSV(contents: Seq[Content], outCsv: File) {
    for (writer <- managed(new CSVWriter(new FileWriter(outCsv)));
         c <- contents) {
      println(s"${c.filename} ${c.location} ${c.speaker} ${c.pinyin.length} ${c.english.length} ${c.chineseSimp.length} ${c.chineseTrad.length}")
      writer.writeNext(Array(c.filename, c.location, c.speaker, c.english, c.chineseSimp, c.chineseTrad, c.pinyin))
    }
  }
}
