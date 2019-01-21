package io.vlingo.gradle

import java.io.File


internal
val Lang.dirName: String
    get() = name.toLowerCase()


private
val templatesRoot =
    File("src/test/resources/protocols")


internal
fun copyActorProtocolsMainTo(lang: Lang, projectDir: File) =
    templatesRoot.resolve("main/${lang.dirName}").copyRecursively(
        projectDir.resolve("src/main/${lang.dirName}").also(File::mkdirs)
    )


internal
fun copyActorProtocolsTestTo(lang: Lang, projectDir: File) =
    projectDir.resolve("src/test").let { srcTest ->
        listOf(lang.dirName, "resources").forEach { subDir ->
            templatesRoot.resolve("test/$subDir").copyRecursively(
                srcTest.resolve(subDir).also(File::mkdirs)
            )
        }
    }
