package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that are started the first time they are
 * installed in a spec.
 *
 * If no spec is executed that installs a particular container,
 * then that container is never started.
 *
 * Once mounted in a spec, the return value from the installation point is a configured HikariDataSource.
 *
 * @param container the specific database test container type
 * @param beforeSpec a beforeSpec callback
 * @param afterSpec an afterSpec callback
 * @param beforeTest a beforeTest callback
 * @param afterTest a afterTest callback
 *
 * @param beforeStart a callback that is invoked only once, just before the container is started.
 * If the container is never started, this callback will not be invoked.
 * This callback can be useful instead of the installation callback as it will only
 * be executed once, regardless of how many times this container is installed.
 *
 * @param afterStart a callback that is invoked only once, just after the container is started.
 * If the container is never started, this callback will not be invoked.
 *
 * @param beforeShutdown a callback that is invoked only once, just before the container is stopped.
 * If the container is never started, this callback will not be invoked.
 *
 * @param afterShutdown a callback that is invoked only once, just after the container is stopped.
 * If the container is never started, this callback will not be invoked.
 */
class JdbcDatabaseContainerExtension(
   private val container: JdbcDatabaseContainer<*>,
   private val beforeStart: (JdbcDatabaseContainer<*>) -> Unit = {},
   private val afterStart: (JdbcDatabaseContainer<*>) -> Unit = {},
   private val beforeShutdown: (JdbcDatabaseContainer<*>) -> Unit = {},
   private val beforeTest: suspend (HikariDataSource) -> Unit = {},
   private val afterTest: suspend (HikariDataSource) -> Unit = {},
   private val beforeSpec: suspend (HikariDataSource) -> Unit = {},
   private val afterSpec: suspend (HikariDataSource) -> Unit = {},
   private val afterShutdown: (HikariDataSource) -> Unit = {},
) : MountableExtension<TestContainerHikariConfig, HikariDataSource>,
   AfterProjectListener,
   BeforeTestListener,
   BeforeSpecListener,
   AfterTestListener,
   AfterSpecListener {

   /**
    * Mounts the container, starting it if necessary. The [configure] block will be invoked
    * every time the container is mounted, and after the container has started.
    */
   override fun mount(configure: TestContainerHikariConfig.() -> Unit): HikariDataSource {
      if (!container.isRunning) {
         beforeStart(container)
         container.start()
         afterStart(container)
      }
      return createDataSource(configure)
   }

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) {
         beforeShutdown(container)
         container.stop()
      }
   }

   private fun createDataSource(configure: TestContainerHikariConfig.() -> Unit): HikariDataSource {
      val config = TestContainerHikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      //      runInitScripts(ds.connection, config.dbInitScripts)
      return HikariDataSource(config)
   }
}
