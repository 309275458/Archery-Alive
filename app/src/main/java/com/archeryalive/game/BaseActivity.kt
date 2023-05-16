package com.archeryalive.game

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

open class  BaseActivity: AppCompatActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            System.exit(0)
        }
        return super.onKeyDown(keyCode, event)
    }

}