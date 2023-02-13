package org.mampod.kotlin.coroutines

import kotlinx.coroutines.*

fun main() = runBlocking {
    GlobalScope.launch(MyContinuationInterceptor()) {
        log(1)
        val job = async() {
            log(2)
            delay(1000)
            log(3)
            "Hello"
        }
        log(4)
        val result = job.await()
        log("5. $result")
    }.join()
    log(6)
}