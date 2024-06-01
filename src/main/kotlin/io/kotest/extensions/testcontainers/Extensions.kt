package io.kotest.extensions.testcontainers

import org.testcontainers.lifecycle.Startable

fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener(this)

fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener(this)

fun <T : Startable> T.perProject(): StartablePerProjectListener<T> = StartablePerProjectListener(this)
