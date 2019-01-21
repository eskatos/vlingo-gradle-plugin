package io.vlingo.gradle

import io.vlingo.actors.Logger
import io.vlingo.actors.ProxyGenerator
import io.vlingo.common.compiler.DynaType

import org.gradle.api.GradleException

import javax.inject.Inject


internal
class ActorProxyGeneratorWork @Inject constructor(

    private
    val spec: ActorProxyGeneratorSpec

) : Runnable {

    override fun run(): Unit =
        try {
            spec.destinationDir.apply {
                takeIf { it.exists() }?.deleteRecursively()
                mkdirs()
            }
            ProxyGenerator.forClasspath(spec.classPath.toList(), spec.destinationDir, DynaType.Main, true, WorkLogger).use { generator ->
                spec.actorProtocols.forEach { protocol ->
                    generator.generateFor(protocol)
                }
            }
        } catch (ex: Exception) {
            throw GradleException("vlingo actor proxies generation failed", ex)
        }

    object WorkLogger : Logger {

        private
        var enabled = true

        override fun name(): String =
            "actor-proxy-generator-work"

        override fun close() {
            enabled = false
        }

        override fun isEnabled(): Boolean =
            enabled

        override fun log(message: String) {
            println(message)
        }

        override fun log(message: String, throwable: Throwable) {
            log(message)
            throwable.printStackTrace(System.out)
        }
    }
}
