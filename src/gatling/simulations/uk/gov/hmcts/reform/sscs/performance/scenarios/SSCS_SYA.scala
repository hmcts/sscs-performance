package uk.gov.hmcts.reform.sscs.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object SSCS_SYA 
{

  val BaseURL = Environment.baseURL
  val IdAMURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val SSCSSYAJourney =

  // Launch Homepage

  group("SSCS_010_Homepage") 
  {
      exec(http("Homepage")
          .get(BaseURL + "/")
          .headers(CommonHeader)
          .check(substring("Select a benefit type")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter PIP as Benefit Type 
  
  .group("SSCS_020_SelectBenefitType") 
  {
      exec(http("PIP Benefit")
          .post(BaseURL + "/benefit-type")
          .headers(CommonHeader) 
          .headers(PostHeader) 
          .formParam("benefitType", "Personal Independence Payment (PIP)")
          .check(substring("What language do you want us to use when")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Select English as Language

  .group("SSCS_020_SelectLanguage")
  {
    exec(http("Language")
        .post(BaseURL + "/language-preference")
        .headers(CommonHeader)
        .headers(PostHeader) 
        .formParam("languagePreferenceWelsh", "no")
        .check(substring("Enter your postcode")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Post Code

  .group("SSCS_030_EnterPostCode")
  {
    exec(http("Post Code")
        .post(BaseURL + "/postcode-check")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("postcode", "TS1 1ST")
        .check(substring("Your appeal will be reviewed by a tribunal")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Click Continue on appeal will be decided by an independent tribunal

  .group("SSCS_040_DecidedIndependent")
  {
    exec(http("Decied Independent Tribunal")
        .post(BaseURL + "/independence")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("I want to be able to save this appeal later")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Select I want to be able to save this appeal later

  .group("SSCS_050_SaveAppealLater")
  {
    exec(http("Save Appeal For Later")
        .post(BaseURL + "/create-account")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("createAccount", "yes")
        .check(regex("""state=(.*)" method""").saveAs("stateId"))
        .check(regex("""name="_csrf" value="(.*)""").saveAs("csrf"))
        .check(substring("Sign in or create an account")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)


// Enter Login credentials

  .group("SSCS_060_SignIn")
  {
    exec(http("Sign In")
        .post(IdAMURL + "/login?client_id=sscs&redirect_uri=https%3A%2F%2Fbenefit-appeal.perftest.platform.hmcts.net%2Fauthenticated&ui_locales=en&response_type=code&state=$(stateId)")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("username", "sscs+myapt.182@mailinator.com")
        .formParam("password", "Pass19word")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "${csrf}")
        .check(substring("Your draft benefit appeals")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Click Create New application

  .group("SSCS_070_NewApplication")
  {
    exec(http("Create New Application")
        .get(BaseURL + "/new-appeal")
        .headers(CommonHeader) 
        .check(substring("Select a benefit type")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
/*
// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_")
  {
    exec(http("")
        .post(BaseURL + "")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("")
        .check(substring("")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
*/



}
