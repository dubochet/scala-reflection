package scala.metaprogramming.symtab
package tests

import org.scalatest._
import reflect.NoSymbol

class BasicTests extends FunSuite with BeforeAndAfterAll {

  lazy val universe = new Universe(this.getClass.getClassLoader, scala.Predef.getClass.getClassLoader)

  test("Creating universe") {
    val u = universe
    assert(u != null)
  }

  test("Get root package") {
    val r = universe.definitions.RootPackage
    assert(r != null)
  }

  test("Read root package propertoes") {
    val r = universe.definitions.RootPackage
    assert(r != null && r != NoSymbol)
    assert(r.name == "_root_")
    println(r.info.getClass)
    println(r.info.decl("scala"))
  }
  
}