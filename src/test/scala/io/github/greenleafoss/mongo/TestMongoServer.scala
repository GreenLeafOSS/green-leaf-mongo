package io.github.greenleafoss.mongo


import de.flapdoodle.embed.mongo.config.Defaults._
import de.flapdoodle.embed.mongo.config.{MongoCmdOptions, MongodConfig, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{Command, MongodExecutable, MongodStarter}
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

object TestMongoServer {

  // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo#usage---optimization

  private val command = Command.MongoD

  private val runtimeConfig = runtimeConfigFor(command)
    .artifactStore(extractedArtifactStoreFor(command).withDownloadConfig(downloadConfigFor(command).build()))
    // .processOutput(new ProcessOutput(new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor()))
    .build()

  protected lazy val mongodExe: MongodExecutable = MongodStarter.getInstance(runtimeConfig).prepare(
    MongodConfig.builder()
      .version(Version.Main.PRODUCTION)
      .net(new Net("localhost", 27027, Network.localhostIsIPv6()))
      .cmdOptions(
        MongoCmdOptions.builder()
          // https://docs.mongodb.com/manual/reference/configuration-options/#storage.syncPeriodSecs
          // If you set storage.syncPeriodSecs to 0, MongoDB will not sync the memory mapped files to disk.
          // If you set storage.syncPeriodSecs to 0 for testing purposes, you should also set --nojournal to true.
          .syncDelay(0)
          .useNoJournal(true)
          .build())
      .build())

}

trait TestMongoServer extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  private val mongod = TestMongoServer.mongodExe.start()

  // we can preload test data here if needed
  override protected def beforeAll(): Unit = super.beforeAll()

  override protected def afterAll(): Unit = if (mongod.isProcessRunning) mongod.stop()
}

object TestMongoServerApp {

  def main(args: Array[String]): Unit = {
    val runtimeCfg = runtimeConfigFor(Command.MongoD)
      //    .processOutput(new ProcessOutput(new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor()))
      .build()

    val starter: MongodStarter = MongodStarter.getInstance(runtimeCfg)

    val mongoCfg = MongodConfig.builder()
      .version(Version.Main.PRODUCTION)
      .net(new Net("localhost", 27027, Network.localhostIsIPv6()))
      .build()

    starter.prepare(mongoCfg).start()
  }

}
