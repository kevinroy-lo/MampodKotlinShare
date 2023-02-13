---
theme: seriph
background: https://source.unsplash.com/collection/94734566/1920x1080
class: text-center
highlighter: shiki
lineNumbers: false
info: |
  ## Slidev Starter Template
  Presentation slides for developers.

  Learn more at [Sli.dev](https://sli.dev)
drawings:
  persist: false
transition: slide-left
css: unocss
title: 玩转Kotlin协程
---

# 玩转Kotlin协程


课件：[PPT](https://github.com/kevinroy-lo/MampodKotlinShare/tree/main/kotlin-coroutines)   &   [Code](https://github.com/kevinroy-lo/MampodKotlinShare/tree/main/code)

分享人：罗广荣

<div class="pt-12">
  <span @click="$slidev.nav.next" class="px-2 py-1 rounded cursor-pointer" hover="bg-white bg-opacity-10">
    Next <carbon:arrow-right class="inline"/>
  </span>
</div>

<div class="abs-br m-6 flex gap-2">
  <a href="https://github.com/kevinroy-lo/MampodKotlinShare" target="_blank" alt="GitHub"
    class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon-logo-github />
  </a>
</div>

<!--
大家好我是深圳产研部门Android的罗广荣

compose分享变成 -> kotlin协程
原因：
  - Compose受众太小，只是Android客户端的一个UI框架
  - Koltin成为Android开发的官方语言，协程也是现如今开发Android主流框架
- 以前也有不太懂的地方，比如协程异常。探讨交流。

PPT 及 示例 以及 示例代码，可以在https://github.com/kevinroy-lo/MampodKotlinShare/ 下载。
-->

---
transition: fade-out
---

# 一、快速入门
### 协程是什么？
<br>

> wiki：Coroutines are computer program components that allow execution to be suspended and resumed, generalizing subroutines for cooperative multitasking. Coroutines are well-suited for implementing familiar program components such as cooperative tasks, exceptions, event loops, iterators, infinite lists and pipes.

<br>

>协同程序是允许暂停和恢复执行的计算机程序组件，概括了用于协作多任务处理的子程序。 协程非常适合实现熟悉的程序组件，例如协作任务、异常、事件循环、迭代器、无限列表和管道。

<br/>
简单来说：

- 协程是一种**非抢占式或者说协作式**的计算机程序并发调度的实现，程序可以主动挂起或者恢复执行。


<!-- - 📝 **Text-based** - focus on the content with Markdown, and then style them later
- 🎨 **Themable** - theme can be shared and used with npm packages
- 🧑‍💻 **Developer Friendly** - code highlighting, live coding with autocompletion
- 🤹 **Interactive** - embedding Vue components to enhance your expressions
- 🎥 **Recording** - built-in recording and camera view
- 📤 **Portable** - export into PDF, PNGs, or even a hostable SPA
- 🛠 **Hackable** - anything possible on a webpage -->

<br>
<br>

Read more about [Kotlin Coroutine?](https://en.wikipedia.org/wiki/Coroutine)

<!--
You can have `style` tag in markdown to override the style for the current page.
Learn more: https://sli.dev/guide/syntax#embedded-styles
-->

<style>
h1 {
  background-color: #2B90B6;
  background-image: linear-gradient(45deg, #4EC5D4 10%, #146b8c 20%);
  background-size: 100%;
  -webkit-background-clip: text;
  -moz-background-clip: text;
  -webkit-text-fill-color: transparent;
  -moz-text-fill-color: transparent;
}
</style>

<!--
首先看一下协程的定义
- 非抢占式，线程受系统的调度，争抢CPU的运行时间
- 协作式，得益于结构化并发，同一件事情，可以不线程中执行，最后在工作线程中恢复
-->

---
transition: slide-up
---
### 协程的启动方式(1)
1、runBlocking{} (runBlocking启动的协程任务会阻断当前线程，直到该协程执行结束)
<v-click>

```kotlin {2|all}
fun main() {
    runBlocking {
        println("running in ：${Thread.currentThread().name}")
        delay(1000)
    }
    println("running in ：${Thread.currentThread().name}")
}
// running in ：main
// running in ：main
```
</v-click>

2、GlobalScope.launch{}
<v-after>

```kotlin {1|2|all}
fun main() = runBlocking {
    GlobalScope.launch {
        delay(600)
        println("running in ：${Thread.currentThread().name}")
    }.join()
    println("running in ：${Thread.currentThread().name}")
}
// running in ：DefaultDispatcher-worker-1
// running in ：main
```
</v-after>


<!-- 
runBlocking 没有默认的调度器，连接Kotlin与协程世界

常用作用域
MainScope
lifecycleScope
viewModelScope
 -->

---
transition: slide-up
---
### 协程的启动方式(2)

3、async/await

<v-after>

```kotlin {2|all}
fun main() = runBlocking {
    val job = async {
        println("start -> ${Thread.currentThread().name}")
    }
    job.await()
    println("start -> ${Thread.currentThread().name}")
}
// start -> main
// start -> main
```
</v-after>
---

### 协程启动模式
<br>

启动协程需要三样东西，分别是 **上下文、启动模式、协程体**，以launch为例，介绍一下协程的启动模式


```kotlin {3}
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT, // 启动模式
    block: suspend CoroutineScope.() -> Unit
): Job
```
<br>

- 🎨 **DEFAULT** - *立即执行协程体*
- 🧑 **LAZY**    -  *只有在有必要的情况下才执行协程体*
- 🤹 **ATOMIC**  - *立即执行协程体，但在开始运行之前无法取消*
- 🎥 **UNDISPATCHED** - *立即在当前线程执行协程体，直到第一个suspend调用*

<!--
平常使用时，直接定义协程体，其实还有其他的两个参数
1、 3、 4 比较容易搞混
-->

---

### 协程启动模式 - DEFAULT
<br>
```kotlin
suspend fun main() {
    log(1)
    val job = GlobalScope.launch {
        log(2)
    }
    log(3)
    job.join()
    log(4)
}
// 14:18:05:453 [main] 1
// 14:18:05:467 [main] 3
// 14:18:05:467 [DefaultDispatcher-worker-1] 2
// 14:18:05:471 [main] 4
```

- 由前面我们知道，默认的模式为DEFAULT，在JVM后台，有专门的线程池去执行任务
- 2 和 3 的输出先后顺序是不确定的

---

### 协程启动模式 - LAZY

> LAZY 是懒汉式启动，launch 后并不会有任何调度行为，协程体也不会进入执行状态，直到我们需要它执行的时候。
```kotlin
suspend fun main() {
    log(1)
    val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
        log(2)
    }
    log(3)
    job.start()
    log(4)
}
// 14:56:28:374 [main] 1
// 14:56:28:493 [main] 3
// 14:56:28:511 [main] 4
// 14:56:28:516 [DefaultDispatcher-worker-1] 2

```

对于LAZY，有2种方法可以触发协程体执行
- 调用 Job.start，主动触发协程的调度执行
  - 例子中，1，3的顺序是确定的，**2，4将不确定**
- 调用 Job.join，隐式的触发协程的调度执行
  - **2，4是确定的**，join方法会挂起当前协程，直到job完成

---

### 协程启动模式 - ATOMIC

> ATOMIC 只有涉及 cancel 的时候才有意义,在到达第一个挂起点前，不会检查是否取消
```kotlin
suspend fun main() {
    log(1)
    val job = GlobalScope.launch(start = CoroutineStart.ATOMIC) {
        log(2)  
        delay(1000)
        log(3)
    }
    job.cancel()
    log(4)
    job.join()
}
```
<br/>
注意：

- 在DEFAULT下，由于cancel，可能协程被取消不会打印。输出：1、（2）、4
- 但在ATOMIC模式下，一定会执行。输出：1、2、4（**non-cancellable way**）

---

### 协程启动模式 - UNDISPATCHED

> 协程在这种模式下会直接开始**在当前线程下执行，直到第一个挂起点**，这听起来有点儿像前面的 ATOMIC，不同之**处在于 UNDISPATCHED 不经过任何调度器即开始执行协程体**。
```kotlin
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
// 14:57:53:331 [main] 1
// 14:57:53:361 [main] 2
// 14:57:53:369 [main] 4
// 14:57:53:484 [DefaultDispatcher-worker-1] 3
// 14:57:53:486 [DefaultDispatcher-worker-1] 5
```
<br/>

<p class="think">想一想，在job执行完后，3和5的打印为什么在同一线程中？</p>

<style>
.think{
  font-size:22px;
  color:red;
}
</style>


---
layout: two-cols
---

## 解惑
<p class="think">3和5的打印为什么在同一线程中？</p>

```kotlin
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
// 14:57:53:331 [main] 1
// 14:57:53:361 [main] 2
// 14:57:53:369 [main] 4
// 14:57:53:484 [DefaultDispatcher-worker-1] 3
// 14:57:53:486 [DefaultDispatcher-worker-1] 5
```

::right::


## RunSuspend

<p  class="think" >1</p>

```kotlin
private class RunSuspend : Continuation<Unit> {
    override val context: CoroutineContext get() = EmptyCoroutineContext
    var result: Result<Unit>? = null
    override fun resumeWith(result: Result<Unit>) = synchronized(this) {
        this.result = result
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") (this as Object).notifyAll()
    }
    // ...
} 
```

**由于runBlocking这种启动方式，并没有设置调度器，会在最后调试的线程中恢复过来**



<style>
.think{
  font-size:22px;
  color:red;
}
</style>



---

## 协程调度器（Dispatchers）

<br>

> 上下文的子类，同时实现了拦截器的接口， dispatch 方法会在拦截器的方法 interceptContinuation 中调用，进而实现协程的调度。

- 🎈Default       --  **线程池**
- 🎄Main          --  **UI线程（Android）**
- 🎭Unconfined    --  **直接执行(在启动协程中执行)**
- ✨IO            --  **线程池**

<br>

IO 仅在 Jvm 上有定义，它基于 Default 调度器背后的线程池，并实现了独立的队列和限制，因此协程调度器从 Default 切换到 IO 并不会触发线程切换。


---
layout: two-cols
---
## 调度节点

```kotlin
suspend fun main() {
    GlobalScope.launch(MyContinuationInterceptor()) {
        log(1)
        val job = async {
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
// 15:31:55:989 [main] <MyContinuation> Success(kotlin.Unit)  // ①
// 15:31:55:992 [main] 1
// 15:31:56:000 [main] <MyContinuation> Success(kotlin.Unit) // ②
// 15:31:56:000 [main] 2
// 15:31:56:031 [main] 4
// 15:31:57:029 [kotlinx.coroutines.DefaultExecutor] <MyContinuation> Success(kotlin.Unit) // ③
// 15:31:57:029 [kotlinx.coroutines.DefaultExecutor] 3
// 15:31:57:031 [kotlinx.coroutines.DefaultExecutor] <MyContinuation> Success(Hello) // ④
// 15:31:57:031 [kotlinx.coroutines.DefaultExecutor] 5. Hello
// 15:31:57:031 [kotlinx.coroutines.DefaultExecutor] 6
```

::right::

<br>

**右边的例子中，调度器回调了4次**
- 1、刚启动时，会被调度一次,launch、async.
- 2、如上
- 3、delay 是挂起点，1000ms 之后需要继续调度执行该协程，调度器调度到这个线程
- 4、返回结果时

<br>

**🧐思考：**

- 这里要注意的一个点是，2为什么在4的前面输出？
- 如果给 async 指定了调度器，又会是什么情形？

Read more about [Kotlin Dispatchers?](https://www.bennyhuo.com/2019/04/11/coroutine-dispatchers/)


---

# 示例其他代码


```kotlin

val dateFormat = SimpleDateFormat("HH:mm:ss:SSS")
val now = {
    dateFormat.format(Date(System.currentTimeMillis()))
}
fun log(msg: Any?) = println("${now()} [${Thread.currentThread().name}] $msg")

class MyContinuationInterceptor: ContinuationInterceptor {
    override val key = ContinuationInterceptor
    override fun <T> interceptContinuation(continuation: Continuation<T>) = MyContinuation(continuation)
}

class MyContinuation<T>(val continuation: Continuation<T>): Continuation<T> {
    override val context = continuation.context
    override fun resumeWith(result: Result<T>) {
        log("<MyContinuation> $result" )
        continuation.resumeWith(result)
    }
}

```


---

## 协程的异常(Exception)

**结构化并发：**
>每个并发操作其实都是在处理一个单独的任务，这个 任务 中，可能还存在 子任务 ; 同样对于这个子任务来说，它又是其父任务的子单元。每个任务都有自己的生命周期，子任务的生命周期会继承父任务的生命周期，比如如果父任务关闭，子任务也会被取消。而如果满足这样特性，我们就称其就是 **结构化并发。**

<br>

**异常传播形式**

在协程中，异常的传播形式有两种
- 🎈一种是自动传播( launch 或 actor)，传递过程是层层向上传递(如果异常没有被捕获)
- 🎍一种是向用户暴漏该异常( async 或 produce )，将不会向上传递，会在调用处直接暴漏




---

## 协程的异常(Exception) 1


**异常处理方式**

在协程中，异常的处理方式两种
- 🎈tryCatch
- 🎍CoroutineExceptionHandler

**使用：**

```kotlin
val handler = CoroutineExceptionHandler { _, it -> log("handler exception happen:$it") }
scope.launch(handler1){}
```

**🧐思考** 可以try-catch住吗？
```kotlin
fun main() {
    runBlocking {
        try {
            CoroutineScope(EmptyCoroutineContext).launch {
                throw RuntimeException()
            }.join()
        } catch (e: Exception) { }
    }
}
```

<!--
开胃小菜，也是最容易犯错的。这里是catch不住的。
并且报Exception in thread "DefaultDispatcher-worker-1"
-->

---

## 异常 - 自动传播

<br/>

```kotlin
Thread.setDefaultUncaughtExceptionHandler { thread, throwable -> log("java exception happen:${throwable}") }
val scope = CoroutineScope(EmptyCoroutineContext + handler2)
scope.launch(/*handler1*/) {
    launch(handler) {
        delay(10)
        log(1)
        throw RuntimeException()
    }
    launch() {
        delay(100)
        log("无论怎样，我都被取消了😭")
    }
}.join()
log(2)
```

**结论：**

- 由于协程的异常传播路径为为 handler1（根协程）> handler2(根协程域) > Java处理
- 由于异常产生，其他子协程和兄弟协程都被取消了

<!--
前面我们知道CoroutineExceptionHandler 来捕获异常，因为向上传播的属性， handler 始终不会被调用到

 由于发生异常，导致协程域内的子协程都被取消
-->

---
layout: two-cols
---

##  异常- SupervisorJob vs Job


```kotlin
val scope = CoroutineScope(handler3 + SupervisorJob()/*3*/)/*根协程域*/
val job1 = scope.launch(handler2 /*+ SupervisorJob()*//*2*/) {/*根协程*/
    launch(handler1 /*+ SupervisorJob()*//*1*/) {
        launch(handler/* + SupervisorJob()*//*0*/) {
            delay(10)
            throw RuntimeException()
        }
        launch() {
            delay(100)
            log("正常执行2,我不会受到影响")
        }
    }
    launch() {
        delay(200)
        log("正常执行3,我不会受到影响")
    }
}
val job3 = scope.launch(CoroutineName("B")/*3*/) {
    delay(300)
    log("正常执行4,我不会受到影响")
}
joinAll(job1, job3)
```

::right::

<br/>

  **如上，0的协程里将抛出异常：**



- 🎄发生异常时，CoroutineExceptionHandler的处理优先级是，向上传播，SupervisorJob -> 根协程 -> 根协程域
- 🎋普通Job，会导致整个协程域的协程都被取消。SupervisorJob会限制在其内的子协程，不会影响兄弟协程。
- 🎍如果在初始化 scope 时添加了 SupervisorJob ,那么整个scope对应的所有 根协程 都将默认携带 SupervisorJob，对应4和5的情况。


<!-- 1、如果0、1、2、3处都不加SupervisorJob，则异常会交由根协程处理。整个协程域都的协程都被取消。

2、给0处加了SupervisorJob，交由自己handler处理，正常执行2、3、4

3、给1处加了SupervisorJob，交由自己handler1处理，正常执行3、4。 0的兄弟协程1被取消。

4、给2处加了SupervisorJob，交由自己handler2处理，正常执行4。    2下的子协程都被取消。4不受影响。

5、给3处加了SupervisorJob，交由自己handler2处理，正常执行4。    2下的子协程都被取消。4不受影响。

6、2、3处都不加SupervisorJob，2下的子协程、4的兄弟协程都被取消 -->


---


## 线程 与 协程



<img src="http://www.enmalvi.com/wp-content/uploads/2021/03/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210326150207.gif"  width="500" height="600"/>


**🎅小结：**

<v-click>

 - 通过(Thread.java)创建的线程，**本质还是操作系统内核线程的映射**
</v-click>

<v-after>

 - 一个线程上可以运行成千上万个协程，**协程是用户态的(userlevel)，内核对协程「无感知」**
</v-after>

<v-after>

 - 本质还**是运行于线程之上**，它通过协程调度器，可以运行到不同的线程上，**底层基于状态机实现，**多协程之间共用一个实例，资源开销极小，因此它更加「轻量」
</v-after>



---
layout: center
class: text-center
--- 

# 感谢观看 
### 更多

推荐阅读：
- [字节小站Kotlin系列](https://juejin.cn/column/7044804359779155999)
- [破解 Kotlin 协程](https://www.bennyhuo.com/book/kotlin-coroutines/00-forword.html)
