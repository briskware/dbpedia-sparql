package com.briskware.dbpedia


import org.scalatest.FunSuite
import org.scalatest.concurrent._
import ScalaFutures._
import org.scalatest.time.SpanSugar._
import spray.json.JsObject

import scala.concurrent.Future

class PersonSuite extends FunSuite {

  import DbpediaSparqlClient.system.dispatcher

  test("How old is Tony Blair") {
    val answer = Question.ask("How old is Tony Blair?")
    assert("61" === answer)
  }

  test("What is the birth place of David Cameron") {
    val answer = Question.ask("What is the birth place of David Cameron?")
    assert("London, United Kingdom" === answer)
  }


  test("Run Sparql request for Cameron") {
    val responseF: Future[JsObject] = Question.sparqlClient.response(
      Person.sparql(uri = Some(new java.net.URI("http://dbpedia.org/resource/David_Cameron"))))

    responseF.isReadyWithin(3 seconds)
    whenReady(responseF) {
      result =>
        val p = Person(result)
        assert(p.age === 48)
    }
  }

  test("Run Sparql request for Blair") {
    val responseF: Future[JsObject] = Question.sparqlClient.response(
      Person.sparql(uri = Some(new java.net.URI("http://dbpedia.org/resource/Tony_Blair"))))

    responseF.isReadyWithin(3 seconds)
    whenReady(responseF) {
      result =>
        val p = Person(result)
        assert(p.birthPlace === "Edinburgh")
    }
  }

}
