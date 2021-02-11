package uk.gov.hmcts.reform.sscs.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sscs.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object SSCS_SYA 
{

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val SSCSSYAJourney =

  // Launch Homepage

  group("SSCS_010_Homepage") {
      exec(http("Load SSCS Homepage")
          .get(BaseURL + "/")
          .headers(CommonHeader)
          .check(substring("Select a benefit type")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)

  // Enter PIP as Benefit Type 
  
  .group("SSCS_020_SelectBenefitType") {
      exec(http("Enter PIP")
          .post(BaseURL + "/benefit-type")
          .headers(CommonHeader) // additional
          .headers(PostHeader) // define these in env
          .formParam("benefitType", "Personal Independence Payment (PIP)")
          .check(substring("What language do you want us to use when")))
  }

  .pause(MinThinkTime seconds,MaxThinkTime seconds)
}
