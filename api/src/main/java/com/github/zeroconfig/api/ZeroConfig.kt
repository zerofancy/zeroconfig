package com.github.zeroconfig.api

import kotlin.reflect.KClass

/**
 * 标注于配置实体类之上，指定配置字段名
 * @param key 配置字段
 * @param title 配置项名（给人看的）
 * @param owner 负责人
 * @param scope 所属的业务线
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ZeroConfig(
    val key: String,
    val title: String = "",
    val owner: String,
    val scope: KClass<out ZeroScope> = DefaultScope::class
)