package uk.gov.hmcts.reform.sscs.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Common
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object SSCS_SYA 
{

  val BaseURL = Environment.baseURL
  val IdAMURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val sscs_loginfeeder = csv("SSCSUserDetails.csv").circular
  val postCode = "TS1 1ST"

  val SSCSSYAJourneyDraft =

  // Launch Homepage

    group("SYA_010_Homepage")
  {
    exec(flushHttpCache).exec(flushSessionCookies).exec(flushCookieJar)
        .exec(http("Homepage")
          .get(BaseURL + "/")
          .headers(CommonHeader)
          .check(substring("Select a benefit type")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter PIP as Benefit Type 
  
  .group("SYA_020_SelectBenefitType") 
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

  .group("SYA_020_SelectLanguage")
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

  .group("SYA_030_EnterPostCode")
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

  .group("SYA_040_DecidedIndependent")
  {
    exec(http("Decied Independent Tribunal")
        .post(BaseURL + "/independence")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("I want to be able to save this appeal later")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Select I want to be able to save this appeal later

  .group("SYA_050_SaveAppealLater")
  {
    exec(http("Save Appeal For Later")
        .post(BaseURL + "/create-account")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("createAccount", "yes")
        .check(regex("""state=(.+)" method""").saveAs("stateId"))
        .check(regex("""name="_csrf" value="(.+)" />""").saveAs("csrf"))
        .check(substring("Sign in or create an account")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Login credentials. This will load the dashboard

// Here there is more logic required. 
// 10% of user will create an account and then do the journey E2E - submit appeal
// remaining, will be percentage based and click on edit and then complete the E2E journey - SYA_120_DecidedIndependent onwards

  .group("SYA_060_SignIn")
  {
    exec(http("Sign In")
        .post(IdAMURL + "/login?client_id=sscs&redirect_uri=" + BaseURL + "%2Fauthenticated&ui_locales=en&response_type=code&state=${stateId}")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("username", "${email}") // this needs to be parameterised from a feeder
        .formParam("password", "${password}")                // this needs to be parameterised from a feeder
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "${csrf}")
        .check(substring("Your draft benefit appeals")))
        //.check(substring("""<a href="/edit-appeal?caseId=""").count.saveAs("draftCount")) // count for how many draft cases
        //.check(regex("""edit-appeal.caseId=(.+)">Edit""").findRandom.saveAs("caseId"))) // find a case at random
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)


 // This code needs to be in an IF statement. If drafCount >=1 do this, else, click on New Application
 // If Edit is clicked on, the script needs to route to the SYA_120_DecidedIndependent part

/*
val EditCase = 
  .exec 
  {
    session =>
      println("Email Address: sscs+myapt.182@mailinator.com")
      println("Draft Count:   " + session("draftCount").as[String])
      println("Case Id:       " + session("caseId").as[String])
    session
  }
*/

  // Click Edit for a case at random
  /*
  .group("SYA_080_EditCase") 
  {
      exec(http("Edit A Case")
          .get(BaseURL + "/edit-appeal?caseId=${caseId}")
          .headers(CommonHeader) 
          .headers(PostHeader) 
          .formParam("caseId", "${caseId}")
          .check(substring("Check your answers")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
*/
//===== eedit

// Click Create New application

//val NewApplication =

  .group("SYA_070_NewApplication")
  {
    exec(http("Create New Application")
        .get(BaseURL + "/new-appeal")
        .headers(CommonHeader) 
        .check(substring("Select a benefit type")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
  
  // Enter PIP as Benefit Type 
  
  .group("SYA_080_SelectBenefitType") 
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

  .group("SYA_090_SelectLanguage")
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

  .group("SYA_100_EnterPostCode")
  {
    exec(http("Post Code")
        .post(BaseURL + "/postcode-check")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("postcode", "TS1 1ST") // TS1 1ST, TS2 2ST, TS3 3ST
        .check(substring("Your appeal will be reviewed by a tribunal")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Click Continue on appeal will be decided by an independent tribunal

  .group("SYA_110_DecidedIndependent")
  {
    exec(http("Decied Independent Tribunal")
        .post(BaseURL + "/independence")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Yes, I have a Mandatory Reconsideration Notice")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Yes, I have a Mandatory Reconsideration Notice (MRN)

  .group("SYA_120_DecidedIndependent")
  {
    exec(http("Have You Got MRN")
        .post(BaseURL + "/have-you-got-an-mrn")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("haveAMRN", "yes")
        .check(substring("When is your Mandatory Reconsideration Notice")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  //Generate data to use (these are used multiple times throughout the flow)
  .exec 
  {
    session =>
      session
        .set("firstName", "Perf" + Common.randomString(5))
        .set("lastName", "SSCS" + Common.randomString(5))
        .set("dobDay", Common.getDay())
        .set("dobMonth", Common.getMonth())
        .set("dobYear", Common.getYear())
  }

  .exec 
  {
    session =>
      println("First name is:   " + session("firstName").as[String])
      println("Last name is:    " + session("lastName").as[String])
      println("DOB Day is:      " + session("dobDay").as[String])
      println("DOB Month is:    " + session("dobMonth").as[String])
      println("DOB Year is:     " + session("dobYear").as[String])
    session
  }

// Enter MRN date

  .group("SYA_130_SubmitMRN")
  {
    exec(http("Enter MRN date")
        .post(BaseURL + "/mrn-date")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("mrnDate.day", "04")
        .formParam("mrnDate.month", "03")
        .formParam("mrnDate.year", "2021")
        .check(substring("Select the Personal Independence Payment number")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Select the DWP Personal Independence Payment number 

  .group("SYA_140_DWPOffice")
  {
    exec(http("Select DWP Office")
        .post(BaseURL + "/dwp-issuing-office")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("pipNumber", "2")      
        .check(substring("appealing for myself")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Appointee - Select - I'm appealing for myself

  .group("SYA_150_AppealingMyself")
  {
    exec(http("I'm Appealing Myself")
        .post(BaseURL + "/are-you-an-appointee")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("isAppointee","no")
        .check(substring("Enter your name")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Appelant details

  .group("SYA_160_SubmitAppelantDetails")
  {
    exec(http("Enter Appelant Details")
        .post(BaseURL + "/enter-appellant-name")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("title","Miss")
        .formParam("firstName","${firstName}")
        .formParam("lastName","${lastName}")
        .check(substring("Enter your date of birth")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter DOB

  .group("SYA_170_AppelantDOB")
  {
    exec(http("Enter Appelant DOB")
        .post(BaseURL + "/enter-appellant-dob")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("date.day","${dobDay}")
        .formParam("date.month","${dobMonth}")
        .formParam("date.year","${dobYear}")
        .check(substring("You can find your National Insurance number on any letter about the benefit")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SYA_180_NINumber")
  {
    exec(http("Enter NI Number")
        .post(BaseURL + "/enter-appellant-nino")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("nino","AB998877A") // AB998877A, SK886644A
        .check(substring("Enter your contact details")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Click link to enter postcode manually

  .group("SYA_190_PostCodeLink")
  {
    exec(http("Click Link to enter Address mannually")
        .post(BaseURL + "/enter-appellant-contact-details")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("submitType","manual")
        .formParam("postcodeLookup","")
        .formParam("phoneNumber","")
        .formParam("emailAddress","")
        .check(substring("Address line 1")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Contact details manually

  .group("SYA_200_ManualContactDetails")
  {
    exec(http("Enter Contact Details")
        .post(BaseURL + "/enter-appellant-contact-details")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("addressLine1", "Performance House")
        .formParam("addressLine2", "11 Performance Street")
        .formParam("townCity", "PerfCity")
        .formParam("county", "PerfCounty")
        .formParam("postCode", "TS1 1ST")
        .formParam("phoneNumber", "")
        .formParam("emailAddress", "${email}") // this needs to match from the feeder
        .check(substring("Do you want to receive text message notifications")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Do you want to receive text message notifications - No 

  .group("SYA_210_TextReminders")
  {
    exec(http("No Text Reminders")
        .post(BaseURL + "/appellant-text-reminders")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("doYouWantTextMsgReminders", "no")
        .check(substring("Do you want to register a representative")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Do you want to register a representative - No

  .group("SYA_220_NoRepresentative")
  {
    exec(http("Select No to Representative")
        .post(BaseURL + "/representative")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("hasRepresentative", "no")
        .check(substring("Your reasons for appealing")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Reason for Appealing
  
  val SSCSSYAJourneyDraftComplete=

  group("SYA_230_ReasonForAppeal")
  {
    exec(http("Enter Reason For Appeal")
        .post(BaseURL + "/reason-for-appealing/item-0")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("item.whatYouDisagreeWith", "Performance testing disagree box")
        .formParam("item.reasonForAppealing", "Social Security and Child Support "))

    .exec(http("Enter Reason For Appeal")
        .post(BaseURL + "/reason-for-appealing")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("item.whatYouDisagreeWith", "Performance testing disagree box")
        .formParam("item.reasonForAppealing", "Social Security and Child Support ")
        .check(substring("Anything else you want to tell the tribunal")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Anything else

  .group("SYA_240_AnythingElse")
  {
    exec(http("Enter Anything Else")
        .post(BaseURL + "/other-reason-for-appealing")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("otherReasonForAppealing", "You might also be able to find a representative through a library or from an organisation")
        .check(substring("Would you like to upload evidence now")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Would you like to upload evidence now? - Yes

  .group("SYA_250_YesUploadEvidence")
  {
    exec(http("Select Yes Upload Evidence")
        .post(BaseURL + "/evidence-provide")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("evidenceProvide", "yes")
        .check(substring("You can only add MP3 & MP4 files after we have received your application")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Click on Choose File button. This will result in the page to refresh with the file name shown as files uploaded

  .group("SYA_260_ChooseFile")
  {
    exec(http("Click Choose File Button")
      .post(BaseURL + "/evidence-upload/item-0")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryp95uCCxYktALnk3E")
      .header("csrf-token", "0")
      .body(RawFileBody("2MbFileUpload.txt"))
      .check(status.is(200)))

    .exec(http("Click Choose File Button")
      .get(BaseURL + "/evidence-upload")
      .headers(CommonHeader) 
      .check(substring("Delete")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // No more files, Save & Continue on Evidence page

  .group("SYA_270_EvidenceContinue")
  {
    exec(http("No Additional Evidence Continue")
        .post(BaseURL + "/evidence-upload")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Briefly describe the evidence you’ve just uploaded")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter description of evidence and continue

  .group("SYA_280_EvidenceDescription")
  {
    exec(http("Description Of Evidence")
        .post(BaseURL + "/evidence-description")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("describeTheEvidence", "The file uploaded is used for performance testing")
        .check(substring("Do you want to attend the hearing")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

    // Enter description of evidence and continue

  .group("SYA_280_EvidenceDescription")
  {
    exec(http("Description Of Evidence")
        .post(BaseURL + "/evidence-description")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("describeTheEvidence", "The file uploaded is used for performance testing")
        .check(substring("Do you want to attend the hearing")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

    // Attend hearing - No

  .group("SYA_290_AttendHearing")
  {
    exec(http("Attend Hearing No")
        .post(BaseURL + "/the-hearing")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("attendHearing", "no")
        .check(substring("You have chosen not to attend the hearing")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

    // Save & Continue - not attend hearing

  .group("SYA_300_NoHearingContinue")
  {
    exec(http("No Hearing Continue")
        .post(BaseURL + "/not-attending-hearing")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Check your answers")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
    
  // Check & Send - Submit Appeal

  .group("SYA_310_CheckYourAppeal")
  {
    exec(http("Submit Appeal")
        .post(BaseURL + "/check-your-appeal")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("signer", "${firstName} ${lastName}") 
        .check(substring("Your appeal has been submitted")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
    
    val Signout=
      group("SYA_320_Signout") {
      exec(http("SYA_320_Signout")
        .get("/sign-out")
        .headers(PostHeader)
        .check(status.in(200, 302, 304)))
    }

}
  