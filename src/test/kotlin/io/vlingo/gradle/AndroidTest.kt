package io.vlingo.gradle

import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.io.File


@RunWith(Parameterized::class)
class AndroidTest(gradleVersion: String) : AbstractTestKitTest(gradleVersion) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Gradle {0}")
        fun getTestParameters() = testedGradleVersionsWithAndroid
    }

    @Before
    fun assumeAndroidHome() =
        assumeNotNull(System.getenv("ANDROID_HOME"))

    @Test
    fun `android java actor `() {

        File("src/test/resources/android").copyRecursively(root)
        copyActorProtocolsMainTo(Lang.JAVA, root.resolve("app"))
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

        buildAndFail("testDebugUnitTest").apply {
            assertThat(output, containsString("> io.vlingo.codegen doesn't support Android projects"))
        }
    }
}
