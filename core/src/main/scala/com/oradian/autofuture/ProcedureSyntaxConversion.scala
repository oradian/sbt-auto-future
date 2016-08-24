package com.oradian.autofuture

import scala.meta._

object ProcedureSyntaxConversion extends AutoFuture {
  def apply(source: Tree) = source transform {
    /* Transform abstract definitions, match if return type tokens are empty (procedure syntax) */
    case tree @ Decl.Def(_, _, _, _, decltpe @ Type.Name("Unit")) if decltpe.tokens.isEmpty =>
      /* copying decltpe forces injection of explicit return type `: Unit` */
      tree.copy(decltpe = decltpe)

    /* Transform definitions, match if return type is defined and tokens are empty (procedure syntax)  */
    case tree @ Defn.Def(_, _, _, _, decltpe @ Some(rt @ Type.Name("Unit")), _) if rt.tokens.isEmpty =>
      /* copying decltpe forces injection of explicit return type `: Unit = ` */
      tree.copy(decltpe = decltpe)
  }
}
