package org.mampod.kotlin.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

// 1、DEFAULT
//suspend fun main() {
//    log(1)
//    val job = GlobalScope.launch {
//        log(2)
//    }
//    log(3)
//    job.join()
//    log(4)
//}

// 2、LAZY
//suspend fun main() {
//    log(1)
//    val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
//        log(2)
//    }
//    log(3)
//    job.start()
//    log(4)
//}

// 3、ATOMIC
//suspend fun main() {
//    log(1)
//    val job = GlobalScope.launch(start = CoroutineStart.ATOMIC) {
//        log(2)
//        delay(1000)
//        log(3)
//    }
//    job.cancel()
//    log(4)
//    job.join()
//}

// 4、UNDISPATCHED
//suspend fun main() {
//    log(1)
//    val job = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
//        log(2)
//        delay(100)
//        log(3)
//    }
//    log(4)
//    job.join()
//    log(5)
//}

//fun main() = runBlocking {
//    val job = async {
//        println("start -> ${Thread.currentThread().name}")
//        delay(1000)
//        log(2)
//    }
////    job.await()
//    println("start -> ${Thread.currentThread().name}")
//}

fun main() {
    runBlocking {
        try {
            CoroutineScope(EmptyCoroutineContext).launch {
                throw RuntimeException()
            }.join()
        } catch (e: Exception) { }
    }
}