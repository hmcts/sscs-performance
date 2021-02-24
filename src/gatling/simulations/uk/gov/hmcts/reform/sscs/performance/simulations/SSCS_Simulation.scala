package uk.gov.hmcts.reform.sscs.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.sscs.performance.scenarios._
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment

class SSCS_Simulation extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  // This needs to cover the E2E journey for a new user
  val SSCSScenario = scenario("SSCS_SYA")
    .exec(
      //CreateUser.CreateCitizen,
      SSCS_SYA.SSCSSYAJourney
    )
  /*
  // This needs to cover the Edit and then E2E journey
  val SSCSScenario = scenario("SSCS_SYA")
    .exec(
      //CreateUser.CreateCitizen,
      SSCS_SYA.SSCSSYAJourney
    )
  
  // This will cover the MYA E2E journey
  val MYAScenario = scenario("SSCS_SYA")
    .exec(
      //CreateUser.CreateCitizen,
      SSCS_MYA.MYAJourney
    )
  */

  // This needs to cover the E2E journey for a new user
  val SSCSMYAScenario = scenario("SSCS_MYA")
    .exec(
      //CreateUser.CreateCitizen,
      SSCS_MYA.SSCSMYAJourney
    )

  setUp(
    SSCSMYAScenario.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(100))
/*
  setUp(
    SSCSScenario.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(100))
*/

}