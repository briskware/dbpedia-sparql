dbpedia-sparql
==============

Sparql Client and Playground for the http://dbpedia.org/sparql endpoint

Uses Spray and Futures with the Akka Dispatcher to send queries to the Dbpedia Sparql endpoint,
querying the age and the birth place of a foaf:Person.

# Instructions

1. Check out the project
```
% git clone https://github.com/briskware/dbpedia-sparql.git
```
2. Change to the cloned directory
```
% cd dbpedia-sparql
```
3. Perform a clean build and run the tests
```
% sbt clean test
```
