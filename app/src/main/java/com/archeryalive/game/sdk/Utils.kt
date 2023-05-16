package com.archeryalive.game.sdk

import android.app.ActivityManager
import android.content.Context

/**
 * Description:
 *
 * @author: roy
 * Time: 2022/7/12 16:50
 * Modifier:
 * Fix Description:
 * Version:
 */

const val HANDLE_OK = 200
const val HANDLE_ABANDON = 505
const val HANDLE_ERROR_SERVICE_DESTROYED = 404
const val DEFAULT_I = -324211
internal const val SERVICE_HEARTBEATS_CALL_ID = 0XFA98716
internal const val SERVICE_PING = "ping#01"
internal const val SERVICE_PONG = "pong#02"
internal const val SERVICE_INIT = "web_service_init#013"
internal const val SERVICE_DESTROY = "web_service_destroy#027"
internal const val SERVICE_LOG = "service_logs#07"

internal fun getProgressName(context: Context): String? {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager?
    manager?.runningAppProcesses?.forEach {
        if (it.pid == android.os.Process.myPid()) {
            return it.processName
        }
    }
    return ""
}

internal object WebLogUtils {

    private var logIn: ((s: String) -> Unit)? = null

    @Suppress("unused")
    fun setLogIn(logAble: Boolean, logIn: (s: String) -> Unit) {
        if (logAble) WebLogUtils.logIn = logIn else WebLogUtils.logIn = null
    }

    fun log(s: String) {
        logIn?.invoke(s)
    }
}