rootProject.name = "kotest-extensions-testcontainers"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

include(
   ":kotest-extensions-testcontainers-kafka",
   ":kotest-extensions-testcontainers-elastic",
)

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
