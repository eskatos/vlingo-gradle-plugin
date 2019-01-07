package io.vlingo.gradle;

import io.vlingo.actors.Properties;
import io.vlingo.actors.ProxyGenerator;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;


// TODO isolate using the Gradle Worker API
public class ActorProxyGeneratorTask extends DefaultTask {

    @Classpath
    public final ConfigurableFileCollection classesDirs = getProject().files();

    @Input
    public final SetProperty<String> actorProtocols = getProject().getObjects().setProperty(String.class).empty();

    @OutputDirectory
    public final DirectoryProperty destinationDirectory = getProject().getObjects().directoryProperty();

    @TaskAction
    public void generateActorProxies() throws Exception {
        Set<String> protocols = actorProtocols.get();
        if (!protocols.isEmpty()) {

            // TODO this won't scale with multiple jvm languages
            File classesDir = classesDirs.getSingleFile();
            assert classesDir.isDirectory();

            File destinationDir = destinationDirectory.getAsFile().get();
            if (destinationDir.exists()) {
                Files.walk(destinationDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            destinationDir.mkdirs();

            // TODO this won't play well with concurrent task execution
            Properties.properties.setProperty("proxy.generated.classes.main", classesDir.getCanonicalPath() + "/");
            Properties.properties.setProperty("proxy.generated.sources.main", destinationDir.getCanonicalPath() + "/");

            try (ProxyGenerator generator = ProxyGenerator.forMain(true)) {
                for (String protocol : protocols) {
                    generator.generateFor(protocol);
                }
            }
        }
    }
}
