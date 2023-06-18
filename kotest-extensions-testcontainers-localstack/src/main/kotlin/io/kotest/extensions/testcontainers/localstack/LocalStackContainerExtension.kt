package io.kotest.extensions.testcontainers.localstack

import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   container: LocalStackContainer,
   mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project,
) : AbstractContainerExtension<LocalStackContainer>(container, mode) {

   constructor(
      image: DockerImageName,
      mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project
   ) : this(LocalStackContainer(image), mode)
}
