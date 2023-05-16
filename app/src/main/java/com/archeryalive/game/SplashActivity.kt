package com.archeryalive.game

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView


class SplashActivity : BaseActivity() {
    private val progressBg: ImageView by lazy { findViewById(R.id.ivProgressBg) }
    private val progressFor: ImageView by lazy { findViewById(R.id.ivProgressFor) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_puzzle)
        progressBg.post { showProgress() }
        findViewById<View>(R.id.ivPrivacyPolicy).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://sites.google.com/view/arrowerpuzzle/home")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        valueAnimator?.resume()
    }

    override fun onPause() {
        super.onPause()
        valueAnimator?.pause()
    }

    private var valueAnimator: ValueAnimator? = null

    private fun showProgress() {
        valueAnimator = ValueAnimator.ofInt(0, progressBg.width)
        valueAnimator?.duration = 5000
        valueAnimator?.addUpdateListener {
            val progress = it.animatedValue as Int
            val layoutParams = progressFor.layoutParams
            layoutParams.width = progress
            progressFor.layoutParams = layoutParams
        }
        valueAnimator?.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                if (BaseApplication.instance.appOpenAdManager.isAdAvailable()) {
                    BaseApplication.instance.showAdIfAvailable(this@SplashActivity,
                        object : BaseApplication.OnShowAdCompleteListener {
                            override fun onShowAdComplete() {
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        })
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        valueAnimator?.start()
    }

}