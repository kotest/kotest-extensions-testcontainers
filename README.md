# kotest-extensions-testcontainers

Kotest extensions for [TestContainers](https://www.testcontainers.org/).

See [docs](https://kotest.io/docs/extensions/test_containers.html).

Please create issues on the main kotest [board](https://github.com/kotest/kotest/issues).

[![Build Status](https://github.com/kotest/kotest-extensions-testcontainers/workflows/master/badge.svg)](https://github.com/kotest/kotest-extensions-testcontainers/actions)
[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-extensions-testcontainers)
![GitHub](https://img.shields.io/github/license/kotest/kotest-extensions-testcontainers)
[![kotest @ kotlinlang.slack.com](https://img.shields.io/static/v1?label=kotlinlang&message=kotest&color=blue&logo=slack)](https://kotlinlang.slack.com/archives/CT0G9SD7Z)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-testcontainers/)

## Changelog

### 1.3.2

* Updated spec and test callbacks to be suspendable.

### 1.3.1

* Updated test containers to 1.17.0

### 1.3.0

* Added `SharedJdbcDatabaseContainerExtension` and `SharedTestContainerExtension` which can be used to lazily share a single test container across a module.

### 1.2.1

* Improves the handling of `dbInitScripts`. Will now accept absolute or relative paths, for local _or classpath_ resources.

### 1.2.0

* Adds new config option  `dbInitScripts` on the `JdbcTestContainerExtension` config lambda. This option accepts a **list**
of `.sql` files or folders (with .sql files, sorted lexicographically) to run after the container is started.
### 1.1.0

* Requires Kotest 5.0.2 or higher
* Adds `JdbcTestContainerExtension` as a new extension
* Adds `TestContainerExtension` as a new extension

### 1.0.1

* Released for test containers v1.16.0

### 1.0.0

* Migrated from the main Kotest repo to a standalone repo.
