package scala.metaprogramming.symtab
package reflection

import java.lang.{ ClassLoader => JClassLoader, Class => JClass }

abstract class SymbolCompleters { thisCompleters =>

  val universe: Universe
  import universe._

  def reflectionLoader: JClassLoader = universe.reflectionLoader
  def libraryLoader: JClassLoader = universe.libraryLoader

  type ScalaSigAnnot = scala.reflect.ScalaSignature
  lazy val scalaSigAnnotJClass =
    libraryLoader.loadClass("scala.reflect.ScalaSignature").asInstanceOf[JClass[ScalaSigAnnot]]

  type ScalaLongSigAnnot = scala.reflect.ScalaLongSignature
  lazy val scalaLongSigAnnotJClass =
    libraryLoader.loadClass("scala.reflect.ScalaLongSignature").asInstanceOf[JClass[ScalaLongSigAnnot]]

  case class MissingRequirementException(val req: String) extends Exception(req + " not found.")

  class PackageSymbolCompleter(thisPackageURL: String) extends universe.LazyType {

    object packageCompleter extends PackageCompleter {
      lazy val universe: thisCompleters.universe.type = thisCompleters.universe
    }

    override def complete(thisPackage: Symbol): Unit = {
      packageCompleter.load(thisPackage, thisPackageURL)
    }
    
  }

  class ClassSymbolCompleter(binaryName: String) extends universe.LazyType {

    object classCompleter extends ClassCompleter {
      lazy val universe: thisCompleters.universe.type = thisCompleters.universe
    }

    override def complete(root: Symbol): Unit = {
      classCompleter.load(binaryName, root)
    }

  }

}
