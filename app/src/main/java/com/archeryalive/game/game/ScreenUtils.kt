package com.archeryalive.game.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

/**
 * Description:
 */
object ScreenUtils {

    private var screenHeight = 0
    private const val screenWidth = 0
    private var statusBarHeight = 0

    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    fun getScreenWidth(mContext: Context?): Int {
        return if (screenWidth > 0) {
            screenWidth
        } else mContext?.resources?.displayMetrics?.widthPixels ?: 0
    }

    /**
     * 获取屏幕高度,是否包含导航栏高度
     */
    @JvmStatic
    fun getScreenHeight(mContext: Context?, isIncludeNav: Boolean): Int {
        if (mContext == null) {
            return 0
        }
        val screenHeight = getScreenHeight(mContext)
        return if (isIncludeNav) {
            screenHeight
        } else {
            screenHeight - getNavigationBarHeight(mContext)
        }
    }

    /**
     * 获取屏幕高(包括底部虚拟按键)
     *
     * @param mContext
     * @return
     */
    @JvmStatic
    fun getScreenHeight(mContext: Context?): Int {
        if (screenHeight > 0) {
            return screenHeight
        }
        if (mContext == null) {
            return 0
        }
        val displayMetrics = DisplayMetrics()
        //WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        val display = getWindowManager(mContext)!!.defaultDisplay
        screenHeight = try {
            display.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels
        } catch (e: Exception) {
            display.height
        }
        return screenHeight
    }

    /**
     * 获取WindowManager。
     */
    private fun getWindowManager(mContext: Context?): WindowManager? {
        return if (mContext == null) {
            null
        } else mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * 获取NavigationBar的高度
     */
    private fun getNavigationBarHeight(mContext: Context): Int {
        if (!hasNavigationBar(mContext)) {
            return 0
        }
        val resources = mContext.resources
        val resourceId = resources.getIdentifier("navigation_bar_height",
            "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 是否存在NavigationBar
     */
    private fun hasNavigationBar(mContext: Context): Boolean {
        val display = getWindowManager(mContext)!!.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        return realSize.x != size.x || realSize.y != size.y
    }

    /**
     * dp转成px
     *
     * @param mContext
     * @param dipValue
     * @return
     */
    fun dip2px(mContext: Context, dipValue: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun sp2px(mContext: Context, spValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue,
            mContext.resources.displayMetrics
        )
    }

    /**
     * px转成dp
     *
     * @param mContext
     * @param pxValue
     * @return
     */
    fun px2dip(mContext: Context, pxValue: Int): Int {
        val scale = mContext.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    @SuppressLint("PrivateApi")
    private fun getStatusBarHeightByReflect(mContext: Context): Int { //int sbHeight
        if (statusBarHeight > 0) {
            return statusBarHeight
        }
        statusBarHeight = try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val sbHeightId = field[obj].toString().toInt()
            mContext.resources.getDimensionPixelSize(sbHeightId)
        } catch (e1: Exception) {
            e1.printStackTrace()
            0
        }
        return statusBarHeight
    }

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    fun getStatusBarHeight(mContext: Context): Int {
        var statusBarHeight = getStatusBarHeightByReflect(mContext)
        if (statusBarHeight == 0) {
            statusBarHeight = dip2px(mContext, 30f)
        }
        return statusBarHeight
    }

    @JvmStatic
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs: Resources = context.resources
        val id = rs . getIdentifier ("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        return hasNavigationBar
    }

}