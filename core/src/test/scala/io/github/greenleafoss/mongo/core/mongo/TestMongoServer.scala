package io.github.greenleafoss.mongo.core.mongo

import io.github.greenleafoss.mongo.core.log.Log

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.Defaults.*
import de.flapdoodle.embed.mongo.config.MongoCmdOptions
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.packageresolver.Command
import de.flapdoodle.embed.process.config.process.ProcessOutput
import de.flapdoodle.embed.process.io.ConsoleOutputStreamProcessor
import de.flapdoodle.embed.process.io.Processors
import de.flapdoodle.embed.process.io.Slf4jLevel
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

object TestMongoServer extends Log:

  // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo#usage---optimization

  private val command = Command.MongoD

  private val runtimeConfig = runtimeConfigFor(command)
    .artifactStore(extractedArtifactStoreFor(command).withDownloadConfig(downloadConfigFor(command).build()))
    .processOutput(
      ProcessOutput.silent()
      // ProcessOutput
      //  .builder()
      //  .output(Processors.logTo(log, Slf4jLevel.DEBUG))
      //  .error(Processors.logTo(log, Slf4jLevel.ERROR))
      //  .commands(Processors.namedConsole("[console>]"))
      //  .build()
    )
    .build()

  protected lazy val mongodExe: MongodExecutable = MongodStarter
    .getInstance(runtimeConfig)
    .prepare(
      MongodConfig
        .builder()
        .version(Version.Main.V6_0)
        .net(new Net("localhost", 27027, Network.localhostIsIPv6()))
        .cmdOptions(
          MongoCmdOptions
            .builder()
            .storageEngine("ephemeralForTest")
            // https://docs.mongodb.com/manual/reference/configuration-options/#storage.syncPeriodSecs
            // If you set storage.syncPeriodSecs to 0, MongoDB will not sync the memory mapped files to disk.
            // If you set storage.syncPeriodSecs to 0 for testing purposes, you should also set --nojournal to true.
            .syncDelay(0)
            .useNoJournal(true)
            .build()
        )
        .build()
    )

trait TestMongoServer extends AsyncWordSpec with Matchers with BeforeAndAfterAll:
  private val mongod = TestMongoServer.mongodExe.start()

  // we can preload test data here if needed
  override protected def beforeAll(): Unit = super.beforeAll()

  override protected def afterAll(): Unit = if (mongod.isProcessRunning) mongod.stop()
