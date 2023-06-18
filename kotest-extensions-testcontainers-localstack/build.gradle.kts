dependencies {
   api(projects.kotestExtensionsTestcontainers)
   implementation(libs.kotest.framework.api)
   implementation(libs.kotlinx.coroutines.core)
   api("org.testcontainers:localstack:1.18.3")
}
