package org.mampod.kotlin.coroutines

import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.EmptyCoroutineContext

val handler = CoroutineExceptionHandler { _, it -> log("handler exception happen:$it") }
val handler1 = CoroutineExceptionHandler { _, it -> log("handler1 exception happen:$it") }
val handler2 = CoroutineExceptionHandler { _, it -> log("handler2 exception happen:$it") }
val handler3 = CoroutineExceptionHandler { _, it -> log("handler3 exception happen:$it") }
suspend fun main() {
    // 可以catch得住吗？
    val scope = CoroutineScope(EmptyCoroutineContext)
    try {
        scope.launch {
            throw RuntimeException()
        }.join()
    } catch (e: Exception) {
        log("catch exception :$e")
    }

    /*-------例1---------*/
//    Thread.setDefaultUncaughtExceptionHandler { thread, throwable -> log("java exception happen:${throwable}") }
//    val scope = CoroutineScope(EmptyCoroutineContext + handler2)
//    scope.launch(/*handler1*/) {
//        launch(handler) {
//            delay(10)
//            log(1)
//            throw RuntimeException()
//        }
//        launch() {
//            delay(100)
//            log("无论怎样，我都被取消了😭")
//        }
//    }.join()
//    log(2)

    /* 结论：
    *  1、由于协程的异常传播路径为为 handler1（根协程）> handler2(根协程域) > Java处理
    *  2、由于异常产生，其他子协程和兄弟协程都被取消了
    * */

    /*-------例2--------*/
//    val scope = CoroutineScope(handler3 + SupervisorJob()/*3*/)/*根协程域*/
//    val job1 = scope.launch(handler2 /*+ SupervisorJob()*//*2*/) {/*根协程*/
//        launch(handler1 /*+ SupervisorJob()*//*1*/) {
//            launch(handler/* + SupervisorJob()*//*0*/) {
//                delay(10)
//                throw RuntimeException()
//            }
//            launch() {
//                delay(100)
//                log("正常执行2,我不会受到影响")
//            }
//        }
//        launch() {
//            delay(200)
//            log("正常执行3,我不会受到影响")
//        }
//    }
//    val job3 = scope.launch(CoroutineName("B")/*3*/) {
//        delay(300)
//        log("正常执行4,我不会受到影响")
//    }
//    joinAll(job1, job3)

    /*
    *  如上，0的协程里将抛出异常：
    *       1、如果0、1、2、3处都不加SupervisorJob，则异常会交由根协程处理。整个协程域都的协程都被取消。
    *       2、给0处加了SupervisorJob，交由自己handler处理，正常执行2、3、4
    *       3、给1处加了SupervisorJob，交由自己handler1处理，正常执行3、4。 0的兄弟协程1被取消。
    *       4、给2处加了SupervisorJob，交由自己handler2处理，正常执行4。    2下的子协程都被取消。4不受影响。
    *       5、给3处加了SupervisorJob，交由自己handler2处理，正常执行4。    2下的子协程都被取消。4不受影响。
    *       6、2、3处都不加SupervisorJob，2下的子协程、4的兄弟协程都被取消
    *
    *  结论：
    *       1、发生异常时，CoroutineExceptionHandler的处理优先级是，向上传播，SupervisorJob -> 根协程 -> 根协程域
    *       2、普通Job，会导致整个协程域的协程都被取消。SupervisorJob会限制在其内的子协程，不会影响兄弟协程。
    *       3、如果在初始化 scope 时添加了 SupervisorJob ,那么整个scope对应的所有 根协程 都将默认携带 SupervisorJob，对应4和5的情况。
    * */


}