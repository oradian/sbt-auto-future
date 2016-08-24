package com.oradian.autofuture

import scala.meta._

trait AutoFuture {
  def apply(source: Tree): Tree
}

object AutoFuture {
  val tasks = IndexedSeq[AutoFuture](
    ProcedureSyntaxConversion
  )

  sealed trait Result
  object Result {
    case class Error(error: String) extends Result
    case class Success(body: String) extends Result
    case object Noop extends Result
  }

  def apply(source: String): Result = {
    source.parse[Source] match {
      case Parsed.Success(parsed) =>
        /* Chain all tranformations defined in tasks */
        val transformed = tasks.foldLeft(parsed: Tree) { (current, task) => task(current) }

        /* Detect noop instead of recreating the same source from the parsed tree */
        if (transformed eq parsed) Result.Noop else Result.Success(transformed.toString)

      case Parsed.Error(pos, message, details) =>
        Result.Error(message)
    }
  }
}
