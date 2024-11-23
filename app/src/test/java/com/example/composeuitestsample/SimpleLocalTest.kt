package com.example.composeuitestsample

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SimpleLocalTest {
    @Test
    fun verify_thread() {
        println(Thread.currentThread())
        println(Thread.currentThread().name) // Test worker
    }

    @Test
    fun verify_coroutine_thread() = runTest {
        println(Thread.currentThread())
        println(Thread.currentThread().name) // test worker(coroutines test)
    }

    @Test
    fun coroutine_test() = runTest {
        launch {
            delay(1000)
            println("coroutine")
        }
        println("test")
        testScheduler.advanceUntilIdle()
    }
}
