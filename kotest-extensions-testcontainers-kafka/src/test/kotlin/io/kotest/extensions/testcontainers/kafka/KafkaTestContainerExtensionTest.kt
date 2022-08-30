package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration

class KafkaTestContainerExtensionTest : FunSpec({

   val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))
   val props = install(KafkaTestContainerExtension(container)) {
      this[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = "1"
   }

   test("happy path") {

      val producer = KafkaProducer<Bytes, Bytes>(props)
      val consumer = KafkaConsumer<Bytes, Bytes>(props)

      producer.send(ProducerRecord("mytopic", Bytes("hello world".encodeToByteArray())))
      producer.close()

      consumer.subscribe(listOf("mytopic"))
      consumer.poll(Duration.ofSeconds(10)).toList().single().value().get().decodeToString() shouldBe "hello world"
   }

})
