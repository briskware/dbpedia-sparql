dbpedia-sparql
==============

Sparql Client and Playground for the http://dbpedia.org/sparql endpoint

Uses Spray and Futures with the Akka Dispatcher to send queries to the Dbpedia Sparql endpoint,
querying the age and the birth place of a foaf:Person.

# Instructions
Check out the project
```% git clone https://github.com/briskware/dbpedia-sparql.git```
Change to the cloned directory
```% cd dbpedia-sparql```
Perform a clean build and run the tests
```% sbt clean test```

# Running the REPL
A run-evaluate-print-loop (REPL) is provided for playing around.
```
% sbt run
>>> What is the birth place of David Cameron?
Success(London, United Kingdom)
>>> How old is Tony Blair?
Success(61)
>>> about: Patrick Stewart
Success(Person(http://dbpedia.org/resource/Patrick_Stewart,Patrick Stewart,74,Mirfield, United Kingdom))
>>> exit
```
