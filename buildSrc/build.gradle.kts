import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
   `kotlin-dsl`
}

kotlin {
   jvmToolchain(11)
   compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
}

repositories {
   mavenCentral()
}
