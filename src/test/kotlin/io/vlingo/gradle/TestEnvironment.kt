package io.vlingo.gradle


val quickTest: Boolean
    get() = System.getProperty("quickTest") == "true"


val testedGradleVersions =
    if (quickTest) listOf(
        "5.1.1",
        "4.7"
    )
    else listOf(
        "5.1",
        "5.0",
        "4.10",
        "4.9",
        "4.8",
        "4.7"
    )


val testedLanguages =
    if (quickTest) listOf(Lang.JAVA, Lang.KOTLIN)
    else Lang.values().toList()


enum class Lang {
    JAVA,
    GROOVY,
    SCALA,
    KOTLIN
}
