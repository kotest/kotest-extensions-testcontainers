package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

abstract class AbstractContainerExtension(private val mode: TestContainerLifecycleMode) : BeforeSpecListener,
   AfterSpecListener,
   BeforeTestListener,
   AfterTestListener,
   AfterProjectListener {

   abstract fun getContainer(): GenericContainer<*>

   protected fun onMount() {
      if (mode == TestContainerLifecycleMode.Project) getContainer().start()
   }

   override suspend fun afterProject() {
      if (mode == TestContainerLifecycleMode.Project && getContainer().isRunning)
         withContext(Dispatchers.IO) { getContainer().stop() }
   }

   private fun isTestCase(testCase: TestCase) =
      mode == TestContainerLifecycleMode.Test ||
         (mode == TestContainerLifecycleMode.Leaf && testCase.type == TestType.Test)

   override suspend fun beforeAny(testCase: TestCase) {
      if (isTestCase(testCase))
         withContext(Dispatchers.IO) { getContainer().start() }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (isTestCase(testCase) && getContainer().isRunning)
         withContext(Dispatchers.IO) { getContainer().stop() }
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec)
         withContext(Dispatchers.IO) { getContainer().start() }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec && getContainer().isRunning)
         withContext(Dispatchers.IO) { getContainer().stop() }
   }
}
