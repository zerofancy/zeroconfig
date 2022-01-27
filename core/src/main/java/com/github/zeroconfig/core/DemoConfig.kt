package org.bole.boleandroid.zeroconfig

import androidx.annotation.Keep
import com.github.zeroconfig.api.ZeroConfig

@ZeroConfig("demo_config",owner = "liuhaixin.zero",title = "在util中的测试分组")
@Keep
data class DemoConfig(val author: String = "liuhaixin.zero")
