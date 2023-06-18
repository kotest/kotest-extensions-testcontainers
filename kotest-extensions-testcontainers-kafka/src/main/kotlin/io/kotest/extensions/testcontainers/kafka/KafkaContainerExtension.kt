package io.kotest.extensions.testcontainers.kafka

import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
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
   container: KafkaContainer,
   mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
) : AbstractContainerExtension<KafkaContainer>(container, mode) {

   constructor(
      image: DockerImageName,
      mode: ContainerLifecycleMode = ContainerLifecycleMode.Project
   ) : this(KafkaContainer(image), mode)
}

fun KafkaContainer.producer(configure: Properties.() -> Unit = {}): KafkaProducer<Bytes, Bytes> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
   props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = BytesSerializer::class.java
   props.configure()
   return KafkaProducer<Bytes, Bytes>(props)
}

fun KafkaContainer.consumer(configure: Properties.() -> Unit = {}): KafkaConsumer<Bytes, Bytes> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props[ConsumerConfig.GROUP_ID_CONFIG] = "kotest_consumer_" + System.currentTimeMillis()
   props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
   props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
   props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = BytesDeserializer::class.java
   props.configure()
   return KafkaConsumer<Bytes, Bytes>(props)
}
