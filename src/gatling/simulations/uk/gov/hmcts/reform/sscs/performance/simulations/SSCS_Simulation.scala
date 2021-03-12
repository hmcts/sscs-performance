package uk.gov.hmcts.reform.sscs.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.CreateUser
import uk.gov.hmcts.reform.sscs.performance.Feeders
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.sscs.performance.scenarios.{SSCS_SYA, _}

class SSCS_Simulation extends Simulation {

  val BaseURL = Environment.baseURL
  val myafeeder = csv("MYADetails.csv").circular
  val sscs_loginfeederFromNew = csv("SSCSUserDetailsNoDrafts.csv").circular
  val sscs_loginfeeder3drafts = csv("SSCSUserDetails3Drafts.csv").circular
  val sscs_loginfeeder10drafts = csv("SSCSUserDetails10Drafts.csv").circular
  val sscs_loginfeeder15drafts = csv("SSCSUserDetails15Drafts.csv").circular
  val sscs_loginfeedermyadata = csv("SSCSUserDetailsForMYA.csv").circular

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
  
  //below is the scenario for complete the draft with 3 drafts available
  
  val SSCSScenario3Drafts = scenario("SSCS_SYA_3Drafts")
    .feed(sscs_loginfeeder3drafts).feed(Feeders.DataFeederWith3Drafts)
    .repeat(1) {
      exec(//CreateUser.CreateCitizen,
        SSCS_SYA.SSCSSYAJourneyDraft,
        SSCS_SYA.SSCSSYAJourneyDraftComplete,
        SSCS_SYA.Signout
      )
    }
  
  //below is the scenario for complete the draft with 10 drafts available
  val SSCSScenario10Drafts = scenario("SSCS SYA 10 Drafts")
      .feed(sscs_loginfeeder10drafts).feed(Feeders.DataFeederWith10Drafts)
      .repeat(1) {
        exec(//CreateUser.CreateCitizen,
          SSCS_SYA.SSCSSYAJourneyDraft,
          SSCS_SYA.SSCSSYAJourneyDraftComplete,
          SSCS_SYA.Signout
        )
        
      }
  // below is the scenario for complete the draft with 15 drafts available
  val SSCSScenario15Drafts = scenario("SSCS SYA 15 Drafts")
     .feed(sscs_loginfeeder15drafts).feed(Feeders.DataFeederWith15Drafts)
     .repeat(1) {
       exec(//CreateUser.CreateCitizen,
         SSCS_SYA.SSCSSYAJourneyDraft,
         SSCS_SYA.SSCSSYAJourneyDraftComplete,
         SSCS_SYA.Signout
       )
       }
  
  //below is the scenario for complete the appeal from new
  
  val SSCSScenarioComplete = scenario("SSCS_SYA_Complete")
    .feed(sscs_loginfeederFromNew).feed(Feeders.DataFeederNewApplications)
      .exec(
        //CreateUser.CreateCitizen,
        SSCS_SYA.SSCSSYAJourneyDraft,
        SSCS_SYA.NewApplication,
        SSCS_SYA.SSCSSYAJourneyDraftComplete,
        SSCS_SYA.Signout
      )
  
  // below scenario is for user data creation
  val UserCreationScenario = scenario("SSCS User Creation")
    .exec(
      CreateUser.CreateCitizen("citizen")
        .pause(20)
    )
  
  // This needs to cover the E2E journey for a new userMYA
  val SSCSMYAScenario = scenario("SSCS_MYA")
    .feed(myafeeder)
        .exec(SSCS_MYA.SSCSMYAJourneyBeforeUpload)
        .exec(SSCS_MYA.provideEvidence)
        .exec(SSCS_MYA.SSCSSYAJourneyDraftComplete)
  
  //below is the setup to run the MYA
  
  setUp(
    SSCSScenarioComplete.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
  
  //below is the actual setup to run the whole suit
  /*setUp(
		 SSCSScenarioComplete.inject(nothingFor(5),rampUsers(120) during (1200)),
		 SSCSScenario3Drafts.inject(nothingFor(15),rampUsers(120) during (1200)),
		 SSCSScenario10Drafts.inject(nothingFor(35),rampUsers(180) during (1200)),
		 SSCSScenario15Drafts.inject(nothingFor(55),rampUsers(180) during (1200)),
		 SSCSMYAScenario.inject(nothingFor(600),rampUsers(238) during (1200))
     )
     .protocols(httpProtocol)*/
}