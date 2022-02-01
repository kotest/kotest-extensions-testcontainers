package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.util.stream.Collectors
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class TestContainerHikariConfig : HikariConfig() {

   var dbInitScripts: List<String> = emptyList()

   fun runInitScripts(connection: Connection) {

      val scriptRunner = ScriptRunner(connection)

      if (dbInitScripts.isNotEmpty()) {
         dbInitScripts.forEach {

            val path = Paths.get(javaClass.getResource(it)?.toURI() ?: return@forEach)

            if (path.isRegularFile()) {
               scriptRunner.runScript(path.inputStream().reader())
            } else if (path.isDirectory()) {

               val sqlFiles = Files.walk(path)
                  .filter { file -> file.isRegularFile() }
                  .filter { file -> file.toString().endsWith(".sql", true) }
                  .sorted()
                  .collect(Collectors.toList())

               sqlFiles.forEach { sqlFilePath ->
                  scriptRunner.runScript(sqlFilePath.inputStream().reader())
               }
            }
         }
      }

   }
}
