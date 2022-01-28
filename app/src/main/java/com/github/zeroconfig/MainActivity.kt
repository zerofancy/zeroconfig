package com.github.zeroconfig

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.Keep
import com.github.zeroconfig.api.ZeroConfig
import com.github.zeroconfig.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import org.bole.boleandroid.zeroconfig.zeroConfig

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var startedTimesConfig by zeroConfig<ConfigDemo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startedTimesTextView.text = startedTimesConfig.startedTimes.toString()
        startedTimesConfig = startedTimesConfig.copy(startedTimesConfig.startedTimes + 1)
    }
}

@ZeroConfig("config_demo", "配置测试", "zerofancy")
@Keep
data class ConfigDemo(
    @SerializedName("started_times")
    val startedTimes: Long = 0
)