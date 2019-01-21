import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `kotlin-dsl`
    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.2.3"
}

group = "io.vlingo"
version = "0"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
}

dependencies {

    compileOnly("io.vlingo:vlingo-actors:0.7.9")
    compileOnly("io.vlingo:vlingo-common:0.7.9")

    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.apache.commons:commons-io:1.3.2")
}

tasks.test {
    testLogging.events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    systemProperty("quickTest", System.getenv("CI") != "true")
}
