package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.TestContainerLifecycleMode
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
   private val mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project,
) : AfterProjectListener,
   AfterSpecListener,
   MountableExtension<KafkaContainer, KafkaContainer>,
   AutoCloseable {

   constructor(
      image: DockerImageName,
      mode: TestContainerLifecycleMode = TestContainerLifecycleMode.Project
   ) : this(KafkaContainer(image), mode)

   override suspend fun afterProject() {
      if (container.isRunning) close()
   }

   override suspend fun afterSpec(spec: Spec) {
      if (mode == TestContainerLifecycleMode.Spec && container.isRunning) close()
   }

   override fun mount(configure: KafkaContainer.() -> Unit): KafkaContainer {
      container.configure()
      container.start()
      return container
   }

   override fun close() {
      container.stop()
   }
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
