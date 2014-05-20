package com.dinglabs.culturalinterviews

import java.io.{FileReader, PrintWriter, FileWriter, File}
import resource._
import au.com.bytecode.opencsv.{CSVReader, CSVWriter}
import au.com.bytecode.opencsv.CSVParser.{DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER}
import com.dinglabs.culturalinterviews.ImplicitHelpers.{PathUtil, PathToFile, FilenameToFile}
import com.dinglabs.culturalinterviews.html.Parser.extractContentFromHtml
import com.dinglabs.culturalinterviews.html.Content
import scalatags._
import scalatags.all._
import scalax.file.Path

object Main {

  def main(args: Array[String]) {
    println("Hello world!")

    val inputDir  = Path("input")
    val outputDir = Path("output")
    outputDir.deleteRecursively(force=true)
    outputDir.createDirectory(createParents=true)
    val itemsDir = (outputDir/"item").createDirectory()

    // copy over web-files directly
    (inputDir/"web-files").children().foreach{c =>
      val dest = outputDir/(c.relativize(c.parent.get))
      println(s"copying $c to $dest")
      c.copyTo(dest)
    }

    // open CSV file
    for (csvReader <- managed(new CSVReader(new FileReader(inputDir/"online_spreadsheet.csv"), DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, 1))) {
      val Array(number, section, subtitle, youtubeId, code, location, speaker, english, chineseSimp, chineseTrad, pinyin) = csvReader.readNext()
      val csvEntry = CsvEntry(number.toInt, section, subtitle, youtubeId, code, location, speaker, english, chineseSimp, chineseTrad, pinyin)
      val html = htmlForItem(csvEntry)
      writeToFile(html.toString, itemsDir/s"$number.html")
    }
  }

  case class CsvEntry(
                       number:      Int,
                       section:     String,
                       subtitle:    String,
                       youtubeId:   String,
                       code:        String,
                       location:    String,
                       speaker:     String,
                       english:     String,
                       chineseSimp: String,
                       chineseTrad: String,
                       pinyin:      String)

  def htmlForItem(e: CsvEntry) = {
    scalatags.all.html(
      head(
        "title".tag(s"${e.number}: ${e.speaker} - ${e.section} - ${e.subtitle}")
      ),
      body(
        h1(s"${e.number}: ${e.speaker} - ${e.section} - ${e.subtitle}",
           a(href:="#", "next")),
        iframe(width:="560",
               height:="315",
               src:="http://www.youtube.com/embed/P6S_1nCfjWA?showinfo=0&amp;rel=0&amp;theme=light&amp;modestbranding=1",
               "frameborder".attr:="0",
               "allowfullscreen".attr:="allowfullscreen"),
        div(e.english),
        div(e.chineseSimp),
        div(e.chineseTrad),
        div(e.pinyin)
      )
    )
  }

  def writeContentToFilesystem(c: Content, dir: Path) {
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

  /**
   * Extract data from HTML files and write to a CSV.
   */
  def htmlToCSV {
    val htmlFiles = "input/html".listFiles
    println(s"File count: ${htmlFiles.length}")

    val contents = htmlFiles.map(extractContentFromHtml)
    for (writer <- managed(new CSVWriter(new FileWriter("output.csv")));
         c <- contents) {
      println(s"${c.filename} ${c.location} ${c.speaker} ${c.pinyin.length} ${c.english.length} ${c.chineseSimp.length} ${c.chineseTrad.length}")
      writer.writeNext(Array(c.filename, c.location, c.speaker, c.english, c.chineseSimp, c.chineseTrad, c.pinyin))
    }
  }
}
