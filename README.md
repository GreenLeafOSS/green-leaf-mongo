# green-leaf-mongo
![GitHub](https://img.shields.io/github/license/GreenLeafOSS/green-leaf-mongo.svg)
[![Build Status](https://app.travis-ci.com/GreenLeafOSS/green-leaf-mongo.svg?branch=master)](https://app.travis-ci.com/GreenLeafOSS/green-leaf-mongo)
[![Scala CI](https://github.com/GreenLeafOSS/green-leaf-mongo/actions/workflows/scala.yml/badge.svg)](https://github.com/GreenLeafOSS/green-leaf-mongo/actions/workflows/scala.yml)
[![green-leaf-mongo-core](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo-core/latest-by-scala-version.svg)](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo)
[![green-leaf-mongo-spray](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo-spray/latest-by-scala-version.svg)](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo)
[![green-leaf-mongo-play](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo-play/latest-by-scala-version.svg)](https://index.scala-lang.org/greenleafoss/green-leaf-mongo/green-leaf-mongo)


## Short description
This extension created on top of official [MongoDB Scala Driver](https://mongodb.github.io/mongo-scala-driver) and allows to fully utilize [Spray JSON](https://github.com/spray/spray-json) or [Play JSON](https://github.com/playframework/play-json) to represent bidirectional serialization for case classes in BSON, as well as flexible DSL for [MongoDB query operators](https://www.mongodb.com/docs/manual/reference/operator/query/), documents and collections.

It was introduced in 2019 - [Andrii Lashchenko at #ScalaUA - Spray JSON and MongoDB Queries: Insights and Simple Tricks
](https://www.youtube.com/watch?v=NBgKkQtydAo)
Related slides available at https://www.slideshare.net/lashchenko/spray-json-and-mongodb-queries-insights-and-simple-tricks

[<img src="https://image.slidesharecdn.com/sprayjsonandmongodbqueries-190330123716/75/spray-json-and-mongodb-queries-insights-and-simple-tricks-1-2048.jpg" width="75%" geight="75%" />](https://www.slideshare.net/lashchenko/spray-json-and-mongodb-queries-insights-and-simple-tricks)


## Usage
```scala
// build.sbt

// https://mvnrepository.com/artifact/io.github.greenleafoss/green-leaf-mongo-core
// `green-leaf-mongo-core` can be used if you want to create your own extension

// https://mvnrepository.com/artifact/io.github.greenleafoss/green-leaf-mongo-spray
libraryDependencies += "io.github.greenleafoss" %% "green-leaf-mongo-spray" % "3.0"

// https://mvnrepository.com/artifact/io.github.greenleafoss/green-leaf-mongo-play
libraryDependencies += "io.github.greenleafoss" %% "green-leaf-mongo-play" % "3.0"
```

## JSON and BSON protocols

`GreenLeafMongoJsonBasicFormats` based on DefaultJsonProtocol from Spray JSON and allows to override predefined JsonFormats to make possible use custom serialization in BSON format.
This trait also includes a few additional JsonFormats for _LocalDate_, _LocalDateTime_, _ZonedDateTime_, _ObjectId_, _scala Enumeration_ and _UUID_.

`PlayJsonProtocol` is a related extension for Play JSON library and `PlayJsonProtocol` for Spray JSON library. 

`SprayBsonProtocol`/`PlayBsonProtocol` extends related JsonProtocols and overrides _Int_, _Long_, _BigDecimal_, _LocalDate_, _LocalDateTime_, _ZonedDateTime_, _ObjectId_, _scala Enumeration_, _UUID_ and _Regex_ JSON formats to represent them in related BSON (MongoDB Extended JSON V2) formats https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-extended-json-v2-usage.

These base protocols allow to simply (de)serialize this instance to and from both JSON and BSON the same way as in Spray JSON:
```scala 3
// MODEL
case class Test(_id: ObjectId, i: Int, l: Long, b: Boolean, zdt: ZonedDateTime)

// JSON
trait TestJsonProtocol extends SprayJsonProtocol:
  given testJsonFormat = jsonFormat5(Test)

object TestJsonProtocol extends TestJsonProtocol

// BSON
object TestBsonProtocol extends TestJsonProtocol with SprayBsonProtocol
```

Once protocols defined, we can make instance of Test case class and use TestJsonProtocol to print related JSON:
```scala
val obj = Test(new ObjectId("5c72b799306e355b83ef3c86"), 1, 0x123456789L, true, "1970-01-01")

import TestJsonProtocol.given
println(obj.toJson.prettyPrint)
```
Output in this case will be:
```js
{
  "_id": "5c72b799306e355b83ef3c86",
  "i": 1,
  "l": 4886718345,
  "b": true,
  "zdt": "1970-01-01T00:00:00Z"
}
```

Changing single line of import `TestJsonProtocol` to `TestBsonProtocol` allows us to (de)serialize this instance to and from BSON:

```scala
val obj = Test(new ObjectId("5c72b799306e355b83ef3c86"), 1, 0x123456789L, true, "1970-01-01")

import TestBsonProtocol.given
println(obj.toJson.prettyPrint)
```

Output in this case will be:
```js
{
  "_id": {
    "$oid": "5c72b799306e355b83ef3c86"
  },
  "i": {
    "$numberInt": "1"
  },
  "l": {
    "$numberLong": "4886718345"
  },
  "b": true,
  "zdt": {
    "$date": {
      "$numberLong": "0"
    }
  }
}
```

More examples available in implementation of **JsonProtocolSpec**/**BsonProtocolSpec** in Spray and Play project modules.

## GreenLeafMongoDsl
`GreenLeafMongoFilterOps` makes it possible to write queries with a syntax that is more close to real queries in MongoDB, as was implemented in [Casbah Query DSL](http://mongodb.github.io/casbah/3.1/reference/query_dsl/).

```scala
"size" $all ("S", "M", "L")
"price" $eq 10
"price" $gt 10
"price" $gte 10
"size" $in ("S", "M", "L")
"price" $lt 100
"price" $lte 100
"price" $ne 1000
"size" $nin ("S", "XXL")
$or( "price" $lt 5, "price" $gt 1, "promotion" $eq true )
$and( "price" $lt 5, "price" $gt 1, "stock" $gte 1 )
"price" $not { $gte (5.1)  }
$nor( "price" $eq 1.99 , "qty" $lt 20, "sale" $eq true )
"qty" $exists true
"results" $elemMatch $and("product" $eq "xyz", "score" $gte 8)
// ...
```

More examples of queries available in **GreenLeafMongoFilterOpsSpec**.


## GreenLeafMongoDao
`GreenLeafMongoDao` extends `GreenLeafMongoObservableToFutureOps` with `GreenLeafMongoFilterOps` and provides simple DSL to transform Mongo's _Observable[Document]_ instances to _Future[Seq[T]]_, _Future[Option[T]]_ and _Future[T]_.
In addition, this trait provides many useful generic methods such as _insert_, _getById_, _findById_, _updateById_, _replaceById_ and others.
`SprayMongoDao`/`PlayMongoDao` are related implementations for Spray and Play JSON libraries. 
You can find more details and examples in the dao tests.

