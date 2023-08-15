package io.github.greenleafoss.mongo


import de.flapdoodle.embed.mongo.commands.{ImmutableMongodArguments, MongodArguments}
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.{ImmutableMongod, Mongod, RunningMongodProcess}
import de.flapdoodle.embed.process.io.{ImmutableProcessOutput, ProcessOutput, Processors, Slf4jLevel}
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.transitions.Start
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.slf4j.{Logger, LoggerFactory}

object TestMongoServer {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val mongoPort: Int = 27027
  private val mongoVersion: Version.Main = Version.Main.V6_0

  private val processOutput: ImmutableProcessOutput = ProcessOutput.builder
    .commands(Processors.logTo(logger, Slf4jLevel.DEBUG))
    .output(Processors.logTo(logger, Slf4jLevel.INFO))
    .error(Processors.logTo(logger, Slf4jLevel.ERROR))
    .build

  // @see https://www.mongodb.com/docs/manual/reference/program/mongod/
  private val mongodArguments: ImmutableMongodArguments = MongodArguments.defaults
    .withSyncDelay(0)
    .withStorageEngine("ephemeralForTest")
    .withUseNoJournal(true)
    .withUseNoPrealloc(true)

  private val mongod: ImmutableMongod = Mongod.instance
    .withNet(Start.to(classOf[Net]).initializedWith(Net.defaults.withPort(mongoPort)))
    .withProcessOutput(Start.to(classOf[ProcessOutput]).initializedWith(processOutput))
    .withMongodArguments(Start.to(classOf[MongodArguments]).initializedWith(mongodArguments))

  def start(): TransitionWalker.ReachedState[RunningMongodProcess] =
    mongod.start(mongoVersion)

  def stop(runningMongod: TransitionWalker.ReachedState[RunningMongodProcess]): Unit =
    if (runningMongod.current().isAlive) runningMongod.close()
}

trait TestMongoServer extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  private val runningMongoDb = TestMongoServer.start()

  // we can preload test data here if needed
  override protected def beforeAll(): Unit = super.beforeAll()

  override protected def afterAll(): Unit = TestMongoServer.stop(runningMongoDb)
}

object TestMongoServerApp extends App {
  import scala.io.StdIn.readLine

  private val runningMongod = TestMongoServer.start()

  readLine("Press 'Enter' to shutdown MongoDB")

  TestMongoServer.stop(runningMongod)
}
