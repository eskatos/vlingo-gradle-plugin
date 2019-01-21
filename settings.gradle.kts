rootProject.name = "vlingo-gradle-plugin"

listOf("common", "actors").forEach { module ->
    if (file("../vlingo-$module").isDirectory) {
        includeBuild("../vlingo-$module")
    }
}
