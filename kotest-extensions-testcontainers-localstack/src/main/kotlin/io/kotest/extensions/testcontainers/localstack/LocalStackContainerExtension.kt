package io.kotest.extensions.testcontainers.localstack

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainerExtension(
   image: DockerImageName,
   private val mode: TestContainerLifecycleMode,
) : MountableExtension<LocalStackContainer, LocalStackContainer>,
   BeforeSpecListener,
   AfterSpecListener,
   BeforeTestListener,
   AfterTestListener,
   AfterProjectListener {

   constructor() : this(DockerImageName.parse("localstack/localstack:0.11.3"), TestContainerLifecycleMode.Project)

   constructor(mode: TestContainerLifecycleMode) : this(DockerImageName.parse("localstack/localstack:0.11.3"), mode)

   private val localstack = LocalStackContainer(image)

   override fun mount(configure: LocalStackContainer.() -> Unit): LocalStackContainer {
      localstack.configure()
      if (mode == TestContainerLifecycleMode.Project) localstack.start()
      return localstack
   }

   override suspend fun afterProject() {
      if (mode == TestContainerLifecycleMode.Project) withContext(Dispatchers.IO) { localstack.stop() }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (mode == TestContainerLifecycleMode.Test ||
         (mode == TestContainerLifecycleMode.Leaf && testCase.type == TestType.Test)
      ) withContext(Dispatchers.IO) { localstack.start() }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (mode == TestContainerLifecycleMode.Test ||
         (mode == TestContainerLifecycleMode.Leaf && testCase.type == TestType.Test)
      ) withContext(Dispatchers.IO) { localstack.stop() }
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec) withContext(Dispatchers.IO) { localstack.start() }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec) withContext(Dispatchers.IO) { localstack.stop() }
   }

}
