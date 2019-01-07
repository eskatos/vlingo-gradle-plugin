package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.apache.commons.io.FileUtils

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat


@RunWith(Parameterized.class)
class ActorProxyGeneratorPluginTest {

    @Parameterized.Parameters(name = "Gradle {0}")
    static List<String> getTestedGradleVersions() {
        ["5.1", "5.0"]
    }

    private final String gradleVersion

    ActorProxyGeneratorPluginTest(String gradleVersion) {
        this.gradleVersion = gradleVersion
    }

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder()

    def getRoot() {
        tmpDir.root
    }

    @Test
    void "simple sample"() {

        def locationA = new File(root, "locationA")
        def locationB = new File(root, "locationB")

        FileUtils.copyDirectory(new File("src/test/samples/simple"), locationA)
        new File(locationA, "settings.gradle").append("""

            buildCache {
                local {
                    directory = file("../build-cache-dir")
                }
            }

        """.stripIndent())
        FileUtils.copyDirectory(locationA, locationB)

        build(locationA, "build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.SUCCESS))
        }

        build(locationA, "build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.UP_TO_DATE))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.UP_TO_DATE))
        }

        build(locationA, "clean", "build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.FROM_CACHE))
        }

        build(locationB, "build", "--build-cache").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.FROM_CACHE))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.FROM_CACHE))
        }
    }

    private BuildResult build(File projectDir, String... arguments) {
        return GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(arguments)
                .build()
    }
}
