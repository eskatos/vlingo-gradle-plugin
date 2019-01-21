package io.vlingo.gradle

import org.gradle.testkit.runner.TaskOutcome

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.io.File


@RunWith(Parameterized::class)
class CodeGenPluginTest(gradleVersion: String) : AbstractTestKitTest(gradleVersion) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Gradle {0}")
        fun getTestedGradleVersions() = supportedGradleVersions
    }

    @Test
    fun `success, up-to-date and from-cache `() {

        val locationA = root.resolve("locationA")
        val locationB = root.resolve("locationB")

        copySampleTo("simple", locationA)
        locationA.resolve("settings.gradle").appendText("""

            buildCache {
                local {
                    directory = file("../build-cache-dir")
                }
            }

        """.trimIndent())
        locationA.copyRecursively(locationB)

        build(locationA, "build", "--build-cache", "-s").apply {
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.SUCCESS))
        }

        build(locationA, "build", "--build-cache", "-s").apply {
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.UP_TO_DATE))
        }

        build(locationA, "clean", "build", "--build-cache", "-s").apply {
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

    @Test
    fun `test actor depending on main type `() {

        copySampleTo("simple", root)

        assert(root.resolve("src/main/java/io/vlingo/gradle/actortest/Test2Protocol.java")
                .renameTo(root.resolve("src/test/java/io/vlingo/gradle/actortest/Test2Protocol.java")))
        assert(root.resolve("src/main/java/io/vlingo/gradle/actortest/Test2ProtocolActor.java")
                .renameTo(root.resolve("src/test/java/io/vlingo/gradle/actortest/Test2ProtocolActor.java")))

        root.resolve("build.gradle").let { buildScript ->
            buildScript.writeText(buildScript.readLines().dropLast(7).joinToString("\n") + """

                generateActorProxies {
                    actorProtocols.set(["io.vlingo.gradle.actortest.Test1Protocol"])
                }
                generateTestActorProxies {
                    actorProtocols.set(["io.vlingo.gradle.actortest.Test2Protocol"])
                }

            """.trimIndent())
        }

        build("build", "-s").apply {
            assertThat(task(":generateActorProxies")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":generateTestActorProxies")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileTestActorProxiesJava")!!.outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test")!!.outcome, equalTo(TaskOutcome.SUCCESS))
        }
    }

    private
    fun copySampleTo(name: String, target: File) =
            File("src/test/samples/$name").copyRecursively(target)
}
