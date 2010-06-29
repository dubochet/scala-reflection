package scala.metaprogramming.symtab

import scala.reflect.generic

trait AnnotationInfos extends generic.AnnotationInfos { self: Universe =>

  /** Typed information about an annotation. It can be attached to either a symbol or an annotated type.
    * @param args Stores arguments to Scala annotations,`represented as typed trees.
    * @param assocs Stores arguments to classfile annotations as name-value pairs. */
  final case class AnnotationInfo(atp: Type, args: List[Tree], assocs: List[(Name, ClassfileAnnotArg)])
  object AnnotationInfo extends AnnotationInfoExtractor

  /** Arguments to classfile annotations are either:
    * - constants;
    * - arrays of constants;
    * - nested classfile annotations. */
  sealed abstract class ClassfileAnnotArg

  /** A compile-time Constant (Boolean, Byte, Short, Char, Int, Long, Float, Double, String, java.lang.Class or an
    * instance of a Java enumeration value). */
  final case class LiteralAnnotArg(const: Constant) extends ClassfileAnnotArg
  object LiteralAnnotArg extends LiteralAnnotArgExtractor

  /** An array of classfile annotation arguments. */
  final case class ArrayAnnotArg(args: Array[ClassfileAnnotArg]) extends ClassfileAnnotArg
  object ArrayAnnotArg extends ArrayAnnotArgExtractor

  /** A nested classfile annotation. */
  final case class NestedAnnotArg(annInfo: AnnotationInfo) extends ClassfileAnnotArg
  object NestedAnnotArg extends NestedAnnotArgExtractor

  implicit def classfileAnnotArgManifest: ClassManifest[ClassfileAnnotArg] = error("non implemented")

}