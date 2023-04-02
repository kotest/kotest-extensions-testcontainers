package io.kotest.extensions.testcontainers.localstack

import io.kotest.core.extensions.MountableExtension
import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   image: DockerImageName,
   mode: TestContainerLifecycleMode,
) : AbstractContainerExtension(mode),
   MountableExtension<LocalStackContainer, LocalStackContainer> {

   constructor() : this(TestContainerLifecycleMode.Project)
   constructor(mode: TestContainerLifecycleMode) : this(DockerImageName.parse("localstack/localstack:0.11.3"), mode)

   private val container = LocalStackContainer(image)

   override fun getContainer() = container

   override fun mount(configure: LocalStackContainer.() -> Unit): LocalStackContainer {
      container.configure()
      super.onMount()
      return container
   }
}
