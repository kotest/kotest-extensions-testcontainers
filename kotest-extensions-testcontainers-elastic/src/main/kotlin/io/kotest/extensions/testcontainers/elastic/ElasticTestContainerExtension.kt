package io.kotest.extensions.testcontainers.elastic

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.testcontainers.elasticsearch.ElasticsearchContainer

class ElasticTestContainerExtension(
   private val container: ElasticsearchContainer,
) : AfterProjectListener,
   MountableExtension<RestClient, ElasticsearchClient> {

   override fun mount(configure: RestClient.() -> Unit): ElasticsearchClient {
      container.start()
      val restClient = RestClient.builder(HttpHost.create(container.httpHostAddress)).build()
      configure(restClient)
      val transport = RestClientTransport(restClient, JacksonJsonpMapper())
      return ElasticsearchClient(transport)
   }
}
