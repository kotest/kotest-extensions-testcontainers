plugins {
   id("kotest-publishing-conventions")
}

dependencies {
   implementation(projects.kotestExtensionsTestcontainers)
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api(libs.testcontainers.core)
   api(libs.testcontainers.elastic)
   api(libs.elastic.client)

   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
   implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
   implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.15.2")

   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)

   testImplementation("ch.qos.logback:logback-classic:1.4.7")
   testImplementation("org.slf4j:slf4j-api:2.0.7")
}
