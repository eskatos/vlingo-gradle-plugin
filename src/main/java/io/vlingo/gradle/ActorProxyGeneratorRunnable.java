package io.vlingo.gradle;

import io.vlingo.actors.Properties;
import io.vlingo.actors.ProxyGenerator;

import org.gradle.api.GradleException;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;


class ActorProxyGeneratorRunnable implements Runnable {

    private final ActorProxyGeneratorParameters parameters;

    @Inject
    ActorProxyGeneratorRunnable(ActorProxyGeneratorParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void run() {

        try {
            // TODO this won't scale with multiple jvm languages
            File classesDir = parameters.classesDirs.iterator().next();
            assert classesDir.isDirectory();

            if (parameters.destinationDir.exists()) {
                Files.walk(parameters.destinationDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            parameters.destinationDir.mkdirs();

            Properties.properties.setProperty("proxy.generated.classes.main", classesDir.getCanonicalPath() + "/");
            Properties.properties.setProperty("proxy.generated.sources.main", parameters.destinationDir.getCanonicalPath() + "/");

            try (ProxyGenerator generator = ProxyGenerator.forMain(true)) {
                for (String protocol : parameters.actorProtocols) {
                    generator.generateFor(protocol);
                }
            }
        } catch (Exception ex) {
            throw new GradleException("vlingo actor proxies generation failed", ex);
        }
    }
}
