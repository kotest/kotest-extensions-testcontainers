package io.kotest.extensions.testcontainers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestContainerIntegrationPerSpec : StringSpec({

   val testStartable = TestStartable()
   listener(testStartable.perSpec())

   "start count for first test should be one" {
      testStartable.startCount shouldBe 1
   }

   "start count for second test should also be one" {
      testStartable.startCount shouldBe 1
   }
})
