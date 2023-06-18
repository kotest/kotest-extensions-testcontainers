package io.kotest.extensions.testcontainers.elastic

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

class ElasticsearchContainerExtension(
   private val container: ElasticsearchContainer,
   private val mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project,
) : AfterProjectListener,
   AfterSpecListener,
   MountableExtension<ElasticsearchContainer, ElasticsearchContainer>,
   AutoCloseable {

   constructor(
      image: DockerImageName,
      mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project
   ) : this(ElasticsearchContainer(image), mode)

   override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   override suspend fun afterSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec && container.isRunning) close()
   }

   override fun mount(configure: ElasticsearchContainer.() -> Unit): ElasticsearchContainer {
      container.start()
      return container
   }

   override fun close() {
      container.stop()
   }
}

fun ElasticsearchContainer.client(): ElasticsearchClient {
   val restClient = RestClient.builder(HttpHost.create(httpHostAddress)).build()
   val transport = RestClientTransport(restClient, JacksonJsonpMapper())
   return ElasticsearchClient(transport)
}
