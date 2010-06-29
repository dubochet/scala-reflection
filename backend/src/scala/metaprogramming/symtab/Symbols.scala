package scala.metaprogramming.symtab

import scala.reflect.generic


trait Symbols extends generic.Symbols { self: Universe =>

  import generic.Flags._

  class Symbol(override var owner: Symbol, val name: Name) extends AbsSymbol { thisSym =>

    protected def noSymbol = NoSymbol

    /** The flags of this symbol */
    override var flags: Long = 0L
    def setFlag(mask: Long): this.type = { flags = flags | mask; this }

    /** The name of the symbol before decoding, e.g. `$eq$eq` instead of `==`. */
    def encodedName: String = name // TODO

    /** Set when symbol has a modifier of the form private[X], NoSymbol otherwise. */
    override var privateWithin: Symbol = noSymbol // TODO

    // INFO

    private var infoStore: Type = _

    override def info: Type = {
      assert(infoStore ne null, this.name)
      if (!infoStore.isComplete)
        infoStore.complete(thisSym)
      infoStore
    }

    override def info_=(info: Type): Unit = {
      assert(info ne null, this.name)
      infoStore = info
    }

    /** The raw info of the type */
    def rawInfo: Type = info

    /** If this symbol is a class or trait, its self type, otherwise the type of the symbol itself */
    def typeOfThis: Type = thisSym.tpe

    /** If this is a sealed class, its known direct subclasses. Otherwise Set.empty */
    var children: Set[Symbol] = Set.empty[Symbol]
    override def addChild(sym: Symbol): Unit = { children += sym }

    /** Contains the annotations attached to member a definition (class, method, type, field). */
    var annotations: List[AnnotationInfo] = Nil
    override def addAnnotation(annot: AnnotationInfo): Unit = { annotations ::= annot }

    /** The module corresponding to this module class (note that this
     *  is not updated when a module is cloned), or NoSymbol if this is not a ModuleClass */
    override var sourceModule: Symbol = noSymbol // TODO

    /** @PP: Added diagram because every time I come through here I end up
     *       losing my train of thought.  [Renaming occurs.] This diagram is a
     *       bit less necessary since the renaming, but leaving in place
     *       due to high artistic merit.
     *
     * class Foo  <
     *  ^  ^ (2)   \
     *  |  |  |     \
     *  | (5) |     (3)
     *  |  |  |       \
     * (1) v  v        \
     * object Foo (4)-> > class Foo$
     *
     * (1) companionClass
     * (2) companionModule
     * (3) linkedClassOfClass
     * (4) moduleClass
     * (5) companionSymbol
     */

    /** If symbol is an object defition, it's implied associated class, otherwise NoSymbol */
    var moduleClass: Symbol = noSymbol // set in factory method

    /** The class with the same name in the same package as this module or
     *  case class factory. */
    def companionClass: Symbol = noSymbol // TODO

    /** A helper method that factors the common code used the discover a companion module of a class. If a companion
      * module exists, its symbol is returned, otherwise, `NoSymbol` is returned. The method assumes that `this`
      * symbol has already been checked to be a class (using `isClass`). */
    def companionModule: Symbol = noSymbol // TODO

    /** For a module its linked class, for a class its linked module or case
     *  factory otherwise. */
    def companionSymbol: Symbol =
      if (isTerm) companionClass
      else if (isClass) companionModule
      else noSymbol

    /** For a module class: its linked class
     *   For a plain class: the module class of its linked module.
     *
     *  Then object Foo has a `moduleClass' (invisible to the user, the backend calls it Foo$
     *  linkedClassOfClass goes from class Foo$ to class Foo, and back. */
    def linkedClassOfClass: Symbol =
      if (isModuleClass) companionClass else companionModule.moduleClass

    // flags and kind tests

    override def isTerm         = false  // to be overridden
    override def isType         = false  // to be overridden
    override def isClass        = false  // to be overridden
    override def isAliasType    = false  // to be overridden
    override def isAbstractType = false  // to be overridden

    // creators

    def newAliasType(name: Name, pos: Position = NoPosition): Symbol =
      new Symbol(thisSym, name) { // TypeSymbol constructor
        override def isType = true
        override def isAbstractType = isDeferred
        override def isAliasType = !isDeferred
      }

    def newAbstractType(name: Name, pos: Position = NoPosition): Symbol =
      newAliasType(name, pos).setFlag(DEFERRED)

    def newClass(name: Name, pos: Position = NoPosition): Symbol =
      new Symbol(thisSym, name) { // TypeSymbol constructor
        override def isType = true
        override def isClass = true
      }

    def newValue(name: Name, pos: Position = NoPosition): Symbol =
      new Symbol(thisSym, name) { // TermSymbol constructor
        override def isTerm = true
      }

    def newMethod(name: Name, pos: Position = NoPosition): Symbol =
      newValue(name, pos).setFlag(METHOD)

    def newModule(name: Name, clazz: Symbol = newModuleClass(name), pos: Position = NoPosition): Symbol = {
      val m = newValue(name, pos).setFlag(MODULE | FINAL)
      m.moduleClass = clazz
      clazz.sourceModule = m
      m
    }

    def newModuleClass(name: Name, pos: Position = NoPosition): Symbol =
      newClass(name, pos)

    def newPackage(name: Name, pos: Position = NoPosition): Symbol = {
      val p = newModule(name = name, pos = pos)
      p.setFlag(JAVA | PACKAGE)
      p.moduleClass.setFlag(JAVA | PACKAGE)
      p
    }
    
  }

  object NoSymbol extends Symbol(null, nme.NOSYMBOL) {

    override protected def noSymbol = this

  }
  
}