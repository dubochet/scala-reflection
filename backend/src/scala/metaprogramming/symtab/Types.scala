package scala.metaprogramming.symtab

import scala.reflect.generic

trait Types extends generic.Types { self: Universe =>

  abstract class Type extends AbsType {

    def typeSymbol: Symbol = NoSymbol // TODO
    def decl(name: Name): Symbol = NoSymbol // TODO

  }

  lazy val NoType: Type = error("non implemented")
  lazy val NoPrefix: Type = error("non implemented")

  case class ThisType(sym: Symbol) extends Type
  object ThisType extends ThisTypeExtractor

  case class TypeRef(pre: Type, sym: Symbol, args: List[Type]) extends Type
  object TypeRef extends TypeRefExtractor

  case class SingleType(pre: Type, sym: Symbol) extends Type
  object SingleType extends SingleTypeExtractor

  case class SuperType(thistpe: Type, supertpe: Type) extends Type
  object SuperType extends SuperTypeExtractor

  case class TypeBounds(lo: Type, hi: Type) extends Type
  object TypeBounds extends TypeBoundsExtractor

  abstract class CompoundType extends Type

  case class RefinedType(parents: List[Type], decls: Scope) extends CompoundType
  object RefinedType extends RefinedTypeExtractor {

    def apply(parents: List[Type], decls: Scope, clazz: Symbol): RefinedType =
      new RefinedType(parents, decls) { override def typeSymbol: Symbol = clazz }
    
  }

  case class ClassInfoType(parents: List[Type], decls: Scope, clazz: Symbol) extends CompoundType {

    override def decl(name: Name): Symbol =
      decls.iterator find { s => s.name == name } match {
        case Some(s) => s
        case _ => NoSymbol
      }

  }
  object ClassInfoType extends ClassInfoTypeExtractor

  case class ConstantType(value: Constant) extends Type
  object ConstantType extends ConstantTypeExtractor

  case class MethodType(params: List[Symbol], resultType: Type) extends Type
  object MethodType extends MethodTypeExtractor

  case class PolyType(typeParams: List[Symbol], resultType: Type) extends Type
  object PolyType extends PolyTypeExtractor

  case class ExistentialType(quantified: List[Symbol], underlying: Type) extends Type
  object ExistentialType extends ExistentialTypeExtractor

  case class AnnotatedType(annotations: List[AnnotationInfo], underlying: Type, selfsym: Symbol) extends Type
  object AnnotatedType extends AnnotatedTypeExtractor

  /** An as-yet unevaluated type. */
  abstract class LazyType extends Type with AbsLazyType {

    /** Is this type completed (i.e. not a lazy type)? */
    override def isComplete: Boolean = false

    /** If this is a lazy type, assign a new type to `sym'. */
    override def complete(sym: Symbol): Unit
  }

}