package com.github.zeroconfig

import android.app.Application
import com.github.mmkvbackend.MMKVZeroConfigBackend
import org.bole.boleandroid.zeroconfig.ZeroConfigHelper

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MMKVZeroConfigBackend.initMMKV(this)
        ZeroConfigHelper.init(this)
            .addConfigHolder(com.github.zeroconfig.app.ZeroConfigHolder())
    }
}