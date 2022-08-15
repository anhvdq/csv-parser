package dev.pinkeraw.parser

import dev.pinkeraw.util.Constant.DOUBLE_QUOTE

import scala.collection.mutable

class ValueParser(val trimValue: Boolean = true) {
  private val buffer: mutable.StringBuilder = new mutable.StringBuilder()
  private var isQuoted: Boolean = false // This should be handled by a stack
  private var isEndQuoted: Boolean = false // This should be handled by a stack

  def isEmpty: Boolean = buffer.isEmpty

  def isProcessingQuote: Boolean = isQuoted && !isEndQuoted // This should be handled by a stack

  def accept(c: Char): Unit = {
    // a field may start with or without a double quote but there is no quote inside the double quote
    // => a field have 2 quotes or none at any position
    String.valueOf(c) match {
      case DOUBLE_QUOTE =>
        if (!isQuoted) {
          isQuoted = true
        } else if (!isEndQuoted) {
          isEndQuoted = true
        } else {
          throw new IllegalStateException("Double quotes can not appear inside quotes")
        }
      case _ =>
        buffer.append(c)
    }
  }

  def flush(): String = {
    val result = if (trimValue) buffer.result().trim else buffer.result()
    isQuoted = false
    isEndQuoted = false
    buffer.clear()
    result
  }
}
