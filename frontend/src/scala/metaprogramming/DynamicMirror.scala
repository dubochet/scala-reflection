package scala.metaprogramming

/** A dynamic mirror reflects program entities in a running (dynamic) program. Entities such as values and call stacks
  * are defined only for dynamic mirors. */
trait DynamicMirror extends Mirror {

  /* ========== LOCATION ========== */

  /**  */
  trait Location extends Reflection


}