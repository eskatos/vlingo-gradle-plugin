package io.vlingo.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.apache.commons.io.FileUtils

import org.junit.Ignore
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
    void "incremental build and cacheability "() {

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

    @Test
    @Ignore("ProxyGenerator can't load TestProtocol1 when loading TestProtocol2")
    // Requires passing a complete classpath to the proxy generator because of inter-protocol dependencies
    // TODO Report to vlingo
    void "multiple source sets "() {

        FileUtils.copyDirectory(new File("src/test/samples/simple"), root)

        assert new File(root, "src/main/java/io/vlingo/gradle/actortest/Test2Protocol.java")
                .renameTo(new File(root, "src/test/java/io/vlingo/gradle/actortest/Test2Protocol.java"))
        assert new File(root, "src/main/java/io/vlingo/gradle/actortest/Test2ProtocolActor.java")
                .renameTo(new File(root, "src/test/java/io/vlingo/gradle/actortest/Test2ProtocolActor.java"))

        def buildFile = new File(root, "build.gradle")
        buildFile.text = buildFile.readLines().dropRight(7).join("\n") + """
            generateActorProxies {
                actorProtocols.set(["io.vlingo.gradle.actortest.Test1Protocol"])
            }
            generateTestActorProxies {
                actorProtocols.set(["io.vlingo.gradle.actortest.Test2Protocol"])
            }
        """.stripIndent()

        build("build", "-s").tap {
            assertThat(task(":generateActorProxies").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileActorProxiesJava").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":generateTestActorProxies").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":compileTestActorProxiesJava").outcome, equalTo(TaskOutcome.SUCCESS))
            assertThat(task(":test").outcome, equalTo(TaskOutcome.SUCCESS))
        }
    }

    private BuildResult build(String... arguments) {
        return build(root, arguments);
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
