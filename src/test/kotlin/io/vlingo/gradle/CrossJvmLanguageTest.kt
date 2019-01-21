package io.vlingo.gradle

import org.gradle.testkit.runner.TaskOutcome

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.io.File


enum class Lang {
    JAVA,
    GROOVY,
    SCALA,
    KOTLIN;

    val dirName: String
        get() = name.toLowerCase()

    val pluginRequest: String
        get() = if (this == KOTLIN) """id("org.jetbrains.kotlin.jvm") version "1.3.11""""
        else """id("$dirName")"""
}


data class Params(
        val gradle: String,
        val mainLang: Lang,
        val testLang: Lang
) {
    val pluginRequests: String
        get() = setOf(mainLang, testLang).joinToString("\n") {
            it.pluginRequest
        }

    val dependencies: String
        get() = sequence {
            when (mainLang) {
                Lang.GROOVY -> yield("implementation(localGroovy())")
                Lang.SCALA -> yield("implementation(\"org.scala-lang:scala-library:2.11.12\")")
                Lang.KOTLIN -> yield("implementation(\"org.jetbrains.kotlin:kotlin-stdlib\")")
            }
            when (testLang) {
                Lang.GROOVY -> yield("testImplementation(localGroovy())")
                Lang.SCALA -> yield("testImplementation(\"org.scala-lang:scala-library:2.11.12\")")
                Lang.KOTLIN -> yield("testImplementation(\"org.jetbrains.kotlin:kotlin-stdlib\")")
            }
        }.joinToString("\n")
}


@RunWith(Parameterized::class)
class CrossJvmLanguageTest(

        private
        val parameters: Params

) : AbstractTestKitTest(parameters.gradle) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun getTestParameters() = sequence {
            supportedGradleVersions.forEach { gradleVersion ->
                Lang.values().forEach { mainLang ->
                    Lang.values().forEach { testLang ->
                        yield(Params(
                                gradleVersion,
                                mainLang,
                                testLang
                        ))
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

            // TODO REMOVE ME
            includeBuild("/Users/paul/src/vlingo-related/vlingo-common")
            includeBuild("/Users/paul/src/vlingo-related/vlingo-actors")
        """.trimIndent())
        locationA.resolve("build.gradle").writeText("""

            plugins {
                ${parameters.pluginRequests}
                id("io.vlingo.codegen")
            }

            dependencies {
                implementation("io.vlingo:vlingo-actors:0.7.9")
                testImplementation("junit:junit:4.11")

                ${parameters.dependencies}
            }

            repositories {
                mavenCentral()
            }

            generateActorProxies {
                actorProtocols.set([
                        "io.vlingo.gradle.actortest.Test1Protocol",
                        "io.vlingo.gradle.actortest.Test2Protocol"
                ])
            }

        """.trimIndent())

        locationA.copyRecursively(locationB)

        build(locationA, "build", "--build-cache", "-s").apply {
            println(output)
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.SUCCESS))
        }

        build(locationA, "build", "--build-cache", "-s").apply {
            println(output)
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
        }

        build(locationA, "clean", "build", "--build-cache", "-s").apply {
            println(output)
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
        }

        build(locationB, "build", "--build-cache", "-s").apply {
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.FROM_CACHE))
        }
    }

    private
    fun copyActorProtocolsMainTo(lang: Lang, projectDir: File) {
        projectDir.resolve("src/main/${lang.dirName}").let { targetDir ->
            targetDir.mkdirs()
            File("src/test/resources/protocols/main/${lang.dirName}/")
                    .copyRecursively(targetDir)
        }
    }

    private
    fun copyActorProtocolsTestTo(lang: Lang, projectDir: File) {
        projectDir.resolve("src/test/${lang.dirName}").let { targetDir ->
            targetDir.mkdirs()
            File("src/test/resources/protocols/test/${lang.dirName}")
                    .copyRecursively(targetDir)
        }
        projectDir.resolve("src/test/resources").let { targetDir ->
            targetDir.mkdirs()
            File("src/test/resources/protocols/test/resources")
                    .copyRecursively(targetDir)
        }
    }
}
