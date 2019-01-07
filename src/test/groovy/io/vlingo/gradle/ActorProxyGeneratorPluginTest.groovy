package io.vlingo.gradle

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ActorProxyGeneratorPluginTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder()

    def getRoot() {
        tmpDir.root
    }

    @Test
    void "simple sample"() {

        FileUtils.copyDirectory(new File("src/test/samples/simple"), root)

        GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(root)
                .withArguments("build", "-s")
                .build()
    }
}
