package com.github.zeroconfig.api

/**
 * 注解处理过程中用于传递注解上信息的类
 */
data class ZeroConfigInformation(
    val key: String,
    val title: String,
    val clazz: String,
    val scope: String,
    val owner: String
)