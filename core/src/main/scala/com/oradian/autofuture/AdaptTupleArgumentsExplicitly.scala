package com.oradian.autofuture

import scala.meta._
import scala.meta.parsers.Parsed.{Error, Success}
import scala.meta.tokens.Token

object AdaptTupleArgumentsExplicitly extends AutoFuture {
  private[this] case class Injection(open: Int, close: Int) {
    require(open < close, "Open offest must be lower than close offset")
  }

  private[this] def locateInjection(tree: Tree): Injection = {
    val open = tree.tokens.find {
      case _: Token.LeftParen => true
      case _ => false
    }.getOrElse(sys.error("Could not find first open parenthesis")).pos.end.offset

    val close = tree.tokens.reverse.find {
      case _: Token.RightParen => true
      case _ => false
    }.getOrElse(sys.error("Could not find last closed parenthesis")).pos.start.offset

    Injection(open, close)
  }

  def apply(source: String): AutoFuture.Result = {
    source.parse[Source] match {
      case Success(parsed) =>
        val injections = parsed collect {
          /* Transform Some.apply(<multiple parameters>) */
          case tree @ Term.Apply(Term.Name("Some"), args) if args.size > 1 =>
            locateInjection(tree)

          /* Transform Option.apply(<multiple parameters>) */
          case tree @ Term.Apply(Term.Name("Option"), args) if args.size > 1 =>
            locateInjection(tree)

          /* Transform arrow association */
          case tree @ Term.ApplyInfix(_, Term.Name("->"), _, args) if args.size > 1 =>
            locateInjection(tree)
        }

        if (injections.isEmpty) {
          AutoFuture.Result.Noop
        } else {
          val sb = new StringBuilder
          var last = 0

          val opens = injections map { injection => injection.open -> '(' }
          val closes = injections map { injection => injection.close -> ')' }

          for ((offset, paren) <- (opens ++ closes).sortBy(_._1)) {
            (sb ++= source.substring(last, offset)
                += paren)
            last = offset
          }

          sb ++= source.substring(last)
          AutoFuture.Result.Success(sb.toString)
        }

      case Error(pos, message, details) =>
        AutoFuture.Result.Error(s"At line ${pos.start.line}: $message")
    }
  }
}
