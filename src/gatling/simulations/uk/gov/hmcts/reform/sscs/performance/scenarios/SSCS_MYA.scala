
package uk.gov.hmcts.reform.sscs.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object SSCS_MYA 
{

  val MYABaseURL = Environment.myaULR
  val IdAMURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val SSCSMYAJourney =

  // Launch MYA Homepage with TYA reference number - https://sscs-cor.perftest.platform.hmcts.net/sign-in?tya=XeIpTC5Zfd

  group("MYA_010_MYAHomepage") 
  { 
    exec(http("Homepage")
      .get(MYABaseURL + "/sign-in?tya=PQ1CdmGUl0")
      .headers(CommonHeader)
      .formParam("tya", "PQ1CdmGUl0")
      .check(regex("""state=(.+)" method""").saveAs("stateId"))
      .check(regex("""name="_csrf" value="(.+)" />""").saveAs("csrf"))      
      .check(substring("Sign in or create an account")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
  
  // Enter Login credentials. This will load either postcode or dashboard

  // https://idam-web-public.perftest.platform.hmcts.net/login?redirect_uri=https%3A%2F%2Fsscs-cor.perftest.platform.hmcts.net%2Fsign-in&client_id=sscs&response_type=code&state=gjJcK9U82h
  .group("MYA_020_SignIn")
  {
    exec(http("Sign In")
        .post(IdAMURL + "/login?redirect_uri=" + MYABaseURL + "%2Fsign-in&client_id=sscs&response_type=code&state=PQ1CdmGUl0")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("username", "perfsscs01@mailinator.com") // perftest-sscs@perftest12345.com
        .formParam("password", "Pa55word11")                    // Pa55word11
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "true")
        .formParam("_csrf", "${csrf}")
        .check(substring("This is the postcode used when the appeal was registered").count.saveAs("postCodeTrue")) // If this is true, need to enter post code 1st
        .check(substring("Status of your appeal").count.saveAs("appealStatus"))) // count for how many draft cases
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter PostCode - this is conditional and only done the first time a user logs In
/*
  .doIf ("${postCodeTrue}")
  {
    .group("MYA_030_PostCode")
    {
    exec(http("Enter PostCode")
        .post(BaseURL + "/assign-case")
        .headers(CommonHeader) 
        .headers(PostHeader) 
        .formParam("_csrf", "${csrf}")
        .formParam("postcode", "TS1 1ST")
        .formParam("assign-case", "Continue")
        .check(substring("Status of your appeal")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)
  }
*/
  // Click on Hearing

  .group("MYA_040_ViewHearing")
  {
    exec(http("Click Hearing")
      .get(MYABaseURL + "/hearing")
      .headers(CommonHeader) 
      .check(substring("The hearing for your")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Click on Provide Evidence
  
  .group("MYA_050_TaskList")
  {
    exec(http("Click Provide Evidence")
      .get(MYABaseURL + "/task-list")
      .headers(CommonHeader) 
      .check(substring("Provide evidence to the tribunal")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Click Submit Your Evidence Link
  
  .group("MYA_060_SubmitEvidenceLink")
  {
    exec(http("Submit Evidence Link")
      .get(MYABaseURL + "/additional-evidence")
      .headers(CommonHeader) 
      .check(regex("""name="_csrf" value="(.+)">""").saveAs("csrf"))   
      .check(substring("How do you want to provide additional information")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Select radio buton to upload and continue
  
  .group("MYA_070_UploadOption")
  {
    exec(http("Upload Additional Evidence")
      .post(MYABaseURL + "/additional-evidence")
      .headers(CommonHeader) 
      .headers(PostHeader) 
      .formParam("additional-evidence-option", "upload")
      .formParam("_csrf", "${csrf}")
      .formParam("continue", "")
      .check(regex("""_csrf=(.+)" method""").saveAs("csrf"))   
      .check(substring("You can upload letters, documents, photos or videos")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter text in the box to describe the evidence and click on choose file
  
  .group("MYA_080_UploadFile")
  {
    exec(http("Desribe and Upload File")
      .post(MYABaseURL + "/additional-evidence/upload?_csrf=${csrf}")
      .headers(CommonHeader) 
      .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryBOgHbWr4LuU2ZuBi") 
      .formParam("_csrf", "${csrf}")
      .bodyPart(RawFileBodyPart("additional-evidence-file", "MYA2MbFile.pdf")
      .fileName("MYA2MbFile.pdf")
      .transferEncoding("binary")).asMultipartForm
      .check(regex("""_csrf=(.+)" method""").saveAs("csrf"))   
      .check(substring("""value="Delete" class""")))
    }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Submit Evidence to the tribunal button
  
  .group("MYA_090_SubmitEvidence")
  {
    exec(http("Submit Evidence Tribunal")
      .post(MYABaseURL + "/additional-evidence/upload?_csrf=${csrf}")
      .headers(CommonHeader) 
      .headers(PostHeader) 
      .formParam("additional-evidence-description", "The evidence that is attached is for performance testing. I am now describing what the evidence shows by writing the information in this text box. I know the details I enter here will be transformed by DocMosis into a PDF")
      .formParam("additional-evidence-file", "(binary)")
      .formParam("buttonSubmit", "Submit evidence to the tribunal")
      .check(substring("You have submitted additional evidence")))
    }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Click Appeal Details
  
  .group("MYA_100_AppealDetails")
  {
    exec(http("Appeal Details")
      .get(MYABaseURL + "/your-details")
      .headers(CommonHeader) 
      .check(substring("Appellant contact email address")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)


  // Click - Your Pip benefit Appeal breadcrumb
  
  .group("MYA_110_YourAppeal")
  {
    exec(http("Your Benefit Appeal")
      .get(MYABaseURL + "/status")
      .headers(CommonHeader) 
      .check(substring("Status of your appeal")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

    // Sign Out
  
  .group("MYA_120_SignOut")
  {
    exec(http("Sign Out")
      .get(MYABaseURL + "/sign-out")
      .headers(CommonHeader) 
      .check(substring("Sign in or create an account")))
    }

    .pause(MinThinkTime seconds,MaxThinkTime seconds)

}
