package io.kotest.extensions.testcontainers.localstack

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   image: DockerImageName,
   private val mode: TestContainerLifecycleMode,
) : AbstractContainerExtension(mode),
   MountableExtension<LocalStackContainer, LocalStackContainer>,
   BeforeSpecListener,
   AfterSpecListener,
   BeforeTestListener,
   AfterTestListener,
   AfterProjectListener {

   constructor() : this(DockerImageName.parse("localstack/localstack:0.11.3"), TestContainerLifecycleMode.Project)
   constructor(mode: TestContainerLifecycleMode) : this(DockerImageName.parse("localstack/localstack:0.11.3"), mode)

   private val localstack = LocalStackContainer(image)

   override fun getContainer() = localstack

   override fun mount(configure: LocalStackContainer.() -> Unit): LocalStackContainer {
      localstack.configure()
      super.onMount()
      return localstack
   }
}
