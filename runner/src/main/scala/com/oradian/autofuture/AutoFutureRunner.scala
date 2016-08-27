package com.oradian.autofuture

import org.slf4j.LoggerFactory

import scalax.file._

object AutoFutureRunner extends App {
  if (args.isEmpty) {
    System.err.println("Usage: java -jar autofuture.jar [file1] [directory1] ... [directoryN]")
    sys.exit(1)
  }

  val logger = LoggerFactory.getLogger("auto-future")

  args foreach { arg =>
    val path = Path.apply(arg.replace('\\', '/'), '/').toAbsolute
    logger.info("Gathering sources from path: {}", path.path)
    for (source <- (path ** "*.scala").par if source.isFile) {
      logger.trace("Examining Scala source: {}", source.path)
      AutoFuture(source.string) match {
        case AutoFuture.Result.Success(body) =>
          logger.debug("Auto-Futuring Scala source: {}", source.path)
          source.write(body)
        case AutoFuture.Result.Error(error) =>
          logger.warn("Error while parsing Scala source: {}", error)
        case AutoFuture.Result.Noop =>
          logger.trace("Noop on: {}", source.path)
      }
    }
  }

  sys.exit(0)
}
