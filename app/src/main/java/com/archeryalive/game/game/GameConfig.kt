package com.archeryalive.game.game


class GameConfig private constructor() {
    companion object {
        private const val DEFAULT_VIDEO_MAX_CACHED_SIZE = 512 * 1024 * 1024L
        private const val DEFAULT_VIDEO_CACHED_PATH = "gameCache"
        private const val MAX_CACHE_SIZE = DEFAULT_VIDEO_MAX_CACHED_SIZE
        private const val CACHE_FILE_DIR = DEFAULT_VIDEO_CACHED_PATH
        private const val CACHE_ENABLE = true

     
        fun create(): GameConfig {
            return GameConfig()
        }
    }

    internal var maxCacheSize: Long = MAX_CACHE_SIZE
    internal var cacheFileDir: String = CACHE_FILE_DIR
    internal var cacheEnable: Boolean = CACHE_ENABLE



    fun updateMaxCacheSize(maxCacheSize: Long): GameConfig {
        this.maxCacheSize = maxCacheSize
        return this
    }


    fun setCacheFileDir(cacheFileDir: String): GameConfig {
        this.cacheFileDir = cacheFileDir
        return this
    }


    fun setCacheEnable(cacheEnable: Boolean): GameConfig {
        this.cacheEnable = cacheEnable
        return this
    }

}