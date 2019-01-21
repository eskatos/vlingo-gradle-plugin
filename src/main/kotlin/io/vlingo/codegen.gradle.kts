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

plugins.withType<JavaBasePlugin> {

    sourceSets.configureEach {

        val codeGenTaskName = getTaskName("generate", "actorProxies")
        val codeGenDestDir = layout.buildDirectory.dir("generated-sources/$codeGenTaskName/java/")

        val codeGenTask = tasks.register<ActorProxyGeneratorTask>(codeGenTaskName) {
            classpath.from(compileClasspath)
            destinationDirectory.set(codeGenDestDir)
        }

        listOf("java", "groovy", "scala", "kotlin").forEach { language ->
            plugins.withId(language) {
                codeGenTask.configure {
                    classpath.from(Callable { tasks.named(getCompileTaskName(language)) })
                }
            }
        }

        val compileTaskName = getTaskName("compile", "actorProxiesJava")
        val compileDestDir = layout.buildDirectory.dir("classes/$codeGenTaskName/java/")

        val compileTask = tasks.register<JavaCompile>(compileTaskName) {
            dependsOn(codeGenTask)
            setSource(codeGenDestDir)
            classpath = files(compileClasspath, provider { output.classesDirs.files })
            setDestinationDir(compileDestDir.map { it.asFile })
        }

        output.dir(compileDestDir, "builtBy" to compileTask)
    }
}

val Project.sourceSets: NamedDomainObjectContainer<SourceSet>
    get() = if (isGradleFourDotTenOrGreater) the()
    else the<JavaPluginConvention>().sourceSets
