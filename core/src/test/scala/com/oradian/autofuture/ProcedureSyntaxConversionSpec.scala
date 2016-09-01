package com.oradian.autofuture

import org.specs2.Specification

class ProcedureSyntaxConversionSpec extends Specification {
  def is = s2"""
    Injections into classes
      abstract declaration in a trait            $declInTrait
      definition in a trait                      $defnInTrait
      abstract declaration in an abstract class  $declInAbstractClass
      definition in a class                      $defnInClass
      definition in an object                    $defnInObject

    Noops
      noop in empty                              $noopEmpty
      noop in not applicable                     $noopNa
      noop in decl                               $noopDecl
      noop in defn                               $noopDefn

    Spaces in definitions
      0 spaces                                   $defn0Space
      1 space                                    $defn1Space
      2 spaces                                   $defn2Space
      whitespace & comment noise                 $testNoise

    Complex parameters
      different kinds of parameters              $testParameters
      nested conversions with duck typing        $testDuckTyping
  """

  private[this] val Marker = java.util.UUID.randomUUID.toString
  private[this] val DECL = Marker + ": Unit"
  private[this] val DEFN = Marker + ": Unit ="

  private[this] val BOTH_TYPE = Marker + "BothType"
  private[this] val BOTH_BODY = Marker + "BothBody"

  private[this] def testOne(source: String) = {
    val expected = source.replaceAll(Marker, "")
    val origin = source.replaceAll(DEFN, "").replaceAll(DECL, "")

    AutoFuture.process(origin, List(ProcedureSyntaxConversion), AutoFuture.Result.Noop) ====
      AutoFuture.Result.Success(expected)
  }

  private[this] def testNone(source: String) =
    AutoFuture.process(source, List(ProcedureSyntaxConversion), AutoFuture.Result.Noop) ====
      AutoFuture.Result.Noop

  private[this] def testBoth(source: String) = {
    val declTest = source.replaceAll(BOTH_TYPE, DECL).replaceAll(BOTH_BODY, "")
    val defnTest = source.replaceAll(BOTH_TYPE, DEFN).replaceAll(BOTH_BODY, "{ body }")
    testOne(declTest) and testOne(defnTest)
  }

  def noopEmpty = testNone("")
  def noopNa    = testNone("""object Foo { def x: Int = 1 }""")
  def noopDecl  = testNone("""trait Foo { def x: Unit }""")
  def noopDefn  = testNone("""object Foo { def x: Unit = println("Hi!") }""")

  // Injections into classes
  def declInTrait         = testOne(s"""trait Foo { def x${DECL} }""")
  def defnInTrait         = testOne(s"""trait Foo { def x${DEFN} { body } }""")
  def declInAbstractClass = testOne(s"""abstract class Foo { def x${DECL} }""")
  def defnInClass         = testOne(s"""class Foo { def x${DEFN} { body } }""")
  def defnInObject        = testOne(s"""object Foo { def x${DEFN} { body } }""")

  // Spaces in definitions
  def defn0Space = testOne(s"""object Foo { def x${DEFN}{ body }}""")
  def defn1Space = testOne(s"""object Foo { def x${DEFN} { body } } """)
  def defn2Space = testOne(s"""object Foo { def  x${DEFN}  { body }  }  """)

  def testNoise = testBoth(s"""trait Foo {
    def x${BOTH_TYPE} /* Comment 1 */ // Comment 2 //
  ${BOTH_BODY}}""")

  // Complex signatures
  def testParameters = testBoth(s"""trait Echo {
    def noParams${BOTH_TYPE}${BOTH_BODY}
    def someParams(s: String)(u: UUID)${BOTH_TYPE} ${BOTH_BODY}
    def paramsForEveryone()()()()(i: Int)()()()()${BOTH_TYPE} // lots of parameters
  ${BOTH_BODY}
  }""")

  def testDuckTyping = testBoth(s"""trait Echo {
    def quackTwice(duck: {def quack${DECL}})${BOTH_TYPE} // two replacements
  ${BOTH_BODY}
  }""")
}
