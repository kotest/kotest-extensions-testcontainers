package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import redis.clients.jedis.Jedis

class GenericContainerTest : FunSpec() {
   init {

      val container = install(TestContainerExtension("redis:5.0.3-alpine")) {
         startupAttempts = 2
         exposedPorts = listOf(6379)
      }

      test("read from redis") {
         val jedis = Jedis(container.host, container.firstMappedPort)
         jedis.set("foo", "bar")
         jedis.get("foo") shouldBe "bar"
      }
   }
}
