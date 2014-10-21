package com.briskware.dbpedia

import scala.io.StdIn

object Repl extends App {

  val empty = """^\s*$""".r
  var exit = false

  while (!exit) {
    val ln = StdIn.readLine(">>> ")
    ln match {
      case "exit" =>
        DbpediaSparqlClient.system.shutdown()
        exit = true
      case empty() =>
      case x: String => println(Question.askPolitely(x))
    }
  }

}
