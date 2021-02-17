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
        .check(regex("""state=(.+)" method""").saveAs("stateId"))
        .check(regex("""name="_csrf" value="(.+)" />""").saveAs("csrf"))
        .check(substring("Sign in or create an account")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter Login credentials

  .group("SSCS_060_SignIn")
  {
    exec(http("Sign In")
        .post(IdAMURL + "/login?client_id=sscs&redirect_uri=" + BaseURL + "%2Fauthenticated&ui_locales=en&response_type=code&state=${stateId}")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("username", "sscs+myapt.182@mailinator.com") // perftest-sscs@perftest12345.com
        .formParam("password", "Pass19word")                    // Pa55word11
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "${csrf}")
        .check(substring("Your draft benefit appeals"))
        .check(substring("""<a href="/edit-appeal?caseId=""").count.saveAs("draftCount")))
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
  
  // Enter PIP as Benefit Type 
  
  .group("SSCS_080_SelectBenefitType") 
  {
      exec(http("PIP Benefit")
          .post(BaseURL + "/benefit-type")
          .headers(CommonHeader) 
          .headers(PostHeader) 
          .formParam("benefitType", "Personal Independence Payment (PIP)")
          .check(substring("What language do you want us to use when")))
  }

  // Select English as Language

  .group("SSCS_090_SelectLanguage")
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

  .group("SSCS_100_EnterPostCode")
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

  .group("SSCS_110_DecidedIndependent")
  {
    exec(http("Decied Independent Tribunal")
        .post(BaseURL + "/independence")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Yes, I have a Mandatory Reconsideration Notice")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Yes, I have a Mandatory Reconsideration Notice (MRN)

  .group("SSCS_120_DecidedIndependent")
  {
    exec(http("Have You Got MRN")
        .post(BaseURL + "/have-you-got-an-mrn")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("haveAMRN", "yes")
        .check(substring("When is your Mandatory Reconsideration Notice (MRN) dated")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter MRN date

  .group("SSCS_130_SubmitMRN")
  {
    exec(http("Enter MRN date")
        .post(BaseURL + "/mrn-date")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("mrnDate.day", "10")
        .formParam("mrnDate.month", "02")
        .formParam("mrnDate.year", "2021")
        .check(substring("Select the Personal Independence Payment number")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Select the DWP Personal Independence Payment number 

  .group("SSCS_140_DWPOffice")
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

  .group("SSCS_150_AppealingMyself")
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

  .group("SSCS_160_SubmitAppelantDetails")
  {
    exec(http("Enter Appelant Details")
        .post(BaseURL + "/enter-appellant-name")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("title","Miss")
        .formParam("firstName","SSCS SYA")
        .formParam("lastName","PerfTest")
        .check(substring("Enter your date of birth")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter DOB

  .group("SSCS_170_AppelantDOB")
  {
    exec(http("Enter Appelant DOB")
        .post(BaseURL + "/enter-appellant-dob")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("date.day","10")
        .formParam("date.month","10")
        .formParam("date.year","1970")
        .check(substring("You can find your National Insurance number on any letter about the benefit")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Enter 

  .group("SSCS_180_NINumber")
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

  .group("SSCS_190_PostCodeLink")
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

  .group("SSCS_200_ManulContactDetails")
  {
    exec(http("Enter Contact Details")
        .post(BaseURL + "/enter-appellant-contact-details")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("addressLine1", "Performance House")
        .formParam("addressLine2", "11 Performance Street")
        .formParam("townCity", "PerfCity")
        .formParam("county", "PerfCounty")
        .formParam("postCode", "TS11ST")
        .formParam("phoneNumber", "")
        .check(substring("Do you want to receive text message notifications")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

// Do you want to receive text message notifications - No 

  .group("SSCS_210_TextReminders")
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

  .group("SSCS_220_NoRepresentative")
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

  .group("SSCS_230_ReasonForAppeal")
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

  .group("SSCS_240_AnythingElse")
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

  .group("SSCS_250_YesUploadEvidence")
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

  .group("SSCS_260_ChooseFile")
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
      .headers(PostHeader) 
      .check(substring("Delete")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // No more files, Save & Continue on Evidence page

  .group("SSCS_270_EvidenceContinue")
  {
    exec(http("No Additional Evidence Continue")
        .post(BaseURL + "/evidence-upload")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Briefly describe the evidence you’ve just uploaded")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter description of evidence and continue

  .group("SSCS_280_EvidenceDescription")
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

  .group("SSCS_280_EvidenceDescription")
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

  .group("SSCS_290_AttendHearing")
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

  .group("SSCS_300_NoHearingContinue")
  {
    exec(http("No Hearing Continue")
        .post(BaseURL + "/not-attending-hearing")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .check(substring("Check your answers")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
    
  // Check & Send - Submit Appeal

  .group("SSCS_310_CheckYourAppeal")
  {
    exec(http("Submit Appeal")
        .post(BaseURL + "/check-your-appeal")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("signer", "SSCS SYA PerfTest")
        .check(substring("Your appeal has been submitted")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

}
  