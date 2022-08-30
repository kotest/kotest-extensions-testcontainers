package io.kotest.extensions.testcontainers.elastic

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.http.HttpHost
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.testcontainers.elasticsearch.ElasticsearchContainer

class ElasticTestContainerExtension(
   private val container: ElasticsearchContainer
) : MountableExtension<ElasticsearchClient, ElasticsearchClient>,
   BeforeSpecListener,
   AfterSpecListener {

   override fun mount(configure: ElasticsearchClient.() -> Unit): ElasticsearchClient {
      container.start()
      val credentialsProvider = BasicCredentialsProvider()

//      credentialsProvider.setCredentials(
//         AuthScope.ANY,
//         UsernamePasswordCredentials("admin", ElasticsearchContainer.ELASTICSEARCH_DEFAULT_PASSWORD)
//      );

      val restClient = RestClient.builder(HttpHost.create(container.httpHostAddress))
//         .setHttpClientConfigCallback { httpClientBuilder ->
//         httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
      .build()

      val transport = RestClientTransport(restClient, JacksonJsonpMapper())
      val client = ElasticsearchClient(transport)

      configure(client)
      return client
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }
}
