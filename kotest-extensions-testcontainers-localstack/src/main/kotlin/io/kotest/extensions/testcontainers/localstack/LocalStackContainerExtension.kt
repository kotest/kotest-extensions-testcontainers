package io.kotest.extensions.testcontainers.localstack

import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   container: LocalStackContainer,
   mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
) : AbstractContainerExtension<LocalStackContainer>(container, mode) {

   constructor(
      image: DockerImageName,
      mode: ContainerLifecycleMode = ContainerLifecycleMode.Project
   ) : this(LocalStackContainer(image), mode)
}
