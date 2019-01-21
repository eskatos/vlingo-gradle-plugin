/**
 * io.vlingo.codegen Gradle Plugin.
 *
 * Reacts to the `java-base` plugin and, for each source set,
 * registers a task that depends on the source set's classes and generates the actor proxies java source,
 * and another task to compile them to classes,
 * adding the result to the source set output.
 *
 * TODO review wiring logic
 * as it won't scale with multiple jvm languages
 * and feels hackish
 * any better way to do this?
 */
package io.vlingo

import io.vlingo.gradle.*

import kotlin.reflect.KClass


plugins.withType<JavaBasePlugin> {

    sourceSets.configureEachCompatible {

        val codeGenTaskName = getTaskName("generate", "actorProxies")
        val codeGenDestDir = layout.buildDirectory.dir("generated-sources/$codeGenTaskName/java/")

        val codeGenTask = tasks.registerCompatible(codeGenTaskName, ActorProxyGeneratorTask::class) {
            classpath.from(compileClasspath)
            destinationDirectory.set(codeGenDestDir)
        }

        listOf("java", "groovy", "scala", "kotlin").forEach { language ->
            plugins.withId(language) {
                codeGenTask.configure {
                    classpath.from(tasks.namedCompatible(getCompileTaskName(language)))
                }
            }
        }

        val compileTaskName = getTaskName("compile", "actorProxiesJava")
        val compileDestDir = layout.buildDirectory.dir("classes/$codeGenTaskName/java/")

        val compileTask = tasks.registerCompatible(compileTaskName, JavaCompile::class) {
            dependsOn(codeGenTask)
            setSource(codeGenDestDir)
            classpath = files(compileClasspath, provider { output.classesDirs.files })
            setDestinationDir(compileDestDir.map { it.asFile })
        }

        output.dir(compileDestDir, "builtBy" to compileTask)
    }
}

val Project.sourceSets: NamedDomainObjectContainer<SourceSet>
    get() =
        if (isGradleFourDotTenOrGreater) the()
        else the<JavaPluginConvention>().sourceSets

fun <T : Any> NamedDomainObjectContainer<T>.configureEachCompatible(block: T.() -> Unit) =
        if (isGradleFourDotNineOrGreater) configureEach(block)
        else all(block)

fun <T : Task> TaskContainer.registerCompatible(name: String, type: KClass<T>, block: T.() -> Unit): TaskProviderCompatible<T> =
        if (isGradleFourDotNineOrGreater) taskProviderCompatibleFor(register(name, type.java, block))
        else taskProviderCompatibleFor(create(name, type.java, block))

fun TaskContainer.namedCompatible(name: String): TaskProviderCompatible<Task> =
        if (isGradleFourDotNineOrGreater) taskProviderCompatibleFor(named(name))
        else taskProviderCompatibleFor(getByName(name))

fun <T : Task> taskProviderCompatibleFor(provider: TaskProvider<T>) =
        TaskProviderCompatible<T>({ provider.get() }, { provider.configure(it) })

fun <T : Task> taskProviderCompatibleFor(task: T) =
        TaskProviderCompatible<T>({ task }, { task.apply(it) })

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
