package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer

class DatabaseContainerTest : FunSpec() {
   init {

      val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
         withInitScript("init.sql")
         startupAttempts = 1
         withUrlParam("connectionTimeZone", "Z")
         withUrlParam("zeroDateTimeBehavior", "convertToNull")
      }

      val ds = install(JdbcTestContainerExtension(mysql)) {
         maximumPoolSize = 8
         minimumIdle = 4
      }

      test("read from database") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT * FROM hashtags")
            rs.next()
            rs.getString("tag") shouldBe "startrek"
         }
      }
   }
}
