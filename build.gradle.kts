import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
   id("kotest-publishing-conventions")
   kotlin("jvm") version "1.7.10"
}

repositories {
   mavenCentral()
   maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
   }
}

group = "io.kotest.extensions"
version = Ci.version

dependencies {
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api(libs.testcontainers.core)
   api(libs.testcontainers.jdbc)
   api(libs.testcontainers.kafka)
   api(libs.testcontainers.elastic)
   api(libs.kafka.client)
   api(libs.hikari)

   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
   testImplementation(libs.jedis)
   testImplementation(libs.testcontainers.mysql)
   testImplementation(libs.mysql.connector.java)
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      exceptionFormat = TestExceptionFormat.FULL
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
   kotlinOptions.jvmTarget = "1.8"
}
