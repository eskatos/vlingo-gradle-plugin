import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.plugins.ide.idea.model.IdeaProject
import org.jetbrains.gradle.ext.*

plugins {
    `build-scan`
    `kotlin-dsl`
    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.2.3"
    id("org.jetbrains.gradle.plugin.idea-ext") version "0.5"
}

group = "io.vlingo"
version = "0"

val isCI = System.getenv("CI") == "true"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {

    compileOnly("io.vlingo:vlingo-actors:0.8.0")
    compileOnly("io.vlingo:vlingo-common:0.8.0")

    compileOnly("com.android.tools.build:gradle:3.3.0")

    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.apache.commons:commons-io:1.3.2")
}

tasks.test {
    testLogging.events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    systemProperty("quickTest", !isCI)
}

idea {
    project {
        settings {
            delegateActions {
                delegateBuildRunToGradle = true
                testRunner = ActionDelegationConfig.TestRunner.GRADLE
            }
            doNotDetectFrameworks("android")
        }
    }
}

if (isCI) {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        tag("CI")
    }
}


// -- Kotlin extensions required because of lack of accessors in gradle-kotlin-dsl
// -- for Gradle extensions on properties of project Gradle extensions

fun IdeaProject.settings(block: ProjectSettings.() -> Unit): Unit =
    (this as ExtensionAware).configure(block)

fun ProjectSettings.delegateActions(block: ActionDelegationConfig.() -> Unit): Unit =
    (this as ExtensionAware).configure(block)
