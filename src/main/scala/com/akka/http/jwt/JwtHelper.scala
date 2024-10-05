package com.akka.http.jwt


import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.util.{Failure, Success, Try}

object JwtHelper {
  // Secret key for signing JWT
  private val secretKey = "your-secret-key"

  // Encode a JWT token with user data (e.g., userId)
  def createToken(userId: String): String = {
    val claim = JwtClaim(
      content = Map("userId" -> userId).asJson.noSpaces, // Payload with user data
      expiration = Some(java.time.Instant.now.plusSeconds(3600).getEpochSecond) // Expiration: 1 hour
    )
    Jwt.encode(claim, secretKey, JwtAlgorithm.HS256) // Encode with HMAC SHA-256
  }

  // Decode and validate the JWT token
  def decodeToken(token: String): Option[String] = {
    Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256)) match {
      case Success(decoded) =>
        io.circe.parser.parse(decoded.content).flatMap(_.hcursor.get[String]("userId")) match {
          case Right(userId) => Some(userId)
          case _ => None
        }
      case Failure(_) => None // Token validation failed
    }
  }
}


