package uk.gov.hmcts.reform.sscs.performance.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Random

object Common {

  val rnd = new Random()
  val now = LocalDate.now()
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getDate(): String = {
    now.format(patternDate)
  }

  def getDay(): String = {
    (1 + rnd.nextInt(28)).toString
  }

  def getMonth(): String = {
    (1 + rnd.nextInt(12)).toString
  }

  //Dob >= 25 years
  def getDobYear(): String = {
    now.minusYears(25 + rnd.nextInt(70)).format(patternYear)
  }
  //Dod <= 21 years
  def getDodYear(): String = {
    now.minusYears(1 + rnd.nextInt(20)).format(patternYear)
  }

  def getPostcode(): String = {
    randomString(2).toUpperCase() + rnd.nextInt(10).toString + " " + rnd.nextInt(10).toString + randomString(2).toUpperCase()
  }

/*
  val reasonForAppealing = "Social Security and Child Support forms including notices of appeal to the Department of Work and Pensions and HM Revenue and Customs.
    You can appeal a decision about your entitlement to benefits, for example Personal Independence Payment (PIP), Employment and Support Allowance (ESA) and Universal Credit.
    Appeals are decided by the Social Security and Child Support Tribunal. The tribunal is impartial and independent of government. The tribunal will listen to both sides before making a decision.
    You can appeal a decision about your entitlement to benefits, for example Personal Independence Payment (PIP), Employment and Support Allowance (ESA) or Universal Credit.
    If you do not need a mandatory reconsideration your decision letter will say why. Include this explanation when you submit your appeal.
    You’ll need to choose whether you want to go to the tribunal hearing to explain your appeal in person. If you do not attend, your appeal will be decided on your appeal form and any supporting evidence you provide."

  val anythingElse = "Things that were not considered are. 
    You can appoint someone as a ‘representative’ to help you with your appeal. A representative can.help you submit your appeal or prepare your evidence act on your behalf give you advice. Anyone can be a representative, including friends and family.
    You might also be able to find a representative through a library or from an organisation in your area that gives advice on claiming benefits, such as Citizens Advice.
    Your representative will have permission to act on your behalf, for example to respond to letters. They’ll be sent all the information about your appeal, including any medical evidence."
*/
}