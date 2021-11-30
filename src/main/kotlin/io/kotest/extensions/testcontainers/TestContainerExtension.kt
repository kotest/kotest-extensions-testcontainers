package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.TestLifecycleAware
import java.util.Optional

class TestContainerExtension<T : GenericContainer<Nothing>>(
   private val container: T,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<T, T>, SpecExtension, TestListener {

   companion object {
      operator fun invoke(
         dockerImageName: String,
         lifecycleMode: LifecycleMode = LifecycleMode.Spec
      ): TestContainerExtension<GenericContainer<Nothing>> {
         return TestContainerExtension(
            GenericContainer<Nothing>(dockerImageName),
            lifecycleMode
         )
      }
   }

   override fun mount(configure: T.() -> Unit): T {
      container.configure()
      return container
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
