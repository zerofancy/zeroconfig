package com.github.zeroconfig.processor

import com.github.zeroconfig.api.DefaultScope
import com.github.zeroconfig.api.IZeroConfigHolder
import com.github.zeroconfig.api.ZeroConfig
import com.github.zeroconfig.api.ZeroConfigInformation
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ZeroConfigProcessor : AbstractProcessor() {
    private lateinit var elementUtils: Elements
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var typeUtils: Types

    private lateinit var classInfoMap: MutableMap<String, ZeroConfigInformation>

    private lateinit var zeroConfigHolderPackage: String

    private lateinit var zeroConfigHolderClassName: String

    private var counter = 0

    private var wasWrittenToFile: Boolean = false

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        elementUtils = processingEnv.elementUtils
        filer = processingEnv.filer
        messager = processingEnv.messager
        typeUtils = processingEnv.typeUtils
        classInfoMap = mutableMapOf()

        val fullPackagePath = processingEnv.options[OPTION_CONFIG_HOLDER] ?: let {
            error("必须设置$OPTION_CONFIG_HOLDER")
            return
        }
        zeroConfigHolderClassName = fullPackagePath.split('.').last()
        zeroConfigHolderPackage =
            fullPackagePath.removeSuffix(zeroConfigHolderClassName)
                .removeSuffix(".")
        note("${javaClass.simpleName} init className = ${zeroConfigHolderClassName}, package = ${zeroConfigHolderPackage}. ")
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        counter++
        note("Processing round $counter, new annotations: ${annotations.isNotEmpty()}, processingOver: ${roundEnvironment.processingOver()}")

        if (roundEnvironment.processingOver() && annotations.isNotEmpty()) {
            error("Unexpected processing state: annotations still available after processing over")
            return false
        }
        if (annotations.isEmpty()) {
            return false
        }

        if (wasWrittenToFile) {
            error("Unexpected processing state: annotations still available after writing.")
            return false
        }

        // 收集数据
        roundEnvironment.getElementsAnnotatedWith(ZeroConfig::class.java).forEach { element ->
            //使用了注解的某个类
            if (element !is TypeElement) {
                error("注解只能标记在实体类上：$element")
                return false
            }
            val annotation = element.getAnnotation(ZeroConfig::class.java)
            if (!checkAnnotationValid(annotation)) return false
            classInfoMap[annotation.key] = ZeroConfigInformation(
                key = annotation.key,
                clazz = element.qualifiedName.toString(),
                title = annotation.title,
                scope = getClassFromAnnotation { annotation.scope.qualifiedName!! },
                owner = annotation.owner
            )
        }

        generateCode()

        wasWrittenToFile = true

        return true
    }

    private fun generateCode() {
        val codeBlocks = classInfoMap.map {
            CodeBlock.builder().add(
                "%S to %T(key=%S,title=%S,clazz=%S,scope=%S,owner=%S)",
                it.key,
                ZeroConfigInformation::class,
                it.value.key,
                it.value.title,
                it.value.clazz,
                it.value.scope,
                it.value.owner
            ).build().toString()
        }
        val file = FileSpec.builder(zeroConfigHolderPackage, zeroConfigHolderClassName)
            .addType(
                TypeSpec.classBuilder(zeroConfigHolderClassName)
                    .addSuperinterface(IZeroConfigHolder::class)
                    .addFunction(
                        FunSpec.builder("getValue")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(
                                Map::class.parameterizedBy(String::class)
                                    .plusParameter(ZeroConfigInformation::class)
                            )
                            .addStatement(
                                "return mapOf(${codeBlocks.joinToString(",")})"
                            )
                            .build()
                    )
                    .build()
            )
            .build()
        note(file.toString())
        file.writeTo(filer)
    }

    private fun checkAnnotationValid(annotation: ZeroConfig): Boolean {
        if (annotation.key.trim() == "") {
            error("key不能为空：$annotation")
            return false
        }
        if (annotation.title.trim() == "") {
            warning("title为空：$annotation")
        }
        if (annotation.owner.trim() == "") {
            error("必须指定owner：$annotation")
            return false
        }
        if (getClassFromAnnotation { annotation.scope.qualifiedName!! } == DefaultScope::class.qualifiedName) {
            warning("建议指定业务线：$annotation")
        }
        return true
    }

    /**
     * 获取annotation中的Class
     * https://www.jianshu.com/p/6822278f4771
     */
    private fun getClassFromAnnotation(block: () -> String): String {
        return try {
            block()
        } catch (e: MirroredTypeException) {
            e.typeMirror.toString()
        }
    }

    /**
     * 没有用注解，避免了硬编码
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(ZeroConfig::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedOptions(): MutableSet<String> = mutableSetOf(OPTION_CONFIG_HOLDER)

    fun note(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "$message\r\n")
    }

    // \r\n换行 https://medium.com/@cafonsomota/annotation-processor-printing-a-message-and-doing-it-in-a-new-line-1b6609e86e5c
    fun warning(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "$message\r\n")
    }

    fun error(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "$message\r\n")
    }

    companion object {
        private const val OPTION_CONFIG_HOLDER = "zeroConfigHolder"
    }
}