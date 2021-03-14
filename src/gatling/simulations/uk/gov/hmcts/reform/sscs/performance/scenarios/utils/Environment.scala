package uk.gov.hmcts.reform.sscs.performance.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://benefit-appeal.perftest.platform.hmcts.net"
  val myaULR = "https://sscs-cor.perftest.platform.hmcts.net"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val idamAPIURL = "https://idam-api.perftest.platform.hmcts.net"

  val minThinkTime = 190
  val maxThinkTime = 210
  val mrnDate="08"
  val mrnMonth="03"
  val mrnYear="2021"

  val HttpProtocol = http
  /*  .proxy(Proxy("proxyout.reform.hmcts.net", 8080)
      .httpsPort(8080))
      .noProxyFor(baseURL, idamURL)*/

  val commonHeader = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> " en-GB,en-US;q=0.9,en;q=0.8,en-AU;q=0.7,hr;q=0.6,be;q=0.5,br;q=0.4,ar;q=0.3",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "none",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1",
    "user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36",
    "sec-fetch-site" -> "same-origin"
  )
    
  val postHeader = Map(
    "content-type" -> "application/x-www-form-urlencoded"
  )

}