package io.vlingo.gradle

import org.gradle.api.DefaultTask
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

    @Classpath
    val classesDirs = project.files()

    @Input
    val actorProtocols = project.objects.setProperty(String::class.java).empty()

    @OutputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val destinationDirectory = project.objects.directoryProperty()

    @TaskAction
    @Suppress("unused")
    fun generateActorProxies() {
        val protocols = actorProtocols.get()
        if (protocols.isNotEmpty()) {

            val spec = ActorProxyGeneratorSpec(
                    classesDirs.files,
                    protocols,
                    destinationDirectory.asFile.get()
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
}