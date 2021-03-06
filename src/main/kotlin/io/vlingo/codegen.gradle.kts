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

import io.vlingo.gradle.ActorProxyGeneratorTask
import io.vlingo.gradle.configureEachAndroidVariant
import io.vlingo.gradle.configureEachCompatible
import io.vlingo.gradle.namedCompatible
import io.vlingo.gradle.registerCompatible
import io.vlingo.gradle.sourceSets
import io.vlingo.gradle.withAndroidPlugin


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
        val compileDestDir = layout.buildDirectory.dir("classes/$compileTaskName/java/")

        val compileTask = tasks.registerCompatible(compileTaskName, JavaCompile::class) {
            dependsOn(codeGenTask)
            setSource(codeGenDestDir)
            classpath = files(compileClasspath, provider { output.classesDirs.files })
            setDestinationDir(compileDestDir.map { it.asFile })
        }

        output.dir(compileDestDir, "builtBy" to compileTask)
    }
}


plugins.withAndroidPlugin {

    configureEachAndroidVariant {

        println("VARIANT $this")

        val variantCompileClasspath = getCompileClasspath(null)

        val codeGenTaskName = "generate${name.capitalize()}ActorProxies"
        val codeGenDestDir = layout.buildDirectory.dir("generated-sources/$codeGenTaskName/java")

        val codeGenTask = tasks.registerCompatible(codeGenTaskName, ActorProxyGeneratorTask::class) {
            classpath.from(variantCompileClasspath)
            destinationDirectory.set(codeGenDestDir)
        }

        // TODO how to get all languages compilation outputs
        codeGenTask.configure {
            classpath.from(javaCompileProvider)
        }

        val compileTaskName = "compile${name.capitalize()}ActorProxiesJava"
        val compileDestDir = layout.buildDirectory.dir("classes/$compileTaskName/java")

        val compileTask = tasks.registerCompatible(compileTaskName, JavaCompile::class) {
            dependsOn(codeGenTask)
            setSource(codeGenDestDir)
            // TODO how to get all languages compilation outputs
            classpath = files(variantCompileClasspath, javaCompileProvider)
            setDestinationDir(compileDestDir.map { it.asFile })
        }

        registerPostJavacGeneratedBytecode(files(compileTask))
    }
}
