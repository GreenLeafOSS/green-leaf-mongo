package io.github.greenleafoss.mongo.core.mongo

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.Defaults.runtimeConfigFor
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.packageresolver.Command
import de.flapdoodle.embed.process.runtime.Network

object TestMongoServerApp:

  @main def main(): Unit =
    val runtimeCfg = runtimeConfigFor(Command.MongoD)
      // .processOutput(ProcessOutput.silent())
      // .processOutput(new ProcessOutput(new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor(), new ConsoleOutputStreamProcessor()))
      // .processOutput(ProcessOutput.)
      .build()

    val starter: MongodStarter = MongodStarter.getInstance(runtimeCfg)

    val mongoCfg = MongodConfig
      .builder()
      .version(Version.Main.V6_0)
      .net(new Net("localhost", 27027, Network.localhostIsIPv6()))
      .build()

    starter.prepare(mongoCfg).start()
