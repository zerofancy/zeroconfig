# ZeroConfig

项目中的配置日渐复杂，使用ZeroConfig可以更加有效存取和组织项目中的配置信息。

这个东西是我在做毕设时就在考虑的，现在我将它单独抽出来可以直接依赖。

## 使用

首先定义配置类

```kotlin
@ZeroConfig("s3_config", "S3相关配置", "zerofancy")
@Keep
data class S3Config(
    @SerializedName("key")
    val key: String = "",
    @SerializedName("secret")
    val secret: String = "",
    @SerializedName("bucket")
    val bucket: String = "",
    @SerializedName("path")
    val path: String = "{year}/{month}/{md5}.{extName}",
    @SerializedName("domain_prefix")
    val domainPrefix: String = "",
    @SerializedName("region")
    val region: String = Regions.DEFAULT_REGION.getName(),
)
```

接下来在需要使用的地方

```kotlin
private var s3Config by zeroConfig<S3Config>()

...
fun getAWSAccessKeyId(): String = s3Config.key // 读取
private fun saveConfig() {
    s3Config = s3Config.copy( // 写入
        key = binding.keyEditText.text.toString(),
        secret = binding.secretEditText.text.toString(),
        bucket = binding.bucketEditText.text.toString(),
        path = binding.pathEditText.text.toString(),
        domainPrefix = binding.domainPrefixEditText.text.toString()
    )
}

```



## 接入

![jitpack](https://jitpack.io/v/zerofancy/zeroconfig.svg)

首先添加jitpack仓库

```groovy
    repositories {
		...
        maven { url 'https://jitpack.io' }
    }
```

在需要接入的仓中添加依赖并配置生成配置类

```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
}

kapt {
    arguments {
        arg("zeroConfigHolder", "top.ntutn.s3manager.app.ZeroConfigHolder")
    }
}

dependencies {
	... implementation("com.github.zerofancy.zeroconfig:core:2012cd0a87")
    implementation("com.github.zerofancy.zeroconfig:mmkvbackend:2012cd0a87")
    kapt("com.github.zerofancy.zeroconfig:processor:2012cd0a87")

}
```

在Application类中，初始化ZeroConfig和MMKV。

```kotlin
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MMKVZeroConfigBackend.initMMKV(this)
        ZeroConfigHelper.init(this)
            .addConfigHolder(top.ntutn.s3manager.app.ZeroConfigHolder())
        ...
    }
}
```

> 注意这里的`MMKVZeroConfigBackend.initMMKV(this)`，如果你已经初始化过MMKV这里是不需要重复执行的。

可以看到，初始化ZeroConfig时链式调用传入所有配置类。

## 原理

ZeroConfig目前有这样的项目结构：

![image-20220129032128609](https://images.ntutn.top/2022/01/562b34d29a3117fcf8beb52586c92636.png)

- api 定义了一些公共接口
- app 简单demo
- core 主要功能工具类
- mmkvbackend 数据存储MMKV实现
- processor 注解处理

### 属性委托

Kotlin有称为属性委托的语法，允许我们将一个变量的getter和setter委托给另一个对象：

```kotlin
class ZeroConfigDelegate<T>(private val clazz: Class<T>, private val isMultipleProcess: Boolean = false) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ZeroConfigHelper.readConfig(clazz, isMultipleProcess)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) =
        ZeroConfigHelper.saveConfig(clazz, value, isMultipleProcess)
}
```

接下来再定义几个顶级函数就是上面的效果了。

### 注解处理

首先再看一下前面调用处代码

```kotlin
private var s3Config by zeroConfig<S3Config>()
```

我们如何知道去哪里读s3Config这个值呢？显然这里我们传递的信息只有一个类型`S3Config`。

而这个S3Config是我们上面定义的配置类

```kotlin
@ZeroConfig("s3_config", "S3相关配置", "zerofancy")
@Keep
data class S3Config(
    @SerializedName("key")
    val key: String = "",
    @SerializedName("secret")
    val secret: String = "",
    @SerializedName("bucket")
    val bucket: String = "",
    @SerializedName("path")
    val path: String = "{year}/{month}/{md5}.{extName}",
    @SerializedName("domain_prefix")
    val domainPrefix: String = "",
    @SerializedName("region")
    val region: String = Regions.DEFAULT_REGION.getName(),
)
```

这里有个注解`@ZeroConfig`，上面携带了我们要的信息。其实问题到这里已经可以结束了，我们只要反射拿注解上的信息就可以知道配置key为如何了。

但，移动设备的性能还是比较吃紧的，滥用反射是不可取的，正好我们可以在编译期就把这个事情做了。

原理其实也不难，就是扫描代码中的特定注解，找到后生成一些代码，运行时能直接读，这样效率就很高了。具体有兴趣直接去看processor代码吧。

## 接入方

[S3 Manager](https://github.com/zerofancy/s3manager)
