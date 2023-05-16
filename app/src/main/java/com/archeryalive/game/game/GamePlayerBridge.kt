package com.archeryalive.game.game

import com.archeryalive.game.sdk.WebJavaScriptIn


class GamePlayerBridge(gamePlayerListener: GamePlayerListener) : BaseGamePlayerBridge(gamePlayerListener),
    WebJavaScriptIn {
    override val name: String
        get() = "GamePlayerBridge"
}