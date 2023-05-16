package com.archeryalive.game.game

/**
 * Description: 定义web游戏JS回调的行为
 *
 */
interface GamePlayerListener {

    /**
     * Called when the game started , the script is available form now
     */
    fun gameLoadingStarted()

    /**
     * Called when the game is load finished.
     */
    fun gameLoadingFinished()

    /**
     * 在实际游戏进行时触发（例如，在关卡开始时或游戏未暂停时）。
     */
    fun gameStart()

    /**
     * 当游戏停止时触发（例如，当游戏暂停、关卡完成、玩家死亡或播放广告时）。
     */
    fun gameStop()

    /**
     * 随着游戏中的自然停止而触发（例如，在关卡之间、重新启动关卡，或者基本上任何玩家即将回到游戏阶段的场景）。
     */
    fun commercialBreak()

    /**
     * 在用户提示时触发以换取游戏内奖励（例如解锁金币或奖励物品、死后复活或任何可以说服玩家观看广告的场景）。
     */
    fun rewardedBreak()

    fun gameplayStart()
}