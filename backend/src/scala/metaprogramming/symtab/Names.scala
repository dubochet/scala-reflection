package scala.metaprogramming.symtab

import scala.reflect.generic

trait Names extends generic.Names { self: Universe =>

  type Name = String

  def newTermName(cs: Array[Char], offset: Int, len: Int): Name = new String(cs.slice(offset, offset + len))
  def newTermName(cs: Array[Byte], offset: Int, len: Int): Name = new String(cs.slice(offset, offset + len))
  def newTermName(s: String): Name = s

  def mkTermName(name: Name): Name = name

  def newTypeName(cs: Array[Char], offset: Int, len: Int): Name = new String(cs.slice(offset, offset + len))
  def newTypeName(cs: Array[Byte], offset: Int, len: Int): Name = new String(cs.slice(offset, offset + len))
  def newTypeName(s: String): Name = s

  def mkTypeName(name: Name): Name = name

}