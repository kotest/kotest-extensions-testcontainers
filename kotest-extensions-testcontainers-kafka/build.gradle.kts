plugins {
   id("kotest-publishing-conventions")
}

dependencies {
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api(projects.kotestExtensionsTestcontainers)

   api(libs.testcontainers.core)
   api(libs.testcontainers.kafka)
   api(libs.kafka.client)

   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)

   testImplementation("ch.qos.logback:logback-classic:1.5.12")
   testImplementation("org.slf4j:slf4j-api:2.0.16")
}
