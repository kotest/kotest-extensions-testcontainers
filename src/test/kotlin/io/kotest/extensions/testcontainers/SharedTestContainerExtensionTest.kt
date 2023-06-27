package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.JedisPool

private val container = GenericContainer("redis:5.0.3-alpine").apply {
   startupAttempts = 2
   withExposedPorts(6379)
}

private val ext = ContainerExtension(container)

class SharedTestContainerExtensionTest1 : FunSpec() {
   init {

      val installed = install(ext)
      val jedis = JedisPool(installed.host, installed.firstMappedPort)


      test("should be initialized in the spec") {
         jedis.resource.set("foo", "bar")
         jedis.resource.get("foo") shouldBe "bar"
      }

      test("this test should share the container") {
         jedis.resource.get("foo") shouldBe "bar"
      }
   }
}

class SharedTestContainerExtensionTest2 : FunSpec() {
   init {

      val installed = install(ext)
      val jedis = JedisPool(installed.host, installed.firstMappedPort)

      test("this spec should share the container") {
         jedis.resource.get("foo") shouldBe "bar"
      }
   }
}
