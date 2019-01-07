package io.vlingo.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Set;


@CacheableTask
public class ActorProxyGeneratorTask extends DefaultTask {

    private final WorkerExecutor workerExecutor;
    private final ConfigurableFileCollection classesDirs = getProject().files();
    private final SetProperty<String> actorProtocols = getProject().getObjects().setProperty(String.class).empty();
    private final DirectoryProperty destinationDirectory = getProject().getObjects().directoryProperty();

    @Inject
    public ActorProxyGeneratorTask(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    @Classpath
    public ConfigurableFileCollection getClassesDirs() {
        return classesDirs;
    }

    @Input
    public SetProperty<String> getActorProtocols() {
        return actorProtocols;
    }

    @OutputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getDestinationDirectory() {
        return destinationDirectory;
    }

    @TaskAction
    @SuppressWarnings("unused")
    public void generateActorProxies() throws IOException {
        Set<String> protocols = actorProtocols.get();
        if (!protocols.isEmpty()) {

            Set<File> proxyGeneratorClasspath = getProject().getConfigurations().detachedConfiguration(
                    getProject().getDependencies().create("io.vlingo:vlingo-actors:" + VLingoGradlePluginVersions.getDefaultVLingoVersion())
            ).getFiles();

            ActorProxyGeneratorParameters proxyGeneratorParams = new ActorProxyGeneratorParameters(
                    classesDirs.getFiles(),
                    protocols,
                    destinationDirectory.getAsFile().get()
            );

            workerExecutor.submit(ActorProxyGeneratorRunnable.class, configuration -> {
                configuration.setIsolationMode(IsolationMode.CLASSLOADER);
                configuration.setClasspath(proxyGeneratorClasspath);
                configuration.setParams(proxyGeneratorParams);
            });

        } else {
            setDidWork(false);
        }
    }
}
