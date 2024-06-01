import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
   id("kotest-publishing-conventions")
   kotlin("jvm") version "1.6.21"
}

allprojects {
   apply(plugin = "org.jetbrains.kotlin.jvm")

   repositories {
      mavenCentral()
      maven {
         url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
      }
   }

   group = "io.kotest.extensions"
   version = Ci.version

   tasks.named<Test>("test") {
      useJUnitPlatform()
      testLogging {
         showExceptions = true
         showStandardStreams = true
         exceptionFormat = TestExceptionFormat.FULL
      }
   }

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions.jvmTarget = "11"
   }
}

dependencies {
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api(libs.testcontainers.core)
   api(libs.testcontainers.jdbc)
   api(libs.testcontainers.kafka)
   api(libs.testcontainers.elastic)
   api(libs.kafka.client)
   api(libs.hikari)

   testImplementation(libs.logback)
   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
   testImplementation(libs.jedis)
   testImplementation(libs.testcontainers.mysql)
   testImplementation(libs.testcontainers.redis)
   testImplementation(libs.mysql.connector.java)
}
