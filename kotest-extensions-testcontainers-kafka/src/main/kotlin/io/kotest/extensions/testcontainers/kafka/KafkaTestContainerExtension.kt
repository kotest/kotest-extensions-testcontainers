package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.BytesSerializer
import org.testcontainers.containers.KafkaContainer
import java.util.Properties

class KafkaTestContainerExtension(
   private val container: KafkaContainer,
) : MountableExtension<Properties, Properties>,
   BeforeSpecListener,
   AfterSpecListener {

   override fun mount(configure: Properties.() -> Unit): Properties {
      container.start()
      val props = Properties()
      props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = container.bootstrapServers
      props[ConsumerConfig.GROUP_ID_CONFIG] = "kotest_consumer_" + System.currentTimeMillis()
      props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
      props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
      props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
      props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
      props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
      configure(props)
      return props
   }
}
