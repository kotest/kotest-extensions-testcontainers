@file:Suppress("SqlResolve")

package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer

private val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
   withInitScript("init.sql")
   startupAttempts = 1
   withUrlParam("connectionTimeZone", "Z")
   withUrlParam("zeroDateTimeBehavior", "convertToNull")
}

private val ext = JdbcDatabaseContainerExtension(mysql)

class SharedJdbcTestContainerExtensionSpecTest1 : FunSpec() {
   init {

      val ds = install(ext) {
         maximumPoolSize = 8
         minimumIdle = 4
      }

      test("should initialize once per module") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT * FROM hashtags")
            rs.next()
            rs.getString("tag") shouldBe "startrek"

            it.createStatement().executeUpdate("INSERT INTO hashtags(tag) VALUES ('foo')")

            val rs2 = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs2.next()
            rs2.getLong(1) shouldBe 2
         }
      }

      test("another test should use the same container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 2
         }
      }
   }
}

class SharedJdbcTestContainerExtensionSpecTest2 : FunSpec() {
   init {

      val ds = install(ext)

      test("another spec should use the same container") {
         ds.connection.use {
            val rs = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rs.next()
            rs.getLong(1) shouldBe 2
         }
      }
   }
}
