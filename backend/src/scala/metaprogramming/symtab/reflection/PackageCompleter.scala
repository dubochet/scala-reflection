package scala.metaprogramming.symtab
package reflection

import scala.collection._
import scala.util.matching.Regex
import java.io.File
import java.net.URL
import java.lang.{ ClassLoader => JClassLoader }

import JavaConversions._

abstract class PackageCompleter { thisReflector =>

  val universe: Universe
  import universe._

  def reflectionLoader: JClassLoader = universe.reflectionLoader
  def libraryLoader: JClassLoader = universe.libraryLoader

  /** All string prefixes that a resource URL from the class loader can have. One of those prefixes must
    * be stripped to get the resrouce name. */
  val loaderURLPrefixes: Seq[String] =
    (reflectionLoader.getResources("") map { url => url.toString }).toSeq

  val ClassFileName = new Regex("""\.class""")

  def load(thisPack: Symbol, thisPackURL: String): Unit = {
    val thisPackScope: Scope = new Scope()
    thisPack.info = (ClassInfoType(Nil, thisPackScope, thisPack))
    val (innerClassesURLs, innerPacksURLs) = innerResourcesURLs(thisPackURL)
    for (innerClassURL <- innerClassesURLs) {
      val ns = innerClassURL.split('/')
      val n = ns.last
      assert(n.endsWith(".class"))
      val innerClassName = n.dropRight(6)
      val innerClassBinName =
        ns.init.mkString("", ".", ".") + innerClassName
      val innerClass = thisPack.newClass(innerClassName)
      val innerModule = thisPack.newModule(innerClassName)
      innerClass.info = new completers.ClassSymbolCompleter(innerClassBinName)
      innerModule.info = new completers.ClassSymbolCompleter(innerClassBinName)
      innerModule.moduleClass.info = new LazyType {
        override def complete(root: Symbol) { root.sourceModule.info }
      }
      thisPackScope enter innerClass
      thisPackScope enter innerModule
    }
    for (innerPackURL <- innerPacksURLs) {
      val innerPack = thisPack.newPackage(innerPackURL.split('/').last)
      innerPack.info = new completers.PackageSymbolCompleter(innerPackURL)
      thisPackScope enter innerPack
    }
  }

  /** Pair of direct inner class and packages URLs */
  def innerResourcesURLs(thisPackURL: String): (Seq[String], Seq[String]) = {
    val packParts = reflectionLoader.getResources(thisPackURL).toList
    val classes = mutable.ListBuffer.empty[String]
    val packs = mutable.ListBuffer.empty[String]
    for (packPart <- packParts) {
      classes ++= (packPart.toURI.getScheme match {
        case "file" =>
          val directory = new File(packPart.toURI.getPath)
          assert(directory.isDirectory)
          val cls = mutable.ListBuffer.empty[String]
          val pks = mutable.ListBuffer.empty[String]
          for (file <- directory.listFiles) {

          }
          cls.toList
        /*case "jar" => // TODO: This is really ugly and slow. Can it be done better/faster?
          val connection = packPart.openConnection.asInstanceOf[JarURLConnection]
          val directory = connection.getJarEntry
          val directoryName = directory.getName
          val file = connection.getJarFile
          assert(directory.isDirectory)
          val content = ListBuffer.empty[Beam]
          val entries = file.entries
          while (entries.hasMoreElements) {
            val entry = entries.nextElement
            val entryName = entry.getName
            if (entryName startsWith directoryName) {
              val entryRelativeName = entryName.substring(directoryName.length, entryName.length)
              val firstSlash = entryRelativeName.indexOf('/')
              val maybeBeam =
              if (entryRelativeName.length == 0)
                None
              else if (firstSlash < 0) {
                mangledNameToBeam(entryRelativeName, entry.isDirectory, beam)
              }
              else if (firstSlash == (entryRelativeName.length - 1)) {
                mangledNameToBeam(entryRelativeName.substring(0, firstSlash), entry.isDirectory, beam)
              }
              else
                None
              if (maybeBeam.isDefined) content += maybeBeam.get
            }
          }
          content.toList
          */
        case _ =>
          println("WARNING: reflecting on unsuported class loader")
          Nil
      })
    }
    (classes.toList, packs.toList)
  }

}