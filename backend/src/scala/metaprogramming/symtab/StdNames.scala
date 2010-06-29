package scala.metaprogramming.symtab

import scala.reflect.generic

trait StdNames extends generic.StdNames { self: Universe =>

  val nme = new StandardNames {
    val NOSYMBOL    = newTermName("<none>")
  }


}