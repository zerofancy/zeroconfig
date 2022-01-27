package com.github.zeroconfig.api

/**
 * 每个业务线应该自己实现这个接口，便于遇到问题快速联系到负责人
 */
interface ZeroScope {
    fun getScopeName(): String
    fun getOwners(): List<String>
}

/**
 * 默认业务线
 */
class DefaultScope : ZeroScope {
    override fun getScopeName(): String = "未指定"
    override fun getOwners(): List<String> = listOf("liuhaixin.zero") //the god
}