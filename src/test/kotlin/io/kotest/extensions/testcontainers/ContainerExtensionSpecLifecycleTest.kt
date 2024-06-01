package io.kotest.extensions.testcontainers

import com.redis.testcontainers.RedisContainer
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import redis.clients.jedis.JedisPool

class ContainerExtensionSpecLifecycleTest : FunSpec() {
   init {

      val container = install(ContainerExtension(RedisContainer("7.2.5-alpine"), ContainerLifecycleMode.Spec)) {
         startupAttempts = 2
         withExposedPorts(6379)
      }

      val jedis = JedisPool(container.host, container.firstMappedPort)

      test("should be initialized in the spec") {
         jedis.resource.set("foo", "bar")
         jedis.resource.get("foo") shouldBe "bar"
      }

      test("this test should share the container") {
         jedis.resource.get("foo") shouldBe "bar"
      }
   }
}
