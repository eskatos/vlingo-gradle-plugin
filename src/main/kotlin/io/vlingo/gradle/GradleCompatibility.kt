package io.vlingo.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

import org.gradle.kotlin.dsl.the

import org.gradle.util.GradleVersion

import java.util.concurrent.Callable

import kotlin.reflect.KClass


private
val v5_0 = GradleVersion.version("5.0")


private
val v4_10 = GradleVersion.version("4.10")


private
val v4_9 = GradleVersion.version("4.9")


private
val hasConfigurationAvoidanceSupport =
        GradleVersion.current() >= v4_9


internal
val hasObjectFactoryPropertyFactories =
        GradleVersion.current() >= v5_0


private
val hasSourceSetContainerExtension =
        GradleVersion.current() >= v4_10


internal
val Project.sourceSets: NamedDomainObjectContainer<SourceSet>
    get() =
        if (hasSourceSetContainerExtension) the()
        else the<JavaPluginConvention>().sourceSets


internal
fun <T : Any> NamedDomainObjectContainer<T>.configureEachCompatible(block: T.() -> Unit) =
        if (hasConfigurationAvoidanceSupport) configureEach(block)
        else all(block)


internal
fun <T : Task> TaskContainer.registerCompatible(name: String, type: KClass<T>, block: T.() -> Unit): TaskProviderCompatible<T> =
        if (hasConfigurationAvoidanceSupport) taskProviderCompatibleFor(register(name, type.java, block))
        else taskProviderCompatibleFor(create(name, type.java, block))


internal
fun TaskContainer.namedCompatible(name: String): TaskProviderCompatible<Task> =
        if (hasConfigurationAvoidanceSupport) taskProviderCompatibleFor(named(name))
        else taskProviderCompatibleFor(getByName(name))


private
fun <T : Task> taskProviderCompatibleFor(provider: TaskProvider<T>) =
        TaskProviderCompatible<T>({ provider.get() }, { provider.configure(it) })


private
fun <T : Task> taskProviderCompatibleFor(task: T) =
        TaskProviderCompatible<T>({ task }, { task.apply(it) })


internal
class TaskProviderCompatible<T : Task>(

        private
        val provide: () -> T,

        private
        val apply: (T.() -> Unit) -> Unit

) : Callable<T> {

    override fun call(): T =
            provide()

    fun configure(block: T.() -> Unit) =
            apply(block)
}
