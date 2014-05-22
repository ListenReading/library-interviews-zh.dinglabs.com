package com.dinglabs.culturalinterviews.csv

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVParser._
import java.io.{FileReader, File}
import resource._
import scala.collection.JavaConversions._

object Parser {

  def parseCsv(f: File) : List[CsvEntry] = {
    // read all from CSV file
    val csv = managed(new CSVReader(new FileReader(f), DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, 1)).acquireAndGet(_.readAll())
    csv.map { e => CsvEntry(e(0).toInt, e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8), e(9), e(10)) }.toList
  }
}
