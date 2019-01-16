package io.vlingo.gradle

import java.io.File
import java.io.Serializable


internal
data class ActorProxyGeneratorSpec(
        val classesDirs: Set<File>,
        val actorProtocols: Set<String>,
        val destinationDir: File
) : Serializable
