package domain

import java.util.Properties
import java.io.FileInputStream

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class APISimulation extends Simulation {

  val csvFeeder = csv("token.csv").circular
  val perfProfile = System.getProperty("profile")

  val props = new Properties
  props.load(new FileInputStream(s"./profiles/${perfProfile}"))
  
  val host = props.getProperty("PERF-HOST")
  val port = props.getProperty("PERF-PORT")
  val apiUrl = s"http://$host:$port/v1"

  val httpProtocol = http
    .baseUrl(apiUrl)

  val scn = scenario("Get Employees Info")
    .feed(csvFeeder)
    .exec(
      http("Get Info from Employee One")
        .get("/employees/1")
        .header("Authorization","Bearer ${authToken}")
        .check(status.is(200))
    )
    
  setUp(
    scn.inject(atOnceUsers(3))
    .protocols(httpProtocol)
  )
  .assertions(
    global.failedRequests.percent.is(0),
    global.responseTime.max.lt(3100),
    global.successfulRequests.percent.gt(95),
  )
}