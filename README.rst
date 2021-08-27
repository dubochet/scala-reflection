================================================================================
                          THE SCALA REFLECTION LIBRARY                          
================================================================================

*This codebase is historic, and represents early efforts in the development of a
Scala reflection library. It has been entirely replaced and superseeded by later
developments.*

The Scala reflection library is intended to provide developers with a means to 
reflect on Scala programs in a way that exposes structures corresponding to the 
Scala source being reflected upon.

Scala reflection is built on top of Java reflection. To reassemble a Scala-like
view of the program from the damaged view returned by Java reflection, it
implements an abstract API — which it shares with the Scala compiler — that can
recreate the original Scala view of a program from class files. This API is the
*backend* of the Scala reflection library. The *frontend* ties the backend and
Java reflection into a user-friendly system to do Scala reflection.

Questions can be addressed to Gilles Dubochet <gilles.dubochet@epfl.ch>.
