package io.vlingo.gradle

import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile

import org.gradle.api.internal.plugins.DslObject
import org.gradle.internal.metaobject.DynamicObject


private
val androidPluginIds = listOf(
    "com.android.application",
    "com.android.feature",
    "com.android.instantapp",
    "com.android.library",
    "com.android.test"
)


internal
fun PluginContainer.withAndroidPlugin(block: (Plugin<*>) -> Unit) {
    androidPluginIds.forEach { id ->
        withId(id, block)
    }
}


internal
fun Project.configureEachAndroidVariant(block: AndroidVariantCompatible.() -> Unit) {
    val variants = DslObject(extensions.getByName("android")).asDynamicObject.run {
        when {
            hasProperty("libraryVariants") -> getProperty("libraryVariants") as DomainObjectSet<*>
            hasProperty("applicationVariants") -> getProperty("applicationVariants") as DomainObjectSet<*>
            else -> null
        }
    }
    variants?.configureEachCompatible {
        block(AndroidVariantCompatible(DslObject(this).asDynamicObject))
    }
}


internal
class AndroidVariantCompatible(

    private
    val variant: DynamicObject

) {

    val name: String
        get() = variant.getProperty("name") as String

    val javaCompileProvider: TaskProviderCompatible<JavaCompile>
        get() =
            if (variant.hasProperty("javaCompileProvider")) taskProviderCompatibleFor(variant.getProperty("javaCompileProvider") as TaskProvider<JavaCompile>)
            else taskProviderCompatibleFor(variant.getProperty("javaCompile") as JavaCompile)

    fun getCompileClasspath(key: Any?): FileCollection =
        variant.invokeMethod("getCompileClasspath", key) as FileCollection

    fun registerPostJavacGeneratedBytecode(files: FileCollection) {
        variant.invokeMethod("registerPostJavacGeneratedBytecode", files)
    }

    override fun toString() =
        "${this::class.simpleName}($name)"
}
