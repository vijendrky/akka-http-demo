package com.akka.http.supervisor

import akka.actor.{ActorSystem, Props}

import akka.actor.{Actor, ActorSystem, Props, OneForOneStrategy, SupervisorStrategy, ActorRef}
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._
import scala.language.postfixOps

// Child actor that throws an exception
class ChildActor extends Actor {
  private var state: Int = 0

  override def receive: Receive = {
    case "increment" =>
      state += 1
      println(s"Child actor state: $state")
    case "fail" =>
      println("Child actor will fail now")
      throw new RuntimeException("Failure in Child Actor")
    case "getState" =>
      sender() ! println("state ::: "+state)
  }
}

// Supervisor actor that manages the child actor
class SupervisorActor extends Actor {
  // Define the supervision strategy
  override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: java.lang.RuntimeException => Restart // Restart the child actor on a RuntimeException
    case _: Exception        => Stop    // Stop the child actor on any other Exception
  }

  // Create the child actor
  private val child: ActorRef = context.actorOf(Props[ChildActor], "child")

  override def receive: Receive = {
    case msg => child.forward(msg) // Forward all messages to the child actor
  }
}
object SupervisionExample extends App {
  // Create the Actor System
  val system = ActorSystem("SupervisionSystem")

  // Create the supervisor actor
  private val supervisor = system.actorOf(Props[SupervisorActor], "supervisor")

  // Interact with the child actor through the supervisor
  supervisor ! "increment" // Increment state
  supervisor ! "increment" // Increment state

  // Cause the child actor to fail
  supervisor ! "fail"

  // Request the state of the child actor after failure (will be reset after restart)
  Thread.sleep(1000) // Give time for restart
  supervisor ! "getState"
  supervisor ! "increment" // Increment state
  supervisor ! "increment" // Increment state

  // Shut down the system
  Thread.sleep(5000000)
  system.terminate()
}
