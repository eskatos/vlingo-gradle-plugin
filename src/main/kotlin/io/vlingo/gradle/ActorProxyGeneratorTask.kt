package io.vlingo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor

import org.gradle.kotlin.dsl.*

import javax.inject.Inject


@CacheableTask
open class ActorProxyGeneratorTask @Inject constructor(

    private
    val workerExecutor: WorkerExecutor

) : DefaultTask() {

    @Classpath
    val classpath = project.files()

    @Input
    val actorProtocols = project.objects.setProperty(String::class.java).also { it.set(emptySet()) }

    @OutputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val destinationDirectory = directoryPropertyCompatible()

    @TaskAction
    @Suppress("unused")
    fun generateActorProxies() {
        val destinationDir = destinationDirectory.asFile.get().apply {
            deleteRecursively()
            mkdirs()
        }
        val protocols = actorProtocols.get()
        if (protocols.isNotEmpty()) {

            // TODO:performance let ProxyGenerator accept a ClassLoader, remove classPath from the spec and use current loader in the work
            val spec = ActorProxyGeneratorSpec(
                classpath.files,
                protocols,
                destinationDir
            )

            workerExecutor.submit(ActorProxyGeneratorWork::class) {
                isolationMode = IsolationMode.CLASSLOADER
                classpath = this@ActorProxyGeneratorTask.classpath.files
                params(spec)
            }
        } else {
            didWork = false
        }
    }

    private
    fun directoryPropertyCompatible(): DirectoryProperty =
        if (hasObjectFactoryPropertyFactories) project.objects.directoryProperty()
        else newOutputDirectory()
}
