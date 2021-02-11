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

  val SSCSScenario = scenario( "SSCS_SYA")
    .exec(SSCS_SYA.SSCSSYAJourney)

  setUp(
    SSCSScenario.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
    //.assertions(global.successfulRequests.percent.is(100))

}