package com.archeryalive.game.game

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import androidx.annotation.RestrictTo


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
open class BaseGamePlayerBridge(private val gamePlayerListener: GamePlayerListener) {

    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper()) {
        return@Handler false
    }

    @JavascriptInterface
    fun gameLoadingStarted() {
        mainThreadHandler.post {
            gamePlayerListener.gameLoadingStarted()
        }
    }

    @JavascriptInterface
    fun gameLoadingFinished() {
        mainThreadHandler.post {
            gamePlayerListener.gameLoadingFinished()
        }
    }

    @JavascriptInterface
    fun gameplayStart() {
        mainThreadHandler.post {
            gamePlayerListener.gameplayStart()
        }
    }

    @JavascriptInterface
    fun gameStart() {
        mainThreadHandler.post {
            gamePlayerListener.gameStart()
        }
    }


    @JavascriptInterface
    fun gameStop() {
        mainThreadHandler.post {
            gamePlayerListener.gameStop()
        }
    }


    @JavascriptInterface
    fun commercialBreak() {
        mainThreadHandler.post {
            gamePlayerListener.commercialBreak()
        }
    }

    @JavascriptInterface
    fun rewardedBreak() {
        mainThreadHandler.post {
            gamePlayerListener.rewardedBreak()
        }
    }


}