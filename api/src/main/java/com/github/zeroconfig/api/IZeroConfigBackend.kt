package com.github.zeroconfig.api

interface IZeroConfigBackend {
    fun saveConfig(key: String, value: String, isMultipleProcess: Boolean = false)
    fun readConfig(key: String, isMultipleProcess: Boolean = false)
    fun removeConfig(key: String, isMultipleProcess: Boolean = false)
}