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
 *
 * @param beforeStart a callback that is invoked only once, just before the container is started.
 * @param afterStart a callback that is invoked only once, just after the container is started.
 * @param beforeShutdown a callback that is invoked only once, just before the containuer is stopped.
 *                       If the container is never started, this callback will not be invoked.
 */
class ContainerExtension<T : GenericContainer<*>>(
   private val container: T,
   private val beforeStart: (T) -> Unit = {},
   private val afterStart: (T) -> Unit = {},
   private val beforeShutdown: (T) -> Unit = {},
) : MountableExtension<T, T>,
   AfterProjectListener {

   /**
    * Mounts the container, starting it if necessary. The [configure] block will be invoked
    * every time the container is mounted, and after the container has started.
    */
   override fun mount(configure: T.() -> Unit): T {
      if (!container.isRunning) {
         beforeStart(container)
         container.start()
         afterStart(container)
      }
      container.configure()
      return container
   }

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) {
         beforeShutdown(container)
         container.stop()
      }
   }
}
