package dev.pinkeraw

import dev.pinkeraw.parser.{CSVParser, ValueParser}

import scala.io.Source

object Application {
  def main(args: Array[String]): Unit = {
    val filePath = args(0) // CSV file path
    val header = args(1).toBoolean // true if the file contains header

    val file = Source.fromFile(filePath)
    val valueParser = new ValueParser(true)
    val csvParser = new CSVParser(header, valueParser)
    try {
      file.foreach(csvParser.accept)

      val rows = csvParser.toValueResult

      // print as TSV
      println(csvParser.getHeader.mkString("\t"))
      rows.foreach(row => println(row.mkString("\t")))
    } finally {
      file.close()
    }
  }
}
