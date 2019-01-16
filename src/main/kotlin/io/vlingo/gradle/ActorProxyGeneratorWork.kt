package io.vlingo.gradle

import io.vlingo.actors.Properties
import io.vlingo.actors.ProxyGenerator

import org.gradle.api.GradleException

import javax.inject.Inject


internal
class ActorProxyGeneratorWork @Inject constructor(

        private
        val spec: ActorProxyGeneratorSpec

) : Runnable {

    override fun run(): Unit =
            try {
                // TODO this won't scale with multiple jvm languages
                val classesDir = spec.classesDirs.single()
                assert(classesDir.isDirectory)

                spec.destinationDir.apply {
                    takeIf { it.exists() }?.deleteRecursively()
                    mkdirs()
                }

                Properties.properties.apply {
                    setProperty("proxy.generated.classes.main", "${classesDir.canonicalPath}/")
                    setProperty("proxy.generated.sources.main", "${spec.destinationDir.canonicalPath}/")
                }

                ProxyGenerator.forMain(true).use { generator ->
                    spec.actorProtocols.forEach { protocol ->
                        generator.generateFor(protocol)
                    }
                }
            } catch (ex: Exception) {
                throw GradleException("vlingo actor proxies generation failed", ex)
            }
}
