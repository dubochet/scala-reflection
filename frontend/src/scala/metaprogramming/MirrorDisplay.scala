package scala.metaprogramming

import scala.swing._

abstract class MirrorDisplay {

  val mirror: Mirror

  def rootPackageComponent: Component =
    component(mirror.rootPackageDecl)

  def component(decl: mirror.Declaration): Component = decl match {
    case classDecl: mirror.ClassDecl =>
      new ClassDisplay(classDecl)
    case packageDecl: mirror.PackageDecl =>
      new PackageDisplay(packageDecl)
    case moduleDecl: mirror.ModuleDecl =>
      new ModuleDisplay(moduleDecl)
    case _ =>
      new Label("No display available")
  }

  def showRootPackage: Unit =
    show(mirror.rootPackageDecl)

  def show(decl: mirror.Declaration): Unit = {
    val frame = new Frame {
      title = decl.name
      contents = component(decl)
    }
    frame.open()
  }

  def termDisplay(term: mirror.TermDecl): Component = {
    new Label(term.name)
  }

  def typeDisplay(tpe: mirror.TypeDecl): Component = {
    new Label(tpe.name)
  }

  class ClassDisplay(val classDecl: mirror.ClassDecl) extends BoxPanel(Orientation.Vertical) {
    contents += new Label(classDecl.name)
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      for (term <- classDecl.terms.sequence)
        contents += termDisplay(term)
    })
    contents += new Label("Types:")
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      for (tpe <- classDecl.types.sequence)
        contents += typeDisplay(tpe)
    })
  }

  class ModuleDisplay(val moduleDecl: mirror.ModuleDecl) extends BoxPanel(Orientation.Vertical) {
    contents += new Label(moduleDecl.name)
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      for (term <- moduleDecl.terms.sequence)
        contents += termDisplay(term)
    })
    contents += new Label("Types:")
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      for (tpe <- moduleDecl.types.sequence)
        contents += typeDisplay(tpe)
    })
  }

  class PackageDisplay(val packageDecl: mirror.PackageDecl) extends BoxPanel(Orientation.Vertical) {
    contents += new Label(packageDecl.name)
    contents += new Label("Terms:")
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      val pts = packageDecl.terms.sequence
      for (term <- packageDecl.terms.sequence)
        contents += termDisplay(term)
    })
    contents += new Label("Types:")
    contents += new ScrollPane(new BoxPanel(Orientation.Vertical) {
      for (tpe <- packageDecl.types.sequence)
        contents += typeDisplay(tpe)
    })
  }

}