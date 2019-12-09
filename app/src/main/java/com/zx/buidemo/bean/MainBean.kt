package com.zx.buidemo.bean

/**
 * Created by Xiangb on 2019/11/8.
 * 功能：
 */
data class MainBean(var classFile : Class<*>,
                    var className : String,
                    var uiName : String,
                    var resId : Int = 0) {
}