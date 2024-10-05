package com.akka.http.routes

import akka.http.scaladsl.model.headers.HttpChallenges
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.akka.http.jwt.JwtHelper

object ApiRoutes {

  val route: Route = {
    pathPrefix("api") {
      concat(
        // Public route: No authentication required
        path("login") {
          post {
            entity(as[String]) { username =>
              // Simulate login and return a token
              val token = JwtHelper.createToken(username)
              complete(s"Your JWT token: $token")//$token
            }
          }
        },
        // Protected route: JWT authentication required
        path("secure") {
          authenticate { userId =>
            get {
              complete(s"Hello, $userId! This is a secure endpoint.")
            }
          }
        }
      )
    }
  }
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.{Directive1, AuthenticationFailedRejection}
  import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}

  // Define a custom authentication directive
  def authenticate: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(tokenHeader) if tokenHeader.startsWith("Bearer ") =>
        val token = tokenHeader.substring(7) // Extract the token part
        JwtHelper.decodeToken(token) match {
          case Some(userId) => provide(userId) // Valid token, pass the userId
         // case None => reject(AuthenticationFailedRejection.CredentialsRejected, "Invalid JWT token")
          case None => reject(AuthenticationFailedRejection(
              AuthenticationFailedRejection.CredentialsMissing,
              HttpChallenges.oAuth2("example-realm")))
        }
      //case _ => reject(AuthenticationFailedRejection.CredentialsMissing, "Missing or invalid Authorization header")
      case None => reject(AuthenticationFailedRejection(
        AuthenticationFailedRejection.CredentialsMissing,
        HttpChallenges.oAuth2("Missing or invalid Authorization header")))
    }
  }

  /*import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.AuthenticationFailedRejection
  import akka.http.scaladsl.server.Route

  def myRoute1: Route = {
    path("secure") {
      // Check some condition (for example, missing Authorization header)
      optionalHeaderValueByName("Authorization") {
        case Some(token) =>
          // Token found, proceed to handle the request
          complete(s"Authorized with token: $token")
        //case None =>
          // Token not found, reject the request with AuthenticationFailedRejection
          //reject(AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsMissing, "No token provided"))
      }
    }
  }*/

 /* import akka.http.scaladsl.model.headers.HttpChallenges
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}

  def myRoute: Route = {
    path("secure") {
      // Check for an Authorization header
      optionalHeaderValueByName("Authorization") {
        case Some(token) =>
          // Token found, proceed to handle the request
          complete(s"Authorized with token: $token")
        case None =>
          // Token not found, reject the request with AuthenticationFailedRejection
          reject(AuthenticationFailedRejection(
            AuthenticationFailedRejection.CredentialsMissing,
            HttpChallenges.oAuth2("example-realm")
          ))
      }
    }
  }
*/

}
