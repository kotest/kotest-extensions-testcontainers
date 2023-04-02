package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.extensions.MountableExtension
import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.BytesSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.Properties

class KafkaContainerExtension(
   image: DockerImageName,
   mode: TestContainerLifecycleMode,
) : AbstractContainerExtension(mode),
   MountableExtension<KafkaContainer, Properties> {

   constructor() : this(TestContainerLifecycleMode.Project)
   constructor(mode: TestContainerLifecycleMode) : this(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"), mode)

   private val container = KafkaContainer(image)

   override fun getContainer() = container

   override fun mount(configure: KafkaContainer.() -> Unit): Properties {
      container.start()
      val props = Properties()
      props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = container.bootstrapServers
      props[ConsumerConfig.GROUP_ID_CONFIG] = "kotest_consumer_" + System.currentTimeMillis()
      props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
      props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
      props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
      props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
      props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
      return props
   }
}
