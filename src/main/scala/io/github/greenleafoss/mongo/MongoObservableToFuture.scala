package io.github.greenleafoss.mongo

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{AggregateObservable, FindObservable, SingleObservable}
import org.mongodb.scala._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

trait MongoObservableToFuture {

  protected def findObservableAsSeq[T](x: FindObservable[Document])(implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Seq[T]] = {
    x.toFuture().map(_.map(_.toJson().parseJson.convertTo[T]))
  }

  protected def findObservableAsOpt[T](x: FindObservable[Document])(implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Option[T]] = {
    x.headOption().map(_.map(_.toJson().parseJson.convertTo[T]))
  }

  protected def findObservableAsObj[T](x: FindObservable[Document])(implicit jf: JsonFormat[T], ec: ExecutionContext): Future[T] = {
    x.head().map(_.toJson().parseJson.convertTo[T])
  }

  protected implicit class MongoFindObservableAsFutureDsl(x: FindObservable[Document]) {
    def asSeq[T](implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Seq[T]] = findObservableAsSeq(x)
    def asOpt[T](implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Option[T]] = findObservableAsOpt(x)
    def asObj[T](implicit jf: JsonFormat[T], ec: ExecutionContext): Future[T] = findObservableAsObj(x)
  }


  protected def singleObservableAsOpt[T](x: SingleObservable[Document])(implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Option[T]] = {
    x.toFutureOption().map { _.map(_.toJson().parseJson.convertTo[T]) }
  }

  protected implicit class MongoSingleObservableDocumentToFutureRes(x: SingleObservable[Document]) {
    def asOpt[T](implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Option[T]] = singleObservableAsOpt(x)
  }


  protected def aggObservableAsOpt[T](x: AggregateObservable[Document])(implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Seq[T]] = {
    x.toFuture().map(_.map(_.toJson().parseJson.convertTo[T]))
  }

  protected implicit class MongoAggObservableAsFutureDsl(x: AggregateObservable[Document]) {
    def asSeq[T](implicit jf: JsonFormat[T], ec: ExecutionContext): Future[Seq[T]] = aggObservableAsOpt(x)
  }

}

object MongoObservableToFuture extends MongoObservableToFuture
