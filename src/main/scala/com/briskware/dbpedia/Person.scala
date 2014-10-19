package com.briskware.dbpedia

import java.net.URI

import spray.json.{JsString, JsArray, JsObject, JsValue}

case class Person(uri: URI, name: String, age: Int, birthPlace: String)

object Person {

  /**
   * Providing a dumbed down JSON converter for Persons.
   * Ideally spray-json could have been used but this proved to be a bit quicker.
   * @param json
   * @return
   */
  def apply(json: JsObject): Person = {

    def value(o: JsValue) = o.asJsObject.getFields("value") match {
      case Seq(JsString(s)) => s
    }

    json.getFields("results").head.asJsObject.getFields("bindings").head match {
      case JsArray(Seq(a))  =>
        a.asJsObject.getFields("uri", "name", "age", "birthPlace") match {
        case Seq(uri,name,age,birthPlace) =>
          Person(
            new URI(value(uri)),
            value(name),
            value(age).toInt,
            value(birthPlace)
          )
      }
    }
  }

  def sparql(uri: Option[URI] = None, name: Option[String] = None): String = {

    lazy val idFragment = (uri, name) match {
      case (Some(uri),_) =>
        s"""
           |    <$uri> rdf:type foaf:Person;
           |       rdfs:label ?nameLbl.
           |    FILTER(langMatches(lang(?nameLbl), "en"))
           |    BIND(str(?nameLbl) as ?name)
           |    BIND(<$uri> as ?uri)
         """.stripMargin
      case (_, Some(name)) =>
        s"""
           |    ?uri rdf:type foaf:Person;
           |       rdfs:label "${name}"@en.
           |    BIND("${name}" as ?name)
         """.stripMargin
      case _ => throw new IllegalArgumentException("invalid params")
    }

    s"""
         |PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
         |SELECT ?uri, ?name, ?age, ?birthPlace WHERE {
         |   $idFragment
         |   OPTIONAL {
         |      ?uri dbpedia-owl:birthDate ?dob.
         |      BIND(floor((now() - ?dob)/3600/24/364.25) as ?age)
         |   }
         |   OPTIONAL {
         |      ?uri dbpedia-owl:birthPlace ?pob.
         |      ?pob rdf:type dbpedia-owl:Settlement.
         |      ?pob rdfs:label ?pobLbl.
         |      FILTER(langMatches(lang(?pobLbl), "en")).
         |      OPTIONAL {
         |         ?pob dbpedia-owl:country ?country.
         |         ?country rdfs:label ?countryLbl.
         |         FILTER( langMatches(lang(?countryLbl), "en")).
         |      }
         |      BIND(IF(!bound(?countryLbl), str(?pobLbl), concat(str(?pobLbl),", ", str(?countryLbl))) as ?birthPlace)
         |   }
         |}
       """.stripMargin
  }
}

