package org.mampod.kotlin.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

suspend fun main() {
    val user = getUserInfo()
    val friend = getFriend(user)
    val dynamic = getDynamic(friend)
    log("dynamic:$dynamic")
}

// delay(1000L)用于模拟网络请求
suspend fun getUserInfo(): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "user-1"
}

suspend fun getFriend(user: String): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "user-friend-1"
}

suspend fun getDynamic(friend: String): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "user-friend-1-wechat-line"
}
