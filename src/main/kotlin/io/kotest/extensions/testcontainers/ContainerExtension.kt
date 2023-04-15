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
class ContainerExtension<T : GenericContainer<*>, U>(
   private val container: T,
   private val lifecycle: ContainerLifecycle<T> = ContainerLifecycle(),
   private val mapper: T.() -> U,
) : MountableExtension<T, U>,
   AfterProjectListener {

   companion object {
      operator fun <T : GenericContainer<*>> invoke(
         container: T,
         lifecycle: ContainerLifecycle<T> = ContainerLifecycle(),
      ) = ContainerExtension<T, T>(container, lifecycle) { this }
   }

   override fun mount(configure: T.() -> Unit): U {
      if (!container.isRunning) {
         lifecycle.beforeStart(container)
         container.configure()
         container.start()
         lifecycle.afterStart(container)
      }
      return container.mapper()
   }

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) { container.stop() }
   }
}

class ContainerLifecycle<T : GenericContainer<*>>(
   val beforeStart: (T) -> Unit = {},
   val afterStart: (T) -> Unit = {},
)
