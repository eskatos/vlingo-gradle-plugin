package io.vlingo.gradle;

import java.io.File;
import java.io.Serializable;
import java.util.Set;


class ActorProxyGeneratorParameters implements Serializable {

    final Set<File> classesDirs;
    final Set<String> actorProtocols;
    final File destinationDir;

    ActorProxyGeneratorParameters(Set<File> classesDirs, Set<String> actorProtocols, File destinationDir) {
        this.classesDirs = classesDirs;
        this.actorProtocols = actorProtocols;
        this.destinationDir = destinationDir;
    }
}
