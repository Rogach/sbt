/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package xsbt

import xsbti.Logger
import scala.tools.nsc.{GenericRunnerCommand,InterpreterLoop}

class ConsoleInterface
{
	def run(args: Array[String], bootClasspathString: String, classpathString: String, initialCommands: String, log: Logger)
	{
		val settings = MakeSettings(args.toList, log)
		if(!bootClasspathString.isEmpty)
			settings.bootclasspath.value = bootClasspathString
		settings.classpath.value = classpathString
		log.info(Message("Starting scala interpreter..."))
		log.debug(Message("  Classpath: " + settings.classpath.value))
		log.info(Message(""))
		val loop = new InterpreterLoop {
			override def createInterpreter() = {
				super.createInterpreter()
				if(!initialCommands.isEmpty) interpreter.interpret(initialCommands)
			}
		}
		loop.main(settings)
	}
}
object MakeSettings
{
	def apply(args: List[String], log: Logger) =
	{
		val command = new GenericRunnerCommand(args, message => log.error(Message(message)))
		if(command.ok)
			command.settings
		else
			throw new InterfaceCompileFailed(Array(), command.usageMsg)
	}
}
