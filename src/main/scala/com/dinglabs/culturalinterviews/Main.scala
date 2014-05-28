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
import scala.collection.JavaConversions._
import com.dinglabs.culturalinterviews.csv.{CsvEntry, Parser}
import scalaz._
import Scalaz._

object Main {

  val ITEM_FOLDER = "item"

  def main(args: Array[String]) {
    println("Hello world!")

    val inputDir = Path("input")
    val outputDir = Path("output")
    outputDir.deleteRecursively(force = true)
    outputDir.createDirectory(createParents = true)
    val itemsDir = (outputDir / ITEM_FOLDER).createDirectory()

    // copy over web-files directly
    (inputDir / "web-files").children().foreach { c =>
      val dest = outputDir / (c.relativize(c.parent.get))
      println(s"copying $c to $dest")
      c.copyTo(dest)
    }

    // open CSV file
    val items = Parser.parseCsv(inputDir/"online_spreadsheet.csv")

    // render a page for each item
    for (List(prev, Some(current), next) <- ((None :: items.map(Some(_))) :+ None).sliding(3)) {
      val html = htmlForItem(prev, current, next)
      writeToFile(html.toString, itemsDir/current.htmlFilename)
    }

    // one index for ALL videos, organized by sections and subtitle?
    writeToFile(htmlForIndex(items).toString, outputDir/"index.html")

    // TODO: generate indexes
    // one index for each person?
    // one index for each location?
  }

  implicit class CsvEntryAddons(c: CsvEntry) {
    def htmlFilename = s"${c.number}.html"
    def title = s"${c.number}: ${c.speaker} - ${c.section} - ${c.subtitle}"
    def origUrl = s"http://www.laits.utexas.edu/orkelm/chinese/vid/${c.code}.html"
  }

  def htmlForItem(prev: Option[CsvEntry], e: CsvEntry, next: Option[CsvEntry]) = {
    scalatags.all.html(
      head(
        "title".tag(e.title),
        link(rel:="stylesheet", `type`:="text/css", href:="../css/item.css")

      ),
      body(
        div(`class`:="youtube-container-placeholder"),
        div(`class`:="youtube-container",
          iframe(`class`:="youtube-iframe",
            src:=s"http://www.youtube.com/embed/${e.youtubeId}?showinfo=0&rel=0&theme=light&modestbranding=1&autoplay=1",
            "frameborder".attr:="0",
            "allowfullscreen".attr:="allowfullscreen"
          ),
          div(`class`:="titleSection",
            div(`class`:="title", e.title),
            div(`class`:="location", e.location),
            div(`class`:="navLinks",
              prev.map(p => a(href:=p.htmlFilename, title:=p.title, `class`:="navLink", "< Previous")),
              a(`class`:="navLink", href:="../index.html", "Index"),
              next.map(n => a(href:=n.htmlFilename, title:=n.title, `class`:="navLink", "Next >"))
            )
          )
        ),
        table(`class`:="transcript",
          tr(
            td(`class`:="english", e.english),
            td(e.chineseSimp)
          )
        ),
        table(
          tr(
            td(e.chineseTrad),
            td(e.pinyin)
          )
        ),
        hr(),
        div("Content original source: ", a(href:=e.origUrl, s"${e.code}.html")),
        div(footerLinkToOriginal),
        script(src:="../vendor/tether/tether.0.6.5.min.js"),
        script(raw(
          """
          new Tether({ element:".youtube-container", target:".youtube-container-placeholder", attachment:"top left", targetAttachment:"top left", constraints: [{to:'window', pin:true}]});
          setTimeout(function(){ Tether.position(); } , 0);
          """))
      )
    )
  }

  def footerLinkToOriginal = div("View original ", a(href:="http://www.laits.utexas.edu/orkelm/chinese/", "Cultural Interviews with Chinese-Speaking Professionals")," from University of Texas at Austin.")

  def htmlForIndex(items: Seq[CsvEntry]) = {
    val pageTitle = "Cultural Interviews with Chinese-Speaking Professionals"
    scalatags.all.html(
      head(
        "title".tag(pageTitle)
      ),
      body(
        h1(pageTitle),
        for ((section, sectionItems) <- items.groupBy(_.section).toList) yield {
          div(
            h2(section),
            ul(
              for ((subsection, subsectionItems) <- sectionItems.groupBy(_.subtitle).toList) yield {
                val links = for (i <- subsectionItems.toList) yield {
                  a(href:=s"$ITEM_FOLDER/${i.htmlFilename}", i.speaker)
                }
                li(span(subsection),": ", links.intersperse(span(", ")))
              }
            )
          )
        },
        footerLinkToOriginal
      )
    )
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
