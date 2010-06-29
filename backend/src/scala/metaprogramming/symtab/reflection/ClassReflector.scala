package scala.metaprogramming.symtab
package reflection

import scala.collection._
import java.lang.{ Class => JClass }
import java.lang.reflect.{ Modifier => JModifier }

import scala.reflect.generic.Flags

abstract class ClassReflector {

  val global: Universe
  import global._

  def reflect(jClass: JClass[_], classRoot: Symbol, moduleRoot: Symbol, filename: String): Unit =
    new UniverseReflect(jClass: JClass[_], classRoot, moduleRoot, filename).run()

  class UniverseReflect(jClass: JClass[_], classRoot: Symbol, moduleRoot: Symbol, filename: String) {

    // ClassfileParser's clazz is classRoot

    def run(): Unit = {

      val externalName: Name = jClass.getName
      val isAnnotation = jClass.isAnnotation
      val flags = transFlags(jClass.getModifiers, isClass = true, isAnnotation)
      // TODO: There are some tests with "externalName.toString.indexOf('$') < 0" that I do not understand
      // TODO: Type parameters of enclosing classes? Can this exist in Java?

      val instanceDefs = new Scope
      val staticDefs = new Scope

      enterOwnInnerClasses()

      classRoot.flags = flags

      // TODO: Private within
      
      classRoot.info = ClassInfoType(parseParents(isAnnotation), instanceDefs, classRoot)
      moduleRoot.moduleClass.info = ClassInfoType(List(), staticDefs, moduleRoot.moduleClass)
      moduleRoot.info = moduleRoot.moduleClass.info

    }

    def transFlags(mod: Int, isClass: Boolean, isAnnotation: Boolean): Long = {
      import Flags._
      var res = 0l
      if (JModifier.isPrivate(mod))
        res = res | PRIVATE
      else if (JModifier.isProtected(mod))
        res = res | PROTECTED
      if (JModifier.isAbstract(mod) && !isAnnotation)
        res = res | DEFERRED
      if (JModifier.isFinal(mod))
        res = res | FINAL
      if (JModifier.isInterface(mod) && !isAnnotation)
        res = res | TRAIT | INTERFACE | ABSTRACT
      // TODO if ((flags & JAVA_ACC_SYNTHETIC) != 0) res = res | SYNTHETIC
      if (JModifier.isStatic(mod))
        res = res | STATIC
      if (isClass && ((res & DEFERRED) != 0L))
          res = res & ~DEFERRED | ABSTRACT
      res | JAVA
    }

    def parseParents(isAnnotation: Boolean): List[Type] = {
      val superType =
        if (isAnnotation)
          definitions.AnnotationClass.tpe
        else
          getClassSymbol(jClass.getSuperclass).tpe
      val ifaces =
        for (i <- jClass.getInterfaces.toList) yield getClassSymbol(i).tpe
      superType :: (if (isAnnotation) definitions.ClassfileAnnotationClass.tpe :: ifaces else ifaces)
    }

    def enterOwnInnerClasses(): Unit = {
      // TODO
    }

    def getClassSymbol(jClass: JClass[_]): Symbol =
      error("not implemented") // TODO
    
  }

}