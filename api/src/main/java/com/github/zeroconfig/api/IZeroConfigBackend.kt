package com.github.zeroconfig.api

import java.util.*

interface IZeroConfigBackend {
    companion object {
        val instance get() = ServiceLoader.load(IZeroConfigBackend::class.java).first()!!
    }

    fun saveConfig(key: String, value: String, isMultipleProcess: Boolean = false)
    fun readConfig(key: String, defaultValue: String, isMultipleProcess: Boolean = false): String
    fun removeConfig(key: String, isMultipleProcess: Boolean = false)
}