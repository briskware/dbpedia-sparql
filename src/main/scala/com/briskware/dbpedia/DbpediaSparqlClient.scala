package com.briskware.dbpedia

import java.net.URLEncoder

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import akka.io.IO

import spray.can.Http
import spray.http._
import spray.http.HttpMethods._
import spray.client.pipelining._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._


object DbpediaSparqlClient {
  val encoding = "UTF-8"
  val responseFormat = enc("application/sparql-results+json")
  private def enc(v: String) = URLEncoder.encode(v, encoding)
}

class DbpediaSparqlClient(val hostUrl: String, val dbTimeoutInMillis: Long, val debug: Boolean) {
  import DbpediaSparqlClient._

  implicit val system: ActorSystem = ActorSystem()
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
