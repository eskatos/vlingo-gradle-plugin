package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.io.File


abstract class AbstractTestKitTest(private val gradleVersion: String) {

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
    open fun build(projectDir: File, vararg arguments: String): BuildResult =
            runnerFor(projectDir, *arguments).build().also { println(it.output) }

    private
    fun runnerFor(projectDir: File, vararg arguments: String): GradleRunner =
            GradleRunner.create()
                    .withGradleVersion(gradleVersion)
                    .withPluginClasspath()
                    .withProjectDir(projectDir)
                    .withArguments(*(linkedSetOf("-s") + arguments).toTypedArray())

    protected
    fun BuildResult.outcomeOf(path: String) =
            task(path)?.outcome

    protected
    fun BuildResult.assertTask(path: String, outcome: TaskOutcome) {
        assertThat(outcomeOf(path), equalTo(outcome))
    }
}
