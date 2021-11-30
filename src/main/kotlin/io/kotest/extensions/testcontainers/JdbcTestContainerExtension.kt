package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.lifecycle.TestLifecycleAware
import java.util.Optional
import javax.sql.DataSource

/**
 * A Kotest extension for JDBC Test Containers. This extension will create a [DataSource] attached
 * to the database. This datasource will be an instance of [HikariDataSource] for pooling.
 *
 * @param container the specific test container type
 * @param lifecycleMode determines when the container should be restarted -
 * either per spec, per lambda, or per leaf test
 *
 * Note: This extension requires Kotest 5.0+
 */
class JdbcTestContainerExtension(
   private val container: JdbcDatabaseContainer<Nothing>,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<HikariConfig, DataSource>, SpecExtension, TestListener {

   override fun mount(configure: HikariConfig.() -> Unit): DataSource {
      container.start()
      val config = HikariConfig()
      config.jdbcUrl = container.jdbcUrl
      config.username = container.username
      config.password = container.password
      return HikariDataSource(config)
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      if (lifecycleMode == LifecycleMode.Spec) {
         start()
         execute(spec)
         stop()
      }
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (lifecycleMode == LifecycleMode.LeafTest) {
         lifecycleBeforeTest(testCase)
         start()
      }
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (lifecycleMode == LifecycleMode.LeafTest) {
         lifecycleAfterTest(testCase, result)
         stop()
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (lifecycleMode == LifecycleMode.EveryTest) {
         lifecycleBeforeTest(testCase)
         start()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (lifecycleMode == LifecycleMode.EveryTest) {
         lifecycleAfterTest(testCase, result)
         stop()
      }
   }

   private suspend fun start() {
      withContext(Dispatchers.IO) {
         container.start()
      }
   }

   private suspend fun stop() {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }

   private suspend fun lifecycleBeforeTest(testCase: TestCase) {
      when (container) {
         is TestLifecycleAware -> withContext(Dispatchers.IO) {
            container.beforeTest(testCase.toTestDescription())
         }
      }
   }

   private suspend fun lifecycleAfterTest(testCase: TestCase, result: TestResult) {
      when (container) {
         is TestLifecycleAware -> withContext(Dispatchers.IO) {
            container.afterTest(
               testCase.toTestDescription(), Optional.ofNullable(result.errorOrNull)
            )
         }
      }
   }
}

enum class LifecycleMode {
   Spec, EveryTest, LeafTest
}
