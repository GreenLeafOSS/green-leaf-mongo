package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.*
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait GreenLeafMongoObservableToFutureOps:
  this: GreenLeafJsonBsonOps =>

  protected given ec: ExecutionContext

  // **************************************************
  // FindObservable
  // **************************************************

  protected def mongoFindObservableAsSeq[E: JsonFormat](x: FindObservable[Document]): Future[Seq[E]] =
    x.toFuture().map(seq => seq.map(_.toJson(jws).parseJson.convertTo[E]))

  protected def mongoFindObservableAsOpt[E: JsonFormat](x: FindObservable[Document]): Future[Option[E]] =
    x.headOption().map(opt => opt.map(_.toJson(jws).parseJson.convertTo[E]))

  protected def mongoFindObservableAsObj[E: JsonFormat](x: FindObservable[Document]): Future[E] =
    x.head().map(_.toJson(jws).parseJson.convertTo[E])

  extension (x: FindObservable[Document])
    protected def asSeq[E: JsonFormat]: Future[Seq[E]]    = mongoFindObservableAsSeq(x)
    protected def asOpt[E: JsonFormat]: Future[Option[E]] = mongoFindObservableAsOpt(x)
    protected def asObj[E: JsonFormat]: Future[E]         = mongoFindObservableAsObj(x)

  // **************************************************
  // SingleObservable
  // **************************************************

  protected def mongoSingleObservableAsOpt[E: JsonFormat](x: SingleObservable[Document]): Future[Option[E]] =
    x.headOption().map(opt => opt.map(_.toJson(jws).parseJson.convertTo[E]))

  protected def mongoSingleObservableAsObj[E: JsonFormat](x: SingleObservable[Document]): Future[E] =
    x.head().map(_.toJson(jws).parseJson.convertTo[E])

  extension (x: SingleObservable[Document])
    protected def asOpt[E: JsonFormat]: Future[Option[E]] = mongoSingleObservableAsOpt(x)
    protected def asObj[E: JsonFormat]: Future[E]         = mongoSingleObservableAsObj(x)

  // **************************************************
  // AggregateObservable
  // **************************************************

  protected def mongoAggregateObservableAsSeq[E: JsonFormat](x: AggregateObservable[Document]): Future[Seq[E]] =
    x.toFuture().map(seq => seq.map(_.toJson(jws).parseJson.convertTo[E]))

  extension (x: AggregateObservable[Document])
    protected def asSeq[E: JsonFormat]: Future[Seq[E]] = mongoAggregateObservableAsSeq(x)
