plugins {
   id("kotest-publishing-conventions")
}

dependencies {
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api(libs.testcontainers.core)
   api(libs.testcontainers.elastic)
   api(libs.elastic.client)

   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
   implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")
   implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.13.4")

   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)

   testImplementation("ch.qos.logback:logback-classic:1.4.1")
   testImplementation("org.slf4j:slf4j-api:2.0.0")
}
