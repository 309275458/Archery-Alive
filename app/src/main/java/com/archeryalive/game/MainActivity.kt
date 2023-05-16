package com.archeryalive.game

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.archeryalive.game.game.GamePlayerBridge
import com.archeryalive.game.game.GamePlayerDelegate
import com.archeryalive.game.game.GameWebView
import com.archeryalive.game.game.IFrameOptions
import com.archeryalive.game.util.NetUtils
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : BaseActivity() {

    private var adView: AdView? = null
    private var dialog:Dialog? = null
    private var isShow:Int=0;
    /**
     * 游戏页面执行JS具体的回调逻辑在这里处理
     */
    private var gameDelegate = object : GamePlayerDelegate() {
        override fun getWebView(): WebView {
            return webView
        }

        override fun gameLoadingStarted() {
        }

        override fun gameLoadingFinished() {
            super.gameLoadingFinished()
            // 游戏加载完成
            Log.e("Roy", "called gameLoadingFinished")
        }

        override fun gameStart() {
            Log.e("Roy", "called gameStart")
            // 游戏开始
        }

        override fun gameStop() {
            Log.e("Roy", "called gameStop")
        }

        override fun commercialBreak() {
            // 这里是web页面用户暂停游戏时回调
            Log.e("Roy", "called commercialBreak")
            if (isShow==0){
                isShow=1
                return
            }
            showInt()
        }

        override fun rewardedBreak() {
            // 这里由web页面用户点击获取激励奖励等场景
            Log.e("Roy", "called rewardedBreak")
            requestRewardAd()
        }

        override fun gameplayStart() {
            Log.e("Roy", "called gameplayStart")
        }
    }

    //
    private var gamePlayerBridge = GamePlayerBridge(gameDelegate)
    private var gameOptions: IFrameOptions = IFrameOptions.default
    private lateinit var webView: GameWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = GameWebView(this, gamePlayerBridge)
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        val container = findViewById<FrameLayout>(R.id.game_view)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        container.addView(webView, params)
        requestBanner()
    }

    private fun requestBanner() {
        adView = AdView(this)
        adView?.setAdSize(AdSize.BANNER)
        adView?.adUnitId = Constant.bannerId
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
            }
        }
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    private fun requestRewardAd() {
        dialog = NetUtils.showLoadingDialog(this,"")
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this,
            Constant.rewardId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Roy", adError.message)
                    NetUtils.dismissLoadingDialog(dialog)
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d("Roy", "Ad was loaded.")
                    showRewardAd(rewardedAd)
                    NetUtils.dismissLoadingDialog(dialog)
                }
            })
    }

    fun showRewardAd(rewardedAd: RewardedAd) {
        rewardedAd.show(
            this@MainActivity
        ) {
            Log.e("Roy", "ad rewarded")
            webView.loadUrl("javascript:resolve(true)")
        }
    }


    fun showInt() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            Constant.intId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Roy", adError.message)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("Roy", "Ad was loaded.")
                    interstitialAd.show(this@MainActivity)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        initInternalServer()
        adView?.resume()
    }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    private fun initInternalServer() {
        val serverManager = ServerManager(this)
        serverManager.register()
        serverManager.startServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        val serverManager = ServerManager(this)
        serverManager.stopServer()
        adView?.destroy()
    }

    fun onServerStart(ip: String) {
        if (!TextUtils.isEmpty(ip)) {
            gameDelegate.initGamePlayerScript(gameOptions)
            val mRootUrl = "http://${ip}:8085/"
            val gameUrl = "${mRootUrl}/index.html"
            gameDelegate.load(gameUrl)
            Log.e("Roy", "server start success ip: $ip")
        } else {
            Log.e("Roy", "ip error")
        }
    }




}