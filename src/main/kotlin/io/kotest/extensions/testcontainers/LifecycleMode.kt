package io.kotest.extensions.testcontainers

enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

/**
 * Contains when a test container is started and stopped.
 */
enum class TestContainerLifecycleMode {

   /**
    * The TestContainer is started when a spec is entered, and stopped when
    * the spec completes.
    */
   Spec,

   /**
    * The TestContainer is started when a test at any level is entered, and stopped when
    * that test completes. If you are using nested tests, be aware that the container will
    * be started and stopped at each level.
    */
   Test,

   /**
    * The TestContainer is started when a leaf test is entered, and stopped when
    * that test completes. If you are using nested tests, be aware that the container will
    * only be active for leaf tests.
    */
   Leaf,

   /**
    * The TestContainer is started only when first used and stopped at the end of all tests.
    * This mode is the default choice for test containers. This mode can be combined with
    * isolated test containers by using separate instances of the container extensions.
    */
   Project,
}
