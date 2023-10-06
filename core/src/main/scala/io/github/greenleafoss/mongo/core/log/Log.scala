package io.github.greenleafoss.mongo.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait Log:
  protected val log: Logger = LoggerFactory.getLogger(getClass)
