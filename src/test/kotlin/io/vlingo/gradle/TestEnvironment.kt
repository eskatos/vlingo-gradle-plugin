package io.vlingo.gradle


val quickTest: Boolean
    get() = System.getProperty("quickTest") == "true"


val supportedGradleVersions =
        if (quickTest) listOf(
                "5.1.1",
                "5.0"
        )
        else listOf(
                "5.1.1",
                "5.0"
        )
