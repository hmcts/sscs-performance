package uk.gov.hmcts.reform.sscs.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.CreateUser
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.sscs.performance.scenarios.{SSCS_SYA, _}

class SSCS_Simulation extends Simulation {

  val BaseURL = Environment.baseURL
  val sscs_loginfeeder3drafts = csv("SSCSUserDetails3Drafts.csv").circular
  val sscs_loginfeeder10drafts = csv("SSCSUserDetails10Drafts.csv").circular

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  // This needs to cover the E2E journey for a new user
  val SSCSScenarioE2E = scenario("SSCS_SYA_E2E")
    .exec(
      //CreateUser.CreateCitizen,
      SSCS_SYA.SSCSSYAJourneyDraft,
        SSCS_SYA.SSCSSYAJourneyDraftComplete
    )
  
  //below needs to cover a draft version upto transaction 220
  
  //below is the scenario for creating 3 drafts
  
  val SSCSScenario3Drafts = scenario("SSCS_SYA_3Drafts")
    .feed(sscs_loginfeeder3drafts)
    .repeat(3) {
            exec(//CreateUser.CreateCitizen,
        SSCS_SYA.SSCSSYAJourneyDraft ,
              SSCS_SYA.Signout
        //SSCS_SYA.SSCSSYAJourneyDraftComplete
      )
    }
  
  //below is the scenario for creating 10 drafts
  val SSCSScenario10Drafts = scenario("SSCS SYA 10 Drafts")
      .feed(sscs_loginfeeder10drafts)
      .repeat(10) {
        exec(//CreateUser.CreateCitizen,
          SSCS_SYA.SSCSSYAJourneyDraft ,
          SSCS_SYA.Signout
          //SSCS_SYA.SSCSSYAJourneyDraftComplete
        )
      }
  
  val SSCSScenarioDraftComplete = scenario("SSCS_SYA_Draft_Complete")
                          .exec(
                            //CreateUser.CreateCitizen,
                            //SSCS_SYA.SSCSSYAJourneyDraft
                            SSCS_SYA.SSCSSYAJourneyDraftComplete
                          )
  
  // This needs to cover the Edit and then E2E journey
  val UserCreationScenario = scenario("SSCS User Creation")
    .exec(
      CreateUser.CreateCitizen("citizen")
        .pause(20)
    )
  /*
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

 /* setUp(
    UserCreationScenario.inject(rampUsers(1) during (2))
  ).protocols(httpProtocol)*/
  
  setUp(
    SSCSScenario10Drafts.inject(rampUsers(190) during (5400))
  ).protocols(httpProtocol)
  
  /*setUp(
    SSCSMYAScenario.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
  .assertions(global.successfulRequests.percent.is(100))*/
}