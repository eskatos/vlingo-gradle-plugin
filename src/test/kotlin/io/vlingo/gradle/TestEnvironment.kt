package io.vlingo.gradle


val quickTest: Boolean
    get() = System.getProperty("quickTest") == "true"


val supportedGradleVersions =
        if (quickTest) listOf(
                "5.1.1",
                "4.7"
        )
        else listOf(
                "5.1.1",
                "5.0",
                "4.10",
                "4.9",
                "4.8",
                "4.7"
        )
