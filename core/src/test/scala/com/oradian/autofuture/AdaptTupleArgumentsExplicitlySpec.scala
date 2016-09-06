package com.oradian.autofuture

import org.specs2.Specification

class AdaptTupleArgumentsExplicitlySpec extends Specification {
  def is = s2"""
    Adapted arrow tuple
      pair                       $arrowPair
      tripple                    $arrowTripple

    Adapted tuples
      pair                       $tuplePair
      tripple                    $tupleTripple
      tuple23                    $tuple23

    Nested tuples
      nestedSome                 $nestedSome
      nestedDeep                 $nestedDeep

    Noops
      noop in empty              $noopEmpty
      noop in not applicable     $noopNa
      noop if no need            $noopNoNeed
  """

  private[this] val Marker = java.util.UUID.randomUUID.toString
  private[this] val `(` = Marker + "("
  private[this] val `)` = Marker + ")"

  private[this] def testNone(source: String) =
    AutoFuture.process(source, List(AdaptTupleArgumentsExplicitly), AutoFuture.Result.Noop) ====
      AutoFuture.Result.Noop

  private[this] def test(source: String) = {
    val original = source.replace(`(`, "").replace(`)`, "")
    val expected = source.replace(Marker, "")

    AutoFuture.process(original, List(AdaptTupleArgumentsExplicitly), AutoFuture.Result.Noop) ====
      AutoFuture.Result.Success(expected)
  }

  def arrowPair = test(s"""
    trait Foo {
      Map(1 -> (${`(`}1,2${`)`}))
    }""")

  def arrowTripple = test(s"""
    trait Foo {
      Map(1 -> (${`(`} 1, 2, 3 ${`)`}))
    }""")

  def appendPair = test(s"""
    trait Foo {
      Seq(1 -> 2, 3 -> 4) :+ (${`(`}5, 6${`)`})
    }""")

  def tuplePair = test(s"""
    trait Foo {
      Some(${`(`}1,2${`)`})
    }""")

  def tupleTripple = test(s"""
    trait Foo {
      Some(${`(`} 1, 2, 3 ${`)`})
    }""")

  def tuple23 = test(s"""
    trait Foo {
      Some(${`(`}
        1,2,3,4,
        5, 6, 7, 8,
        9,  10,  11,  12,
        13,   14,   15,   16,   17,
        18,    19,    20,    21,    22,    "23 - Currently unsupported by Scala 2.11.x"
      ${`)`})
    }""")

  def nestedSome = test(s"""
    trait Foo {
      Some(${`(`}1, Some(${`(`}2, 3${`)`}), 4${`)`})
    }""")

  def nestedDeep = test(s"""
    trait Foo {
      val deep = Option(
        Some(
          Option(${`(`}
            Some(${`(`}
              1
            , 2.toString + Some(${`(`}2, 2${`)`})
            , s"$${Some(${`(`} 3, 3, 3 ${`)`})}" // should adapt
            ${`)`})
          , Some(${`(`}
              "Some(1)"
            , "Some(2, 2)"      // should not adapt
            , "Some( 3, 3, 3 )" // should not adapt
            ${`)`})
          ${`)`})
        )
      )
    }""")

  def noopEmpty  = testNone("")
  def noopNa     = testNone("""object Foo { val a = Some(1) }""")
  def noopNoNeed = testNone("""object Foo { val a = Some((1, 2)) }""")
}
