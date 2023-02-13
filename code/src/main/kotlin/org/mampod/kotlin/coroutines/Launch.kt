package org.mampod.kotlin.coroutines

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
suspend fun main() {
    log(1)
    val job = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
        log(2)
        delay(100)
        log(3)
    }
    log(4)
    job.join()
    log(5)
}