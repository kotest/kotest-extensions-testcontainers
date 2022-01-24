package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.kafka.createStringStringConsumer
import io.kotest.extensions.testcontainers.kafka.createStringStringProducer
import io.kotest.matchers.collections.shouldHaveSize
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.testcontainers.containers.KafkaContainer
import java.time.Duration

class KafkaTestContainerExtensionTest : FunSpec() {
   init {

      val kafka = install(TestContainerExtension(KafkaContainer("6.2.1"))) {
         withEmbeddedZookeeper()
         withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
      }

      test("should setup kafka") {

         val producer = kafka.createStringStringProducer()
         producer.send(ProducerRecord("foo", "key", "bubble bobble"))
         producer.flush()
         producer.close()

         val consumer = kafka.createStringStringConsumer {
            this[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
            this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
         }

         consumer.subscribe(listOf("foo"))
         val records = consumer.poll(Duration.ofSeconds(15))
         records.shouldHaveSize(1)
         consumer.close()
      }
   }
}