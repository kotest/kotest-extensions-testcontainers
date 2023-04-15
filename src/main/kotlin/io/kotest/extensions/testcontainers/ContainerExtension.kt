package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer

/**
 * A Kotest [MountableExtension] for [GenericContainer]s that are started the first time they are
 * installed in a spec, and then shared throughout the same test suite. The container is shutdown
 * after all the test suite has completed.
 *
 * If no spec is executed that installs a particular container, then that container is never started.
 */
class ContainerExtension<T : GenericContainer<*>>(
   private val container: T,
   private val lifecycle: ContainerLifecycle<T> = ContainerLifecycle(),
) : MountableExtension<T, T>,
   AfterProjectListener {

   override fun mount(configure: T.() -> Unit): T {
      if (!container.isRunning) {
         lifecycle.beforeStart(container)
         container.start()
         lifecycle.afterStart(container)
      }
      return container
   }

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) { container.stop() }
   }
}

class ContainerLifecycle<T : GenericContainer<*>>(
   val beforeStart: (T) -> Unit = {},
   val afterStart: (T) -> Unit = {},
)
