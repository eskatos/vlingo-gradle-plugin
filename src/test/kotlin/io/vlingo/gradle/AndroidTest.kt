package io.vlingo.gradle

import org.gradle.util.GradleVersion

import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.Before
import org.junit.Test

import java.io.File


class AndroidTest : AbstractTestKitTest(GradleVersion.current().version) {

    @Before
    fun assumeAndroidHome() =
        assumeNotNull(System.getenv("ANDROID_HOME"))

    @Test
    fun `android actor `() {

        File("src/test/resources/android").copyRecursively(root)
        copyActorProtocolsMainTo(Lang.JAVA, root.resolve("app"))
        root.resolve("settings.gradle").apply {
            writeText(readText() + "\n" + """
                // TODO REMOVE ME
                includeBuild("/Users/paul/src/vlingo-related/vlingo-common")
                includeBuild("/Users/paul/src/vlingo-related/vlingo-actors")
            """.trimIndent())
        }
        root.resolve("app/build.gradle").apply {
            writeText(
                """
                    plugins {
                        id("io.vlingo.codegen")
                    }
                """.trimIndent()
                    + "\n" + readText() + "\n"
                    + """
                        dependencies {
                            implementation("io.vlingo:vlingo-actors:0.8.0")
                        }
                    """.trimIndent()
            )
        }

        root.walk().forEach(::println)

        buildAndFail("testDebugUnitTest").apply {
            assertThat(output, containsString("> io.vlingo.codegen doesn't support Android projects"))
        }
    }
}
