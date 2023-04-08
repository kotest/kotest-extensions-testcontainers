package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.BytesSerializer
import org.apache.kafka.common.utils.Bytes
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.Properties

class KafkaContainerExtension(
   private val container: KafkaContainer,
) : AfterProjectListener,
   MountableExtension<KafkaContainer, KafkaContainer> {

   constructor() : this(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3")))

   override suspend fun afterProject() {
      if (container.isRunning) withContext(Dispatchers.IO) { container.stop() }
   }

   override fun mount(configure: KafkaContainer.() -> Unit): KafkaContainer {
      container.configure()
      container.start()
      return container
   }
}

fun KafkaContainer.producer(): KafkaProducer<Bytes, Bytes> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
   props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
   return KafkaProducer<Bytes, Bytes>(props)
}


fun KafkaContainer.consumer(consumerGroupId: String? = null): KafkaConsumer<Bytes, Bytes> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroupId ?: ("kotest_consumer_" + System.currentTimeMillis())
   props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
   props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
   props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
   return KafkaConsumer<Bytes, Bytes>(props)
}
