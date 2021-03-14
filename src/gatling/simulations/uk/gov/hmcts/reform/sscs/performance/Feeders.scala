package uk.gov.hmcts.reform.sscs.performance

object Feeders {


  val random = new scala.util.Random

  val repeat  = List(1, 2, 3,4,5)

  def sequenceValue() =
    Stream.continually(repeat.toStream).flatten.take(5).toList

  def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString


  def randomAlphanumericString(n: Int) =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)



  var generatedEmail = ""
  var generatedPassword = ""
  var generatedEmailForCase = ""
  var orgName = ""
  var appReferenceName = ""
  var sequence1=0
  var seq = 1

  def nextSeq() : Integer = {

    seq = seq + 1
        seq
    }
  
  
  val DataFeederWith3Drafts = Iterator.continually(Map("service" -> ({
    "3Drafts"
  }),
    "Thinktime" -> ({
      "120"
    })

  ));

  val DataFeederWith10Drafts = Iterator.continually(Map("service" -> ({
    "10Drafts"
  }),
    "Thinktime" -> ({
      "120"
    })
  ));

  val DataFeederWith15Drafts = Iterator.continually(Map("service" -> ({
    "15Drafts"
  }),
    "Thinktime" -> ({
      "120"
    })
  ));
  
  val DataFeederNewApplications = Iterator.continually(Map("service" -> ({
    "NewAppeals"
  }),
    "Thinktime" -> ({
      "75"
    })
  ));
  
  

  val DataFeederWithMYA = Iterator.continually(Map("service" -> ({
    "MYA"
  }),
    "SignoutNumber" -> ({
      "070"
    })
  ));
  
  


}



