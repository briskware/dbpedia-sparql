dbpedia-sparql
==============

Sparql Client and Playground for the http://dbpedia.org/sparql endpoint

Uses Spray and Futures with the Akka Dispatcher to send queries to the Dbpedia Sparql endpoint,
querying the age and the birht place of a foaf:Person.

Instructions:

1) Check out the project

% git clone https://github.com/briskware/dbpedia-sparql.git

2) Build and run the tests

% sbt test

