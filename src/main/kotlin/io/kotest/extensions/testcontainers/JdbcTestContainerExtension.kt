package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger
import javax.sql.DataSource

/**
 * A Kotest [MountableExtension] for [JdbcDatabaseContainer]s that will launch the container
 * upon install, and close after the spec has completed.
 *
 * This extension will create a pooled [HikariDataSource] attached to the database and
 * return that to the user as the materialized value.
 *
 * The pool can be configured in the mount configure method.
 *
 * Note: This extension requires Kotest 5.0+
 *
 * @param container the specific test container type
 * @param lifecycleMode determines how the container should be reset between tests
 *
 * @since 1.1.0
 */
class JdbcTestContainerExtension(
   private val container: JdbcDatabaseContainer<Nothing>,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<HikariConfig, DataSource>, AfterSpecListener, TestListener {

   private val ds = SettableDataSource(null)
   private var configure: HikariConfig.() -> Unit = {}

   override fun mount(configure: HikariConfig.() -> Unit): DataSource {
      this.configure = configure
      if (lifecycleMode == LifecycleMode.Spec) {
         container.start()
         ds.setDataSource(createDataSource())
      }
      return ds
   }

   private fun createDataSource(): HikariDataSource {
      val config = HikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      config.configure()
      return HikariDataSource(config)
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         stop()
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         start()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         stop()
      }
   }

   private suspend fun start() {
      withContext(Dispatchers.IO) {
         container.start()
         ds.setDataSource(createDataSource())
      }
   }

   private suspend fun stop() {
      withContext(Dispatchers.IO) {
         ds.setDataSource(null)
         container.stop()
      }
   }
}

class SettableDataSource(private var ds: HikariDataSource?) : DataSource {

   private fun getDs(): DataSource = ds ?: error("DataSource is not ready")

   fun setDataSource(ds: HikariDataSource?) {
      this.ds?.close()
      this.ds = ds
   }

   override fun getLogWriter(): PrintWriter {
      return getDs().logWriter
   }

   override fun setLogWriter(out: PrintWriter?) {
      getDs().logWriter = out
   }

   override fun setLoginTimeout(seconds: Int) {
      getDs().loginTimeout = seconds
   }

   override fun getLoginTimeout(): Int {
      return getDs().loginTimeout
   }

   override fun getParentLogger(): Logger {
      return getDs().parentLogger
   }

   override fun <T : Any?> unwrap(iface: Class<T>?): T {
      return getDs().unwrap(iface)
   }

   override fun isWrapperFor(iface: Class<*>?): Boolean {
      return getDs().isWrapperFor(iface)
   }

   override fun getConnection(): Connection {
      return getDs().connection
   }

   override fun getConnection(username: String?, password: String?): Connection {
      return getDs().getConnection(username, password)
   }

}
