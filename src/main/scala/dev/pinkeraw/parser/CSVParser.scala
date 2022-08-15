package dev.pinkeraw.parser

import dev.pinkeraw.util.Constant.{CR, CRLF, DELIMITER, LF}

import scala.collection.mutable.ArrayBuffer

class CSVParser(header: Boolean = true, valueParser: ValueParser) {
  val result = new ArrayBuffer[Array[Any]]()
  val lineBuffer = new ArrayBuffer[Any]()

  private var headerCols: Option[Array[String]] = None

  def accept(c: Char): Unit = {
    String.valueOf(c) match {
      case CR | LF | CRLF =>
        if (valueParser.isProcessingQuote) {
          valueParser.accept(c)
        } else {
          if (valueParser.isEmpty && lineBuffer.isEmpty) {
            // Ignore empty row
            lineBuffer.clear()
            valueParser.flush()
          } else {
            lineBuffer.append(valueParser.flush())
            val row = lineBuffer.toArray
            lineBuffer.clear()

            // num_of_cols of first row will be the base num_of_cols
            if (headerCols.isEmpty) {
              if (header) {
                headerCols = Some(row.map(_.toString))
              } else {
                headerCols = Some(Seq.range(0, row.length).map(num => s"_col$num").toArray)
                result.append(row)
              }
            } else {
              if (row.length != headerCols.get.length) {
                throw new Exception(s"Number of cols not match " +
                  s"between row and header (${row.length} vs ${headerCols.get.length})")
              } else {
                result.append(row)
              }
            }
          }
        }
      case DELIMITER =>
        if (valueParser.isProcessingQuote) {
          valueParser.accept(c)
        } else {
          lineBuffer.append(valueParser.flush())
        }
      case _ =>
        valueParser.accept(c)
    }
  }

  def toValueResult: Array[Array[Any]] = result.toArray

  def getHeader: Array[String] = headerCols.getOrElse(new Array[String](0))
}
