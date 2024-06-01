package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.MountableFile.forClasspathResource

class JdbcMultiScriptContainerInitTest : FunSpec({

   val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
      startupAttempts = 1
      withUrlParam("connectionTimeZone", "Z")
      withUrlParam("zeroDateTimeBehavior", "convertToNull")

      val sqlInitDir = "/docker-entrypoint-initdb.d/"
      withCopyToContainer(forClasspathResource("init.sql"), sqlInitDir + "000-init.sql")
      withCopyToContainer(forClasspathResource("sql-changesets/001-people.sql"), sqlInitDir + "001-people.sql")
      withCopyToContainer(forClasspathResource("sql-changesets/002-places.sql"), sqlInitDir + "002-places.sql")
   }

   val ds = install(JdbcDatabaseContainerExtension(mysql, ContainerLifecycleMode.Spec)) {
      maximumPoolSize = 8
      minimumIdle = 4
   }


   context("with container"){
      test("db should init multiple changeset files") {
         ds.connection.use {

            it.createStatement().executeUpdate("INSERT INTO hashtags(tag) VALUES ('foo')")
            var rsCount = it.createStatement().executeQuery("SELECT count(*) FROM hashtags")
            rsCount.next()
            rsCount.getLong(1) shouldBe 2

            it.createStatement().executeUpdate("INSERT INTO people(name, is_cool) VALUES ('Dima', true)")
            rsCount = it.createStatement().executeQuery("SELECT count(*) FROM people")
            rsCount.next()
            rsCount.getLong(1) shouldBe 3

            it.createStatement().executeUpdate("INSERT INTO places(city, state) VALUES ('New York', 'NY')")
            rsCount = it.createStatement().executeQuery("SELECT count(*) FROM places")
            rsCount.next()
            rsCount.getLong(1) shouldBe 3
         }
      }
   }

})
