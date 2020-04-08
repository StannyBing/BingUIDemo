package com.zx.bui.util

import android.graphics.Paint

/**
 * Created by Xiangb on 2019/11/13.
 * 功能：
 */
/**
 * 计算baseline
 */
fun Paint.getBaseline(): Float {
    return (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
}