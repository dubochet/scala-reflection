package scala.metaprogramming.symtab

import scala.reflect.generic

import java.io.PrintWriter

trait Trees extends generic.Trees { self: Universe =>

  def newTreePrinter(out: PrintWriter): AbsTreePrinter = new AbsTreePrinter(out) {
    def print(tree: Tree) = error("non implemented")
    def flush() = error("non implemented")
  }

  /** A synthetic term holding an arbitrary type.  Not to be confused with
    * with TypTree, the trait for trees that are only used for type trees.
    * TypeTree's are inserted in several places, but most notably in
    * <code>RefCheck</code>, where the arbitrary type trees are all replaced by
    * TypeTree's. */
  case class TypeTree() extends AbsTypeTree {
    private var orig: Tree = null
    private[Trees] var wasEmpty: Boolean = false

    def original: Tree = orig
    def setOriginal(tree: Tree): this.type = { orig = tree; setPos(tree.pos); this }

    override def defineType(tp: Type): this.type = {
      wasEmpty = isEmpty
      setType(tp)
    }
  }

  object TypeTree extends TypeTreeExtractor

}