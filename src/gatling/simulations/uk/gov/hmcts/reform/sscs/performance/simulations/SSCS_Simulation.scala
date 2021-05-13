package uk.gov.hmcts.reform.sscs.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scenarios.CreateUser
import uk.gov.hmcts.reform.sscs.performance.Feeders
import uk.gov.hmcts.reform.sscs.performance.scenarios.{SSCS_MYA, SSCS_SYA}
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment


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
  
  //below scenarios for creating the drafts
  
  val SSCSScenario15drafts = scenario("SSCS_SYA_3drafts")
    .feed(sscs_loginfeeder15drafts).feed(Feeders.DataFeederNewApplications)
      .repeat(15) {
        exec (
          //CreateUser.CreateCitizen,
          SSCS_SYA.SSCSSYAJourneyDraft,
          SSCS_SYA.NewApplication
        )
      }
  
  //below  scenario is for complete the journey for the user having 3 drafts
  
  val SSCSScenario3Drafts = scenario("SSCS_SYA_3Drafts")
          .feed(sscs_loginfeeder3drafts).feed(Feeders.DataFeederWith3Drafts)
              .repeat(1) {
                exec(
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
    .randomSwitch(
    25d -> exec(SSCS_MYA.provideEvidence).exec(SSCS_MYA.SSCSMYAJourneyDraftComplete),
    75d -> exec(SSCS_MYA.SSCSMYAJourneyDraftComplete)
  )
  
  
  
  //below is the setup to run one scenario for sanity purpose
  
  /*setUp(
    SSCSScenario15drafts.inject(nothingFor(5),rampUsers(90) during (2400))
  ).protocols(httpProtocol)*/
  
  /* setUp(
     SSCSMYAScenario.inject(nothingFor(5),rampUsers(5) during (50))
  ).protocols(httpProtocol)*/
  
  //below is the actual setup to run the whole suit
 /* setUp(
		 SSCSScenarioComplete.inject(nothingFor(5),rampUsers(1) during (1)),
		 SSCSScenario3Drafts.inject(nothingFor(15),rampUsers(1) during (1)),
		 SSCSScenario10Drafts.inject(nothingFor(35),rampUsers(1) during (1)),
		 SSCSScenario15Drafts.inject(nothingFor(55),rampUsers(1) during (1)),
		 SSCSMYAScenario.inject(nothingFor(65),rampUsers(1) during (1))
     )
     .protocols(httpProtocol)*/
  
  setUp(
		 SSCSScenarioComplete.inject(nothingFor(5),rampUsers(50) during (1800)),
		 SSCSScenario3Drafts.inject(nothingFor(15),rampUsers(50) during (1800)),
		 SSCSScenario10Drafts.inject(nothingFor(35),rampUsers(77) during (1800)),
		 SSCSScenario15Drafts.inject(nothingFor(55),rampUsers(77) during (1800)),
		 SSCSMYAScenario.inject(nothingFor(65),rampUsers(574) during (1800))
     )
     .protocols(httpProtocol)
}