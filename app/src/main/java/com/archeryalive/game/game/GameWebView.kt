package com.archeryalive.game.game

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebView
import com.archeryalive.game.sdk.BaseWebView

/**
 * Description:
 */
@SuppressLint("ViewConstructor")
class GameWebView(context: Context, private val gamePlayerBridge: GamePlayerBridge) :
    BaseWebView<GamePlayerBridge>(context) {

    override val javaScriptClient: GamePlayerBridge; get() = gamePlayerBridge

    override val webDebugEnable: Boolean; get() = true


    override fun onPageFinished(view: WebView, url: String) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        super.onPageFinished(view, url)
    }
}