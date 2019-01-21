package io.vlingo.gradle

import org.gradle.util.GradleVersion


val quickTest: Boolean
    get() = System.getProperty("quickTest")?.toBoolean() ?: false


val supportedGradleVersions =
        if (quickTest) listOf(GradleVersion.current().version)
        else listOf(
                "5.1.1" /*,
                "5.1",
                "5.0"*/
        )
