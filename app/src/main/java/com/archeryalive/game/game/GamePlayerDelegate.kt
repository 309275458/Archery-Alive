package com.archeryalive.game.game

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.CallSuper
import com.archeryalive.game.R


abstract class GamePlayerDelegate() : GamePlayerListener {
    var curPath: String = ""

    /**
     * 游戏页面是否加载完毕
     */
    var isPageReady = false
    var inLoading = false

    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper()) {
        return@Handler false
    }


    abstract fun getWebView(): WebView?


    fun initGamePlayerScript(playerOptions: IFrameOptions) {
        val context = getWebView()?.context ?: return
        val htmlPage =
            Utils.readHTMLFromUTF8File(context.resources.openRawResource(R.raw.game_player_bridge))
                .replace("<<injectedPlayerVars>>", playerOptions.toString())
        Utils.log(htmlPage)

        runWithWebView {
            it.settings.mediaPlaybackRequiresUserGesture = false
            it.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            // 匹配url 注入html 文件
            it.loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)
        }

    }


    @CallSuper
    override fun gameLoadingFinished() {
        this.isPageReady = true
    }



    fun load(url: String) {
        inLoading = true
        runWithWebView {
            this.curPath = url
            it.loadUrl(url)
        }
    }


    fun stop() {
        curPath = ""
        getWebView()?.visibility = View.INVISIBLE
        if (!isPageReady) Utils.log("call stop error ! please wait to page ready!")
    }


    fun destroy() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        runWithWebView {
//            playerState = PlayerConstants.PlayerState.STOP
            it.clearHistory()
            it.clearFormData()
            it.removeAllViews()
            (it.parent as? ViewGroup)?.removeView(it)
            it.destroy()
        }
        curPath = ""
    }

    private fun runWithWebView(func: (WebView) -> Unit) {
        try {
            mainThreadHandler.post {
                getWebView()?.let {
                    func(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}