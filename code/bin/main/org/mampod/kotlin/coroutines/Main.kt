package org.mampod.kotlin.coroutines

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor




fun main() = runBlocking {
    GlobalScope.launch {
        delay(600)
        println("running in ：${Thread.currentThread().name}")
    }.join()
    println("running in ：${Thread.currentThread().name}")
}


/*val job = async(Dispatchers.IO) {
    println("running in ：${Thread.currentThread().name}")
}
job.await()*/


/*   // launch一个新的协程
   GlobalScope.launch {
       delay(2000)
       println("running in ：${Thread.currentThread().name}")
   }.join()
   println("running in 1：${Thread.currentThread().name}")*/

//    println("---")
//    runBlocking {
////        catchTest()
//        kotlin.runCatching {
//            withContext(coroutineContext) {
//                println("111")
//                throw IllegalArgumentException("saff")
//            }
//        }.fold({
//            println("coroutine success")
//        }, {
//            println("coroutine catch")
//            println("${it.stackTraceToString()}")
//        })
//    }


private suspend fun catchTest() {
    try {
        coroutineScope {
            launch {
                kotlin.runCatching {
                    println("111")
                    throw IllegalArgumentException("saff")
                }.fold({
                    println("coroutine success")
                }, {
                    println("coroutine catch")
                    println("${it.stackTraceToString()}")
                })

            }

        }
    } catch (e: Exception) {
        println("main catch")
        println("${e.stackTraceToString()}")

//            e.printStackTrace()
    }
    println("22---")
}