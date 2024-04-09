package lawncare

import com.typesafe.scalalogging.LazyLogging

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty

import Fault.given

final class Model(fetcher: Fetcher) extends LazyLogging:
  val shouldBeInFxThread = (message: String) => require(Platform.isFxApplicationThread, message)

  val registered = ObjectProperty[Boolean](true)
  val loggedin = ObjectProperty[Boolean](true)

  val selectedPropertyId = ObjectProperty[Long](0)
  val selectedSessionId = ObjectProperty[Long](0)

  selectedPropertyId.onChange { (_, oldPropertyId, newPropertyId) =>
    logger.info("*** selected property id onchange event: {} -> {}", oldPropertyId, newPropertyId)
    shouldBeInFxThread("*** selected property id onchange should be in fx thread.")
    sessions(newPropertyId)
  }

  val objectAccount = ObjectProperty[Account](Account.empty)
  val observableProperties = ObservableBuffer[Property]()
  val observableSessions = ObservableBuffer[Session]()
  val observableFaults = ObservableBuffer[Fault]()

  objectAccount.onChange { (_, oldAccount, newAccount) =>
    logger.info("*** object account onchange event: {} -> {}", oldAccount, newAccount)
  }

  observableProperties.onChange { (_, changes) =>
    logger.info("*** observable properties onchange event: {}", changes)
  }

  observableSessions.onChange { (_, changes) =>
    logger.info("*** observable sessions onchange event: {}", changes)
  }