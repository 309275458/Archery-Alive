package com.archeryalive.game.game

import org.json.JSONException
import org.json.JSONObject

/**
 * Description:
 */
class IFrameOptions private constructor(private val options: JSONObject) {

    companion object {
        val default = Builder().build()
    }

    override fun toString(): String {
        return options.toString()
    }


    internal fun getOrigin(): String {
        return options.getString(Builder.ORIGIN)
    }

    class Builder {
        companion object {
            private const val AUTO_PLAY = "autoplay"
            private const val CONTROLS = "controls"
            private const val ENABLE_JS_API = "enablejsapi"
            private const val FS = "fs"
            const val ORIGIN = "origin"
            private const val REL = "rel"
            private const val SHOW_INFO = "showinfo"
            private const val DISABLE_KB = "disablekb"
            private const val IV_LOAD_POLICY = "iv_load_policy"
            private const val MODEST_BRANDING = "modestbranding"
            private const val CC_LOAD_POLICY = "cc_load_policy"
            private const val CC_LANG_PREF = "cc_lang_pref"
        }

        private val builderOptions = JSONObject()

        init {
//            autoPlay()
            addInt(CONTROLS, 0)
            addInt(ENABLE_JS_API, 1)
            addInt(DISABLE_KB, 1)
            addInt(FS, 0)
            addString(ORIGIN, GamePlayerConstants.gameHost)
            addInt(REL, 0)
            addInt(SHOW_INFO, 0)
            addInt(IV_LOAD_POLICY, 3)
            addInt(MODEST_BRANDING, 1)
            addInt(CC_LOAD_POLICY, 0)
        }

        fun build(): IFrameOptions {
            return IFrameOptions(builderOptions)
        }


        fun controls(controls: Int): Builder {
            addInt(CONTROLS, controls)
            return this
        }

        fun origin(origin: String): Builder {
            addString(ORIGIN, origin)
            return this
        }


        private fun addString(key: String, value: String) {
            try {
                builderOptions.put(key, value)
            } catch (e: JSONException) {
                throw RuntimeException("Illegal JSON value $key: $value")
            }
        }

        private fun addInt(key: String, value: Int) {
            try {
                builderOptions.put(key, value)
            } catch (e: JSONException) {
                throw RuntimeException("Illegal JSON value $key: $value")
            }
        }
    }


}