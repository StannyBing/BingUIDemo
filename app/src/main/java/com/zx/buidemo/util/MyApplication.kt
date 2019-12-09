package com.zx.buidemo.util

import android.app.Application
import com.zx.zxutils.ZXApp

/**
 * Created by Xiangb on 2019/11/13.
 * 功能：
 */
class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        ZXApp.init(this,true)
    }

}