package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.containers.JdbcDatabaseContainer
import java.sql.Connection

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that are started the first time they are
 * installed in a test, and then shared throughout the same gradle module. The container is shutdown
 * after all specs have completed.
 *
 * If no spec is executed that installs a particular container, then that container is never started.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The pool can be configured in the constructor through the [configure] parameter, to avoid
 * needing to pass the same configuration to each invocation of install.
 *
 * Note: This extension requires Kotest 5.0+
 *
 * @param container the specific database test container type
 * @param beforeSpec a beforeSpec callback, can be used to configure the [HikariDataSource]
 * @param afterSpec an afterSpec callback, can be used to configure the [HikariDataSource]
 * @param beforeTest a beforeTest callback, can be used to configure the [HikariDataSource]
 * @param afterTest a afterTest callback, can be used to configure the [HikariDataSource]
 * @param configure a callback to configure the Hikari config object.
 *
 * @since 1.3.0
 */
class SharedJdbcDatabaseContainerExtension(
   private val container: JdbcDatabaseContainer<*>,
   private val beforeTest: (HikariDataSource) -> Unit = {},
   private val afterTest: (HikariDataSource) -> Unit = {},
   private val beforeSpec: (HikariDataSource) -> Unit = {},
   private val afterSpec: (HikariDataSource) -> Unit = {},
   private val afterStart: (HikariDataSource) -> Unit = {},
   private val configure: TestContainerHikariConfig.() -> Unit = {},
) : MountableExtension<Unit, HikariDataSource>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   private var ds: HikariDataSource? = null

   override fun mount(configure: Unit.() -> Unit): HikariDataSource {
      if (!container.isRunning) {
         container.start()
         ds = createDataSource().apply(afterStart)
      }
      return ds ?: error("DataSource was not initialized")
   }

   override suspend fun afterProject() {
      if (container.isRunning) container.stop()
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec(ds ?: error("DataSource was not initialized"))
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec(ds ?: error("DataSource was not initialized"))
   }

   private fun runInitScripts(connection: Connection, dbInitScripts: List<String>) {

      val scriptRunner = ScriptRunner(connection)

      if (dbInitScripts.isNotEmpty()) {
         dbInitScripts.forEach {
            val resourceList = ResourceLoader().resolveResource(it)

            resourceList
               .filter { resource -> resource.endsWith(".sql") }
               .forEach { resource ->
                  scriptRunner.runScript(resource.loadToReader())
               }
         }
      }
   }

   private fun createDataSource(): HikariDataSource {
      val config = TestContainerHikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      val ds = HikariDataSource(config)
      runInitScripts(ds.connection, config.dbInitScripts)
      return ds
   }
}
