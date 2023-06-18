package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import org.testcontainers.containers.GenericContainer

abstract class AbstractContainerExtension<T : GenericContainer<T>>(
   private val container: T,
   private val mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project,
) : AfterProjectListener,
   AfterSpecListener,
   MountableExtension<T, T>,
   AutoCloseable {

   override fun mount(configure: T.() -> Unit): T {
      container.start()
      container.configure()
      return container
   }

   final override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   final override suspend fun afterSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec && container.isRunning) close()
   }

   final override fun close() {
      container.stop()
   }
}
