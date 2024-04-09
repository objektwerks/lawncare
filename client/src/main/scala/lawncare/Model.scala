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

  val selectedWalkerId = ObjectProperty[Long](0)
  val selectedSessionId = ObjectProperty[Long](0)