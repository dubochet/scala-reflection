package scala.metaprogramming.symtab

import scala.reflect.generic

import java.lang.{ ClassLoader => JClassLoader }

class Universe(val reflectionLoader: JClassLoader, val libraryLoader: JClassLoader)
        extends generic.Universe
                with generic.Constants
                with Symbols
                with Types
                with Scopes
                with Names
                with StdNames
                with Trees
                with AnnotationInfos
                with StdDefinitions
{ thisUniverse =>

  def this(reflectionLoader: JClassLoader) =
    this(reflectionLoader, reflectionLoader)

  type Position = Int

  val NoPosition = 0

  object completers extends reflection.SymbolCompleters {
    val universe: thisUniverse.type = thisUniverse
  }

}
