package io.kotest.extensions.testcontainers

@Deprecated("To be removed")
enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

/**
 * Determines the lifetime of a test container installed in a Kotest extension.
 */
enum class ContainerLifecycleMode {

   /**
    * The TestContainer is started only when first installed and stopped after the spec where it was
    * installed completes.
    *
    * Use this when you need the test container to shut down as soon as the spec does - usually
    * because you are using a separate test container per spec and waiting until the test suite
    * completes to shut them all down will take too much memory.
    */
   Spec,

   /**
    * The TestContainer is started only when first installed and stopped after the entire test suite.
    * This mode is the default choice for test containers. This mode can be used with
    * multiple test containers by using separate instances of the container extensions.
    */
   Project,
}
