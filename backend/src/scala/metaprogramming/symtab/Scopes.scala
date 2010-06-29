package scala.metaprogramming.symtab

import scala.reflect.generic
import scala.collection._

trait Scopes extends generic.Scopes { self: Universe =>

  class Scope extends AbsScope {
    private val content = mutable.Set.empty[Symbol]
    def enter(sym: Symbol) = {
      content += sym
      sym
    }
    def iterator: Iterator[Symbol] =
      content.iterator
    def lookup(name: Name): Symbol =
      lookupAll(name).toList match {
        case Nil => NoSymbol
        case s :: ss => s
      }
    def lookupAll(name: Name): Iterator[Symbol] =
      iterator filter { _.name == name}
  }

  def newScope = new Scope

  /*
  class OptimisticScope extends AbsScope {

    def enter(sym: Any): Any = null

    def iterator: Iterator[Any] = null

    def lookup(name: Name): Symbol =
      lookupAll(name).toList match {
        case Nil => NoSymbol
        case s :: ss => s
      }

    def lookupAll(name: Name): Iterator[Symbol] =
      iterator filter { _.name == name}

  }
  */

}