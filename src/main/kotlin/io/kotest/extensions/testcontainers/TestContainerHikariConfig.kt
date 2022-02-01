package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig

class TestContainerHikariConfig : HikariConfig() {

   var dbInitScripts: List<String> = emptyList()

}
