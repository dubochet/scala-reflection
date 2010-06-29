package scala.metaprogramming.symtab
package reflection

import scala.collection._
import scala.reflect.generic
import java.io.IOException

abstract class ClassUnpickler extends generic.UnPickler {

  val global: Universe
  import global._

  def scan(bytes: Array[Byte], offset: Int, classRoot: Symbol, moduleRoot: Symbol, filename: String): Unit =
    new UniverseScan(bytes, offset, classRoot, moduleRoot, filename).run()

  class UniverseScan(bytes: Array[Byte], offset: Int, classRoot: Symbol, moduleRoot: Symbol, filename: String) extends Scan(bytes, offset, classRoot, moduleRoot, filename) {

    /** pre: `fun` points to a symbol with an overloaded type.
      * Selects the overloaded alternative of `fun` which best matches given
      * argument types `argtpes` and result type `restpe`. Stores this alternative as
      * the symbol of `fun`. */
    def inferMethodAlternative(fun: Tree, argtpes: List[Type], restpe: Type) =
      // TODO: What needs to be done here?
      error("I don't know how to infer method alternatives")

    /** Create a lazy type which when completed returns type at index `i`. */
    def newLazyTypeRef(i: Int): LazyType = new LazyTypeRef(i)

    /** Create a lazy type which when completed returns type at index `i` and sets alias
      * of completed symbol to symbol at index `j` */
    def newLazyTypeRefAndAlias(i: Int, j: Int): LazyType = new LazyTypeRefAndAlias(i, j)

    /** A lazy type which when completed returns type at index `i`. */
    private class LazyTypeRef(i: Int) extends LazyType {
      override def complete(sym: Symbol) : Unit = {
        sym.info = at(i, readType)
      }
    }

    /** A lazy type which when completed returns type at index `i` and sets alias
      * of completed symbol to symbol at index `j`. */
    private class LazyTypeRefAndAlias(i: Int, j: Int) extends LazyTypeRef(i) {
      override def complete(sym: Symbol) {
        super.complete(sym)
        var alias: Symbol = at(j, readSymbol)
        // TODO: What needs to be done here?
        /*
        if (alias hasFlag OVERLOADED) {
          atPhase(currentRun.picklerPhase) {
            alias = alias suchThat (alt => sym.tpe =:= sym.owner.thisType.memberType(alt))
          }
        }
        sym.setAlias(alias)
        */
      }
    }

  }

}