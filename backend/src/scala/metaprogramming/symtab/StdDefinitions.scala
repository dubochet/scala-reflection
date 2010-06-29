package scala.metaprogramming.symtab

import scala.reflect.generic
import generic.Flags._

trait StdDefinitions extends generic.StandardDefinitions { thisUniverse: Universe =>

  val definitions = new AbsDefinitions {

    // outer packages and their classes
    lazy val RootPackage: Symbol = {
      val rp = NoSymbol.newValue(nme.ROOTPKG)
      rp.setFlag(FINAL | MODULE | PACKAGE | JAVA)
      rp.info = RootClass.info
      RootClass.sourceModule = rp
      rp
    }
    lazy val RootClass: Symbol = {
      val rc = NoSymbol.newModuleClass(nme.ROOT).setFlag(FINAL | MODULE | PACKAGE | JAVA)
      rc.info = new completers.PackageSymbolCompleter("/")
      rc
    }

    lazy val EmptyPackage: Symbol =
      RootClass.newPackage(nme.EMPTY_PACKAGE_NAME).setFlag(FINAL)
    lazy val EmptyPackageClass: Symbol =
      EmptyPackage.moduleClass

    lazy val ScalaPackage: Symbol      = error("non implemented")
    lazy val ScalaPackageClass: Symbol = error("non implemented")

    // top types
    lazy val AnyClass: Symbol     = error("non implemented")
    lazy val AnyValClass: Symbol  = error("non implemented")
    lazy val AnyRefClass: Symbol  = error("non implemented")
    lazy val ObjectClass: Symbol  = error("non implemented")

    // bottom types
    lazy val NullClass: Symbol    = error("non implemented")
    lazy val NothingClass: Symbol = error("non implemented")

    // the scala value classes
    lazy val UnitClass: Symbol    = error("non implemented")
    lazy val ByteClass: Symbol    = error("non implemented")
    lazy val ShortClass: Symbol   = error("non implemented")
    lazy val CharClass: Symbol    = error("non implemented")
    lazy val IntClass: Symbol     = error("non implemented")
    lazy val LongClass: Symbol    = error("non implemented")
    lazy val FloatClass: Symbol   = error("non implemented")
    lazy val DoubleClass: Symbol  = error("non implemented")
    lazy val BooleanClass: Symbol = error("non implemented")

    // fundamental reference classes
    lazy val SymbolClass: Symbol  = error("non implemented")
    lazy val StringClass: Symbol  = error("non implemented")
    lazy val ClassClass: Symbol   = error("non implemented")

    // fundamental modules
    lazy val PredefModule: Symbol = error("non implemented")

    // fundamental type constructions
    def ClassType(arg: Type): Type = error("non implemented")

    /** The string representation used by the given type in the VM. */
    def signature(tp: Type): String = error("non implemented")

    /** Is symbol one of the value classes? */
    def isValueClass(sym: Symbol): Boolean = error("non implemented")

    /** Is symbol one of the numeric value classes? */
    def isNumericValueClass(sym: Symbol): Boolean = error("non implemented")

    // standard library classes
    lazy val AnnotationClass: Symbol = error("non implemented")
    lazy val ClassfileAnnotationClass: Symbol = error("non implemented")

  }

}