package com.oradian.autofuture

import scala.annotation.tailrec

trait AutoFuture {
  def apply(source: String): AutoFuture.Result
}

object AutoFuture {
  val tasks = List[AutoFuture](
    ProcedureSyntaxConversion
  , AdaptTupleArgumentsExplicitly
  )

  sealed trait Result
  object Result {
    case class Error(error: String) extends Result
    case class Success(body: String) extends Result
    case object Noop extends Result
  }

  def apply(source: String): Result =
    process(source, tasks, Result.Noop)

  @tailrec
  private[autofuture] def process(source: String, tasks: List[AutoFuture], last: Result): Result = {
    tasks match {
      case Nil =>
        last

      case head :: tail =>
        head(source) match {
          case error: Result.Error =>
            error

          case newLast @ Result.Success(source) =>
            process(source, tail, newLast)

          case Result.Noop =>
            last
        }
    }
  }
}
