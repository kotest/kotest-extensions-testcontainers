package io.kotest.extensions.testcontainers.localstack

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   private val container: LocalStackContainer,
) : AfterProjectListener, MountableExtension<LocalStackContainer, LocalStackContainer> {

   constructor() : this(LocalStackContainer(DockerImageName.parse("localstack/localstack:0.11.3")))

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) { container.stop() }
   }

   override fun mount(configure: LocalStackContainer.() -> Unit): LocalStackContainer {
      container.configure()
      container.start()
      return container
   }
}
