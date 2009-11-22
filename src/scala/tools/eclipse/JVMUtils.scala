/*
 * Copyright 2005-2009 LAMP/EPFL
 */
// $Id$

package scala.tools.eclipse

import scala.tools.nsc.Global 
import ch.epfl.lamp.fjbg.{ JMethodType, JObjectType, JType }

trait JVMUtils { self : Global =>

  private lazy val codeGenerator =
    currentRun.phaseNamed(genJVM.phaseName).asInstanceOf[genJVM.JvmPhase].codeGenerator

  def javaName(sym : Symbol) : String = codeGenerator.javaName(sym)
  
  def javaNames(syms : List[Symbol]) : Array[String] = codeGenerator.javaNames(syms)
  
  def javaFlags(sym : Symbol) : Int = codeGenerator.javaFlags(sym)
  
  def javaType(t: Type): JType = t match {
    case ErrorType | NoType => JType.UNKNOWN

    case m : MethodType =>
      val t = m.finalResultType
      new JMethodType(javaType(t), m.paramss.flatMap(_.map(javaType)).toArray)
      
    case p : PolyType =>
      val t = p.finalResultType
      javaType(t)
    
    case r : RefinedType =>
      JObjectType.JAVA_LANG_OBJECT
      //javaType(r.typeSymbol.tpe)
      
    case _ => codeGenerator.javaType(t)
  }
    
  def javaType(s: Symbol): JType =
    if (s.isMethod)
      new JMethodType(
        if (s.isClassConstructor) JType.VOID else javaType(s.tpe.resultType),
        s.tpe.paramTypes.map(javaType).toArray)
    else
      javaType(s.tpe)
}
