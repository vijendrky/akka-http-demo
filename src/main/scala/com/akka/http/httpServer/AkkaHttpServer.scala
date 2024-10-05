package com.akka.http.httpServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.akka.http.routes.ApiRoutes

import scala.concurrent.ExecutionContextExecutor

object AkkaHttpServer extends App {
  implicit val system: ActorSystem = ActorSystem("akka-http-jwt")
  //implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Start the server on port 8080
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(ApiRoutes.route)

  println(s"Server is running at http://localhost:8080/")
}

