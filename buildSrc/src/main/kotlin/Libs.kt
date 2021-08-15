object Libs {

   const val kotlinVersion = "1.4.32"
   const val org = "io.kotest.extensions"

   object Kotest {
      private const val version = "4.4.3"
      const val Junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
      const val Assertions = "io.kotest:kotest-assertions-core:$version"
      const val Api = "io.kotest:kotest-framework-api:$version"
   }

   object TestContainers {
      private const val version = "1.16.0"
      const val testcontainers = "org.testcontainers:testcontainers:$version"
   }

   object Coroutines {
      private const val version = "1.4.3"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
