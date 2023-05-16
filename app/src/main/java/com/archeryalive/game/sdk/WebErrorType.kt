package com.archeryalive.game.sdk

import android.net.http.SslError
import android.webkit.WebResourceError
import android.webkit.WebResourceResponse

/**
 * Description:
 *
 * @author: roy
 * Time: 2022/7/12 16:54
 * Modifier:
 * Fix Description:
 * Version:
 */
enum class WebErrorType {

    HTTP_ERROR, SSL_ERROR, RESOURCE_ERROR, THROW;

    var httpError: WebResourceResponse? = null
    var sslError: SslError? = null
    var resourceError: WebResourceError? = null
    var throws: Throwable? = null

    internal fun onHttpError(httpError: WebResourceResponse?): WebErrorType {
        this.httpError = httpError
        return this
    }

    internal fun onSSLError(sslError: SslError?): WebErrorType {
        this.sslError = sslError
        return this
    }

    internal fun onResourceError(resourceError: WebResourceError?): WebErrorType {
        this.resourceError = resourceError
        return this
    }

    internal fun throwError(e: Throwable?): WebErrorType {
        this.throws = e
        return this
    }
}