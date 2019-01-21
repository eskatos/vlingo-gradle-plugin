package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File


abstract class AbstractTestKitTest(

        private
        val gradleVersion: String

) {

    @Rule
    @JvmField
    val tmpDir = TemporaryFolder()

    protected
    val root: File
        get() = tmpDir.root

    protected
    fun build(vararg arguments: String): BuildResult =
            build(root, *arguments);

    protected
    fun build(projectDir: File, vararg arguments: String): BuildResult =
            GradleRunner.create()
                    .withGradleVersion(gradleVersion)
                    .withPluginClasspath()
                    .withProjectDir(projectDir)
                    .withArguments(*arguments)
                    .build()
}
