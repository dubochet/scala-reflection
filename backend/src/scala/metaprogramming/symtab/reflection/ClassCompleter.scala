package scala.metaprogramming.symtab
package reflection

import scala.collection._
import java.lang.{ ClassLoader => JClassLoader, Class => JClass }

abstract class ClassCompleter { thisReflector =>

  val universe: Universe
  import universe._

  def reflectionLoader: JClassLoader = universe.reflectionLoader
  def libraryLoader: JClassLoader = universe.libraryLoader

  protected var busy: Option[Symbol] = None // lock to detect recursive reads

  def load(binaryName: String, root: Symbol): Unit = try {
    busy = Some(root)
    val (classSym, staticSym) =
      if (root.isModule)
        (root.companionClass, root)
      else
        (root, root.companionClass)
    val jClass: JClass[_] =
      reflectionLoader.loadClass(binaryName)
    val signatureBytes: Option[Array[Byte]] = {
      val encodedString: Option[String] = {
        val scalaSigAnnot =
          jClass.getAnnotation(completers.scalaSigAnnotJClass).asInstanceOf[completers.ScalaSigAnnot]
        if (scalaSigAnnot != null)
          Some(scalaSigAnnot.bytes)
        else {
          val scalaLongSigAnnot =
            jClass.getAnnotation(completers.scalaSigAnnotJClass).asInstanceOf[completers.ScalaLongSigAnnot]
          if (scalaLongSigAnnot != null) {
            var buffer = new mutable.StringBuilder
            scalaLongSigAnnot.bytes foreach { b => buffer ++= b }
            Some(buffer.toString)
          }
          else
            None
        }
      }
      encodedString map { s: String =>
        val b = new Array[Byte](s.length)
        var i = 0
        while (i < b.length) b(i) = s(i).toByte
        val l = scala.reflect.generic.ByteCodecs.decode(b)
        b.take(l)
      }
    }
    signatureBytes match {
      case Some(bytes) =>
        loadScala(jClass, bytes, classSym, staticSym)
      case None =>
        loadJava(jClass, classSym, staticSym)
    }
  }
  catch {
    case e: completers.MissingRequirementException =>
      throw e
    case e: ClassNotFoundException =>
      throw e
  }
  finally {
    busy = None
  }

  def loadScala(jClass: JClass[_], bytes: Array[Byte], classSym: Symbol, staticSym: Symbol): Unit = {
    object unpickler extends ClassUnpickler {
      val global: thisReflector.universe.type = thisReflector.universe
    }
    unpickler.unpickle(bytes, offset = 0, classSym, staticSym, filename = jClass.toString)
  }

  def loadJava(jClass: JClass[_], classSym: Symbol, staticSym: Symbol): Unit = {
    object reflector extends ClassReflector {
      val global: thisReflector.universe.type = thisReflector.universe
    }
    reflector.reflect(jClass, classSym, staticSym, filename = jClass.toString)
  }

}
