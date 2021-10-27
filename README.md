# green-leaf-mongo
![GitHub](https://img.shields.io/github/license/GreenLeafOSS/green-leaf-mongo.svg)
[![Build Status](https://app.travis-ci.com/GreenLeafOSS/green-leaf-mongo.svg?branch=master)](https://app.travis-ci.com/GreenLeafOSS/green-leaf-mongo)

## Short description
This extension created on top of official [MongoDB Scala Driver](http://mongodb.github.io/mongo-scala-driver), allows to fully utilize [Spray JSON](https://github.com/spray/spray-json) and represents bidirectional serialization for case classes in BSON, as well as flexible DSL for [MongoDB query operators](https://docs.mongodb.com/manual/reference/operator/query/), documents and collections.

## Usage
```scala
// build.sbt
// https://mvnrepository.com/artifact/io.github.greenleafoss/green-leaf-mongo
libraryDependencies += "io.github.greenleafoss" %% "green-leaf-mongo" % "0.1.10"
```

## JSON and BSON protocols

`GreenLeafJsonProtocol` based on DefaultJsonProtocol from Spray JSON and allows to override predefined JsonFormats to make possible use custom seriallization in BSON format.
This trait also includes a few additional JsonFormats for _ZonedDateTime_, _ObjectId_, _scala Enumeration_ and _UUID_.

`GreenLeafBsonProtocol` extends `GreenLeafJsonProtocol` and overrides _Long_, _ZonedDateTime_, _ObjectId_, _scala Enumeration_, _UUID_ and _Regex_ JSON formats to represent them in related BSON formats.

These base protocols allow to simply (de)serialize this instance to and from both JSON and BSON the same way as in Spray JSON:
```scala
// MODEL
case class Test(id: ObjectId, i: Int, l: Long, b: Boolean, zdt: ZonedDateTime)

// JSON
trait TestJsonProtocol extends GreenLeafJsonProtocol {
  implicit def testJf = jsonFormat5(Test)
}
object TestJsonProtocol extends TestJsonProtocol

// BSON
trait TestBsonProtocol extends TestJsonProtocol with GreenLeafBsonProtocol {
  override implicit def testJf = jsonFormat(Test, "_id", "i", "l", "b", "zdt")
}
object TestBsonProtocol extends TestBsonProtocol
```

Once protocols defined, we can make instance of Test case class and use TestJsonProtocol to print related JSON:
```scala
val obj = Test(new ObjectId("5c72b799306e355b83ef3c86"), 1, 0x123456789L, true, "1970-01-01")

import TestJsonProtocol._
println(obj.toJson.prettyPrint)
```
Output in this case will be:
```js
{
  "id": "5c72b799306e355b83ef3c86",
  "i": 1,
  "l": 4886718345,
  "b": true,
  "zdt": "1970-01-01 00:00:00"
}
```

Changing single line of import `TestJsonProtocol` to `TestBsonProtocol` allows us to (de)serialize this instance to and from BSON:

```scala
val obj = Test(new ObjectId("5c72b799306e355b83ef3c86"), 1, 0x123456789L, true, "1970-01-01")

import TestBsonProtocol._
println(obj.toJson.prettyPrint)
```

Output in this case will be:
```js
{
  "_id": {
    "$oid": "5c72b799306e355b83ef3c86"
  },
  "i": 1,
  "l": {
    "$numberLong": "4886718345"
  },
  "b": true,
  "zdt": {
    "$date": 0
  }
}
```

Full code of the examples above available in `GreenLeafJsonAndBsonProtocolsTest`.

## GreenLeafMongoDsl
Import `GreenLeafMongoDsl._` makes it possible to write queries with a syntax that is more close to real queries in MongoDB, as was implemented in [Casbah Query DSL](http://mongodb.github.io/casbah/3.1/reference/query_dsl/).

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
"price" $not { _ $gte 5.1 }
$nor( "price" $eq 1.99 , "qty" $lt 20, "sale" $eq true )
"qty" $exists true
// ...
```

More examples of queries available in `GreenLeafMongoDslTest`.


## GreenLeafMongoDao
`GreenLeafMongoDao` extends `GreenLeafMongoDsl` and provides simple DSL to transform Mongo's _Observable[Document]_ instances to _Future[Seq[T]]_, _Future[Option[T]]_ and _Future[T]_.
In addition this trait provides many useful generic methods such as _insert_, _getById_, _findById_, _updateById_, _replaceById_ and others.
You can find more details and examples in [EntityWithIdAsFieldDaoTest](https://github.com/GreenLeafOSS/green-leaf-mongo/blob/master/src/test/scala/io/github/greenleafoss/mongo/EntityWithIdAsFieldDaoTest.scala), [EntityWithIdAsObjectDaoTest](https://github.com/GreenLeafOSS/green-leaf-mongo/blob/master/src/test/scala/io/github/greenleafoss/mongo/EntityWithIdAsObjectDaoTest.scala), [EntityWithOptionalFieldsDaoTest](https://github.com/GreenLeafOSS/green-leaf-mongo/blob/master/src/test/scala/io/github/greenleafoss/mongo/EntityWithOptionalFieldsDaoTest.scala) and [EntityWithoutIdDaoTest](https://github.com/GreenLeafOSS/green-leaf-mongo/blob/master/src/test/scala/io/github/greenleafoss/mongo/EntityWithoutIdDaoTest.scala).

