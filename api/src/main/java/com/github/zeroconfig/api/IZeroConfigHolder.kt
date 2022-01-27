package com.github.zeroconfig.api

interface IZeroConfigHolder {
    fun getValue(): Map<String, ZeroConfigInformation>
}