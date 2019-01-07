package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.apache.commons.io.FileUtils

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat


class ActorProxyGeneratorPluginTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder()

    def getRoot() {
        tmpDir.root
    }

    @Test
    void "simple sample"() {

        FileUtils.copyDirectory(new File("src/test/samples/simple"), root)
        new File(root, "settings.gradle").append("""

            buildCache {
                local {
                    directory = file("build-cache-dir")
                }
            }

        """.stripIndent())

        build("build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.SUCCESS))
        }

        build("build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.UP_TO_DATE))
        }

        build("clean", "build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.FROM_CACHE))
        }
    }

    private BuildResult build(String... arguments) {
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(root)
                .withArguments(arguments)
                .build()
    }
}
