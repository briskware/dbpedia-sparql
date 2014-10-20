package com.briskware.dbpedia

import java.net.URLEncoder

import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.util.Timeout
import spray.http._
import spray.client.pipelining._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._


object DbpediaSparqlClient {
  val encoding = "UTF-8"
  val responseFormat = enc("application/sparql-results+json")
  private def enc(v: String) = URLEncoder.encode(v, encoding)

  private val config = ConfigFactory.parseString("akka.loglevel=WARNING").withFallback(ConfigFactory.load)

  implicit val system: ActorSystem = ActorSystem("dbpedia-sparql", config)
}

class DbpediaSparqlClient(val hostUrl: String, val dbTimeoutInMillis: Long, val debug: Boolean) {
  import DbpediaSparqlClient._

  implicit val timeout: Timeout = Timeout(dbTimeoutInMillis milliseconds)
  import system.dispatcher

  def response(sparql: String): Future[JsObject] = pipeline(Get(requestUri(sparql, dbTimeoutInMillis)))

  private val pipeline: HttpRequest => Future[JsObject] = {
    sendReceive ~>
    overrideContentType(MediaTypes.`application/json`) ~>
    unmarshal[JsObject]
  }

  private def requestUri(query: String, timeout: Long): Uri = {
    Uri(s"$hostUrl?query=${enc(query)}&format=$responseFormat&timeout=$dbTimeoutInMillis${if (debug) "&debug=on" else ""}")
  }

  def overrideContentType(mediaType: MediaType)(r: HttpResponse): HttpResponse = {
    r.withEntity(HttpEntity(ContentType(mediaType), r.entity.data))
  }

}
