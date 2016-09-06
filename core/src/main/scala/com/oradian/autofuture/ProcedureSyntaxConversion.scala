package com.oradian.autofuture

import scala.meta._
import parsers.Parsed.{Success, Error}
import tokens.Token
import scala.annotation.tailrec

object ProcedureSyntaxConversion extends AutoFuture {
  private[this] case class Injection(offset: Int, defn: Boolean)

  /* We want to track the last token after names and parameters to place the explicit type definition.
   * If we just use the `rt.pos` we may end up with suboptimal placement such as:
   *
   * trait Foo {
   *   def x
   * }
   *
   * becoming
   *
   * trait Foo {
   *   def x
   * : Unit}
   *
   * Also, we cannot just trim whitespaces to the right because of potential line comments.
   * See the accompanying spec for more details */
  private[this] def locateInjection(tree: Tree, rt: Type.Name, defn: Boolean): Injection = {
    val tokens = tree.tokens
    val lastOffset = rt.pos.start.offset

    @tailrec
    def findOffset(index: Int, lastSignificant: Int): Int = {
      val token = tokens(index)
      val tokenOffset = token.pos.start.offset

      if (tokenOffset >= lastOffset) {
        tokens(lastSignificant).pos.start.offset
      } else {
        token match {
          case _: Token.Space
             | _: Token.Tab
             | _: Token.LF
             | _: Token.CR
             | _: Token.FF
             | _: Token.Comment =>
            findOffset(index + 1, lastSignificant)
          case _ => findOffset(index + 1, index + 1)
        }
      }
    }
    val offset = findOffset(0, -1)
    assert(offset > -1, "Could not locate last offset!")
    Injection(offset, defn)
  }

  def apply(source: String): AutoFuture.Result = {
    source.parse[Source] match {
      case Success(parsed) =>
        val injections = parsed collect {
          /* Transform abstract definitions, match if return type tokens are empty (procedure syntax) */
          case tree @ Decl.Def(_, _, _, _, rt @ Type.Name("Unit")) if rt.tokens.isEmpty =>
            locateInjection(tree, rt, false)

          /* Transform definitions, match if return type is defined and tokens are empty (procedure syntax)  */
          case tree @ Defn.Def(_, _, _, _, Some(rt @ Type.Name("Unit")), _) if rt.tokens.isEmpty =>
            locateInjection(tree, rt, true)
        }

        if (injections.isEmpty) {
          AutoFuture.Result.Noop
        } else {
          val sb = new StringBuilder
          var last = 0
          for (injection <- injections.sortBy(_.offset)) {
            val before = source.substring(last, injection.offset)
            (sb ++= before
                ++= (if (injection.defn) ": Unit =" else ": Unit"))
            last = injection.offset
          }

          sb ++= source.substring(last)
          AutoFuture.Result.Success(sb.toString)
        }

      case Error(pos, message, details) =>
        AutoFuture.Result.Error(s"At line ${pos.start.line}: $message")
    }
  }
}
