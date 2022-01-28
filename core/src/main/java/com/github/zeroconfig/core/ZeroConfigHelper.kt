package org.bole.boleandroid.zeroconfig

import android.content.Context
import com.github.zeroconfig.api.IZeroConfigBackend
import com.github.zeroconfig.api.IZeroConfigHolder
import com.github.zeroconfig.api.ZeroConfigInformation
import com.google.gson.Gson

object ZeroConfigHelper {
    private lateinit var gson: Gson
    private lateinit var bufferMap: MutableMap<Class<*>, Any?>
    private var configs: MutableMap<String, ZeroConfigInformation> = mutableMapOf()

    private fun getKeyOfClass(clazz: Class<*>): String {
        return configs.filter { it.value.clazz == clazz.canonicalName }.keys.first()
    }

    fun addConfigHolder(configHolder: IZeroConfigHolder): ZeroConfigHelper {
        configs.putAll(configHolder.getValue())
        return this
    }

    fun init(context: Context): ZeroConfigHelper {
        gson = Gson()
        bufferMap = mutableMapOf()
        return this
    }

    /**
     * 获取所有已经定义的配置
     */
    fun getAllDefinedConfigs(): Collection<ZeroConfigInformation> = configs.values

    fun <T> saveConfig(clazz: Class<*>, value: T, isMultipleProcess: Boolean = false) {
        IZeroConfigBackend.instance.saveConfig(getKeyOfClass(clazz), gson.toJson(value), isMultipleProcess)
        bufferMap[clazz] = value
    }

    fun <T> readConfig(clazz: Class<*>, isMultipleProcess: Boolean = false): T {
        return if (bufferMap.containsKey(clazz)) {
            bufferMap[clazz]
        } else {
            val jsonString = IZeroConfigBackend.instance.readConfig(getKeyOfClass(clazz), "{}", isMultipleProcess)
            gson.fromJson(jsonString, clazz)
        } as T
    }

    private fun getClassByKey(key: String): Class<*>? {
        val className = configs[key]?.clazz ?: return null
        return Class.forName(className)
    }

    fun removeConfig(clazz: Class<*>, isMultipleProcess: Boolean = false) {
        bufferMap.remove(clazz)
        IZeroConfigBackend.instance.removeConfig(getKeyOfClass(clazz), isMultipleProcess)
    }
}