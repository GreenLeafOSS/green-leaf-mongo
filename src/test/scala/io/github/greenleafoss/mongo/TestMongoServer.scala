package io.github.greenleafoss.mongo

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net, RuntimeConfigBuilder}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{Command, MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.slf4j.LoggerFactory

trait TestMongoServer extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  protected var mongodExe: Option[MongodExecutable] = None
  protected var mongodPro: Option[MongodProcess] = None

  override protected def beforeAll(): Unit = {

    val mCfg = new MongodConfigBuilder()
      .version(Version.Main.PRODUCTION)
      // TODO from config
//      .net(new Net("localhost", 27017, Network.localhostIsIPv6()))
      .net(new Net("localhost", 27027, Network.localhostIsIPv6()))
      .build()

    val mExe = TestMongoServer.starter.prepare(mCfg)

    mongodExe = Option(mExe)
    mongodPro = Option(mExe.start())
  }

  override protected def afterAll(): Unit = {
    mongodPro.foreach(_.stop())
    mongodExe.foreach(_.stop())
  }

}


object TestMongoServer {

  // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo#usage---optimization

  private lazy val log = LoggerFactory.getLogger(getClass)

  private lazy val runtimeConfig: IRuntimeConfig = new RuntimeConfigBuilder()
    .defaultsWithLogger(Command.MongoD, log)
    .build()

  lazy val starter: MongodStarter = MongodStarter.getInstance(runtimeConfig)

}
