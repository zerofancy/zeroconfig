package org.bole.boleandroid.zeroconfig

import kotlin.reflect.KProperty

class ZeroConfigDelegate<T>(private val clazz: Class<T>, private val isMultipleProcess: Boolean = false) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ZeroConfigHelper.readConfig(clazz, isMultipleProcess)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) =
        ZeroConfigHelper.saveConfig(clazz, value, isMultipleProcess)
}

/**
 * 委托获取配置值
 * @param clazz 配置类型
 */
fun <T> zeroConfig(clazz: Class<T>): ZeroConfigDelegate<T> = ZeroConfigDelegate(clazz)

/**
 * 委托获取配置值
 * 泛型实化，调用更方便
 */
inline fun <reified T> zeroConfig(): ZeroConfigDelegate<T> =
    zeroConfig(T::class.java)

/**
 * 委托获取配置值
 * @param clazz 配置类型
 */
fun <T> multipleConfig(clazz: Class<T>): ZeroConfigDelegate<T> = ZeroConfigDelegate(clazz, isMultipleProcess = true)

/**
 * 委托获取配置值
 * 泛型实化，调用更方便
 */
inline fun <reified T> multipleConfig(): ZeroConfigDelegate<T> =
    multipleConfig(T::class.java)
