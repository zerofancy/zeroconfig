package com.github.mmkvbackend

import android.content.Context
import com.github.zeroconfig.api.IZeroConfigBackend
import com.google.auto.service.AutoService
import com.tencent.mmkv.MMKV

@AutoService(IZeroConfigBackend::class)
class MMKVZeroConfigBackend : IZeroConfigBackend {
    companion object {
        fun initMMKV(context: Context) {
            MMKV.initialize(context)
        }
    }

    override fun saveConfig(key: String, value: String, isMultipleProcess: Boolean) {
        val mmkv = MMKV.defaultMMKV(
            if (isMultipleProcess) {
                MMKV.MULTI_PROCESS_MODE
            } else {
                MMKV.SINGLE_PROCESS_MODE
            }, null
        )
        mmkv.encode(key, value)
    }

    override fun readConfig(key: String, defaultValue: String, isMultipleProcess: Boolean): String {
        val mmkv = MMKV.defaultMMKV(
            if (isMultipleProcess) {
                MMKV.MULTI_PROCESS_MODE
            } else {
                MMKV.SINGLE_PROCESS_MODE
            }, null
        )
        return mmkv.decodeString(key, defaultValue)!!
    }

    override fun removeConfig(key: String, isMultipleProcess: Boolean) {
        val mmkv = MMKV.defaultMMKV(
            if (isMultipleProcess) {
                MMKV.MULTI_PROCESS_MODE
            } else {
                MMKV.SINGLE_PROCESS_MODE
            }, null
        )
        mmkv.remove(key)
    }
}