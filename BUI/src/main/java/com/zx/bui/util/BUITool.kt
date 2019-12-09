package com.zx.bui.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.util.TypedValue


/**
 * Created by Xiangb on 2019/11/14.
 * 功能：
 */
object BUITool {
    /**
     * dp转px
     *
     * @param dpVal dp值
     * @return
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * sp转px
     *
     * @param spVal sp值
     * @return
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spVal, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 获取更深颜色
     * @param degree 0.0f-1.0f
     */
    fun getDarkerColor(color: Int, degree : Float = 0.1f): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv) // convert to hsv
        // make darker
        hsv[1] = hsv[1] + degree // 饱和度更高
        hsv[2] = hsv[2] - degree // 明度降低
        return Color.HSVToColor(hsv)
    }

    /**
     * 获取更浅的颜色
     * @param degree 0.0f-1.0f
     */
    fun getLighterColor(@ColorInt color: Int, degree : Float = 0.1f): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv) // convert to hsv
        hsv[1] = hsv[1] - degree // less saturation
        hsv[2] = hsv[2] + degree // more brightness
        return Color.HSVToColor(hsv)
    }

    /**
     * Color对象转换成字符串
     * @param color Color对象
     * @return 16进制颜色字符串
     * */
    @SuppressLint("NewApi")
    fun toHexFromColor(@ColorInt color: Int): String {
        var r: String
        var g: String
        var b: String
        var su = StringBuilder()
        var color = Color.valueOf(color)
        r = Integer.toHexString((color.red() * 256).toInt())
        g = Integer.toHexString((color.green() * 256).toInt())
        b = Integer.toHexString((color.blue() * 256).toInt())
        r = if (r.length == 1) "0" + r else r
        g = if (g.length == 1) "0" + g else g
        b = if (b.length == 1) "0" + b else b
//        r = r.toUpperCase()
//        g = g.toUpperCase()
//        b = b.toUpperCase()
//        su.append("-0x")
        su.append(r)
        su.append(g)
        su.append(b)
        //0xFF0000FF
        return su.toString()
    }
}