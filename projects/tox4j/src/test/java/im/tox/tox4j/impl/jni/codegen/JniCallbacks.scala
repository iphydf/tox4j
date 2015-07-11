package im.tox.tox4j.impl.jni.codegen

import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.core.callbacks.ToxEventAdapter

import scala.util.{ Success, Failure, Try }

object JniCallbacks extends CodeGenerator {

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Nothing"))
  def generateCallbacks(clazz: Class[_]): String = {
    clazz.getMethods.filter(_.getDeclaringClass == clazz).sortBy(method => method.getName).map { method =>
      val baseType = clazz.getInterfaces.toList.flatMap { interface =>
        Try(interface.getMethod(method.getName, method.getParameterTypes: _*)) match {
          case Failure(_)          => Nil
          case Success(baseMethod) => List(baseMethod.getDeclaringClass): List[Class[_]]
        }
      } match {
        case interface :: Nil =>
          interface
        case multiple =>
          val names = multiple.map(_.getSimpleName).mkString(", ")
          sys.error(s"Multiple callback interfaces provide the same method '${method.getName}': $names")
      }

      val expectedMethodName = {
        val name = cxxTypeName(baseType.getSimpleName)
        javaVarName(name.substring(0, name.lastIndexOf('_')).toLowerCase)
      }

      assert(
        method.getName == expectedMethodName,
        s"Method name ${method.getName} does not match its interface name ${baseType.getName}."
      )

      s"""
      |// ${baseType.getName}#$expectedMethodName
      |CALLBACK (${cxxVarName(method.getName)})
      |""".stripMargin.trim
    }.mkString("\n")
  }

  writeFile("tox/generated/av.h") {
    generateCallbacks(classOf[ToxAvEventAdapter[_]])
  }

  writeFile("tox/generated/core.h") {
    generateCallbacks(classOf[ToxEventAdapter[_]])
  }

}