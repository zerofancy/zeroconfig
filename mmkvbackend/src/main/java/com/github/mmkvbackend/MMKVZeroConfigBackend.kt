package com.github.mmkvbackend

import com.github.zeroconfig.api.IZeroConfigBackend
import com.tencent.mmkv.MMKV

class MMKVZeroConfigBackend: IZeroConfigBackend {
    override fun saveConfig(key: String, value: String, isMultipleProcess: Boolean) {
        val mmkv = MMKV.defaultMMKV(
            if (isMultipleProcess) {
                MMKV.MULTI_PROCESS_MODE
            } else {
                MMKV.SINGLE_PROCESS_MODE
            }, null
        )
        TODO("Not yet implemented")
    }

    override fun readConfig(key: String, isMultipleProcess: Boolean) {
        TODO("Not yet implemented")
    }

    override fun removeConfig(key: String, isMultipleProcess: Boolean) {
        TODO("Not yet implemented")
    }
}