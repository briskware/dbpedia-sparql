package com.briskware.dbpedia

import spray.json.JsObject

import scala.concurrent.Future
import scala.util.Try

object Question {

  import scala.concurrent.Await
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val timeout = Timeout(5 seconds)

  val hostUrl = "http://dbpedia.org/sparql"

  val sparqlClient = new DbpediaSparqlClient(hostUrl = hostUrl, dbTimeoutInMillis = 3000L, debug = true)

  val age = """^How old is (.*)\?""".r
  val pob = """^What is the birth place of (.*)\?""".r
  val about = """^about:\s*(.*)""".r

  def ask(q: String): String = {

    // Tuple2 where _1 is the name of the person to run and _2 is the value extractor
    val tuple: (String, Person => String) = {
      q match {
        case age(name) => (name, ( (person: Person) => person.age.toString))
        case pob(name) => (name, ( (person: Person) => person.birthPlace))
        case about(name) => (name, ( (person: Person) => person.toString))
      }
    }

    // execute the query (async)
    val resultF: Future[JsObject] = sparqlClient.response(Person.sparql(name = Some(tuple._1)))
    // block for the result of the future to arrive
    val json = Await.result(resultF, timeout.duration).asInstanceOf[JsObject]
    // map and return the result
    tuple._2(Person(json))
  }

  def askPolitely(q: String): Try[String] = {
    Try(ask(q))
  }

}
