package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.io.File


/**
 * Assert plugin behavior across JVM languages.
 *
 * One protocol actor in the `main` source set.
 * Another protocol actor, using the former, in the `test` source set.
 *
 * Test is parameterized with a `main` JVM language and a `test` JVM language.
 *
 * @see [Lang]
 */
@RunWith(Parameterized::class)
class CrossJvmLanguageTest(private val parameters: P) : AbstractTestKitTest(parameters.gradle) {

    data class P(val gradle: String, val mainLang: Lang, val testLang: Lang)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun getTestParameters() = sequence {
            testedGradleVersions.forEach { gradleVersion ->
                testedLanguages.forEach { mainLang ->
                    testedLanguages.forEach { testLang ->
                        yield(P(gradleVersion, mainLang, testLang))
                    }
                }
            }
        }.asIterable()
    }

    @Test
    fun `success, up-to-date and from-cache `() {

        val locationA = root.resolve("locationA")
        val locationB = root.resolve("locationB")

        copyActorProtocolsMainTo(parameters.mainLang, locationA)
        copyActorProtocolsTestTo(parameters.testLang, locationA)

        locationA.resolve("settings.gradle").writeText("""
            rootProject.name = "test"

            buildCache {
                local {
                    directory = file("../build-cache-dir")
                }
            }

            $includeVlingoModulesBuild
        """.trimIndent())
        locationA.resolve("build.gradle").writeText("""

            plugins {
                ${parameters.pluginRequests}
                id("io.vlingo.codegen")
            }

            dependencies {
                implementation("io.vlingo:vlingo-actors:0.8.0")
                testImplementation("junit:junit:4.11")

                ${parameters.dependencies}
            }

            repositories {
                mavenCentral()
            }

            generateActorProxies {
                actorProtocols.set(["io.vlingo.gradle.actortest.Test1Protocol"])
            }
            generateTestActorProxies {
                actorProtocols.set(["io.vlingo.gradle.actortest.Test2Protocol"])
            }

        """.trimIndent())

        locationA.copyRecursively(locationB)

        build(locationA, "build").apply {
            assertTask(":generateActorProxies", TaskOutcome.SUCCESS)
            assertTask(":compileActorProxiesJava", TaskOutcome.SUCCESS)
            assertTask(":generateTestActorProxies", TaskOutcome.SUCCESS)
            assertTask(":compileTestActorProxiesJava", TaskOutcome.SUCCESS)
            assertTask(":test", TaskOutcome.SUCCESS)
        }

        build(locationA, "build").apply {
            assertTask(":generateActorProxies", TaskOutcome.UP_TO_DATE)
            assertTask(":compileActorProxiesJava", TaskOutcome.UP_TO_DATE)
            assertTask(":generateTestActorProxies", TaskOutcome.UP_TO_DATE)
            assertTask(":compileTestActorProxiesJava", TaskOutcome.UP_TO_DATE)
            assertTask(":test", TaskOutcome.UP_TO_DATE)
        }

        build(locationA, "clean", "build").apply {
            assertTask(":generateActorProxies", TaskOutcome.FROM_CACHE)
            assertTask(":compileActorProxiesJava", TaskOutcome.FROM_CACHE)
            assertTask(":generateTestActorProxies", TaskOutcome.FROM_CACHE)
            assertTask(":compileTestActorProxiesJava", TaskOutcome.FROM_CACHE)
            assertTask(":test", TaskOutcome.FROM_CACHE)
        }

        build(locationB, "build").apply {
            assertTask(":generateActorProxies", TaskOutcome.FROM_CACHE)
            assertTask(":compileActorProxiesJava", TaskOutcome.FROM_CACHE)
            assertTask(":generateTestActorProxies", TaskOutcome.FROM_CACHE)
            assertTask(":compileTestActorProxiesJava", TaskOutcome.FROM_CACHE)
            assertTask(":test", TaskOutcome.FROM_CACHE)
        }
    }

    override fun build(projectDir: File, vararg arguments: String): BuildResult {
        return super.build(projectDir, *(linkedSetOf("--build-cache") + arguments).toTypedArray())
    }

    private
    val Lang.pluginRequest: String
        get() =
            if (this == Lang.KOTLIN) """id("org.jetbrains.kotlin.jvm") version "1.3.11""""
            else """id("$dirName")"""

    private
    val P.pluginRequests: String
        get() = setOf(mainLang, testLang).joinToString("\n") {
            it.pluginRequest
        }

    private
    val P.dependencies: String
        get() = sequence {

            val groovy = "localGroovy()"
            val scala = "org.scala-lang:scala-library:2.11.12"
            val kotlin = "org.jetbrains.kotlin:kotlin-stdlib"

            val main = "implementation"
            when (mainLang) {
                Lang.JAVA -> Unit
                Lang.GROOVY -> yield("$main($groovy)")
                Lang.SCALA -> yield("$main(\"$scala\")")
                Lang.KOTLIN -> yield("$main(\"$kotlin\")")
            }

            val test = "testImplementation"
            when (testLang) {
                Lang.JAVA -> Unit
                Lang.GROOVY -> yield("$test($groovy)")
                Lang.SCALA -> yield("$test(\"$scala\")")
                Lang.KOTLIN -> yield("$test(\"$kotlin\")")
            }
        }.joinToString("\n")
}
