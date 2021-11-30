package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import javax.sql.DataSource

/**
 * A Kotest extension for [JdbcDatabaseContainer] that will launch the container upon install,
 * and close after the spec has completed.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The pool can be configured in the mount functions configure method.
 *
 * @param container the specific test container type
 *
 * Note: This extension requires Kotest 5.0+
 */
class JdbcTestContainerExtension(
   private val container: JdbcDatabaseContainer<Nothing>,
) : MountableExtension<HikariConfig, DataSource>, AfterSpecListener {

   override fun mount(configure: HikariConfig.() -> Unit): HikariDataSource {
      container.start()
      val config = HikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      return HikariDataSource(config)
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}

