package com.briskware.dbpedia

import spray.json.JsObject

import scala.concurrent.Future
import scala.util.{Try, Failure, Success}

object Question {

  import scala.concurrent.Await
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val timeout = Timeout(5 seconds)

  val hostUrl = "http://dbpedia.org/sparql"

  val sparqlClient = new DbpediaSparqlClient(hostUrl = hostUrl, dbTimeoutInMillis = 3000L, debug = true)
  import sparqlClient.system.dispatcher

  val age = """^How old is (.*)\?""".r
  val pob = """^What is the birth place of (.*)\?""".r

  def ask(q: String): String = {

    // Tuple2 where _1 is the Sparql to run and _2 is the value extractor
    val tuple: (String, Person => String) = {
      q match {
        case age(name) =>
          (Person.sparql(name = Some(name)), ( (person: Person) => person.age.toString))
        case pob(name) =>
          (Person.sparql(name = Some(name)), ( (person: Person) => person.birthPlace))
      }
    }

    // execute the query (async)
    val resultF: Future[JsObject] = sparqlClient.response(tuple._1)
    // block for the result of the future to arrive
    val json = Await.result(resultF, timeout.duration).asInstanceOf[JsObject]
    // map and return the result
    tuple._2(Person(json))
  }

}
