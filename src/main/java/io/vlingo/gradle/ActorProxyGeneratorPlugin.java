package io.vlingo.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.Collections;


@SuppressWarnings("unused")
public class ActorProxyGeneratorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().withId("java-base", plugin -> {

            project.getExtensions().getByType(SourceSetContainer.class).configureEach(inputSourceSet -> {

                String codeGenTaskName = inputSourceSet.getTaskName("generate", "actorProxies");
                Provider<Directory> codeGenDestDir = project.getLayout().getBuildDirectory().dir("generated-sources/" + codeGenTaskName + "/java/");

                TaskProvider<ActorProxyGeneratorTask> codeGenTask = project.getTasks().register(codeGenTaskName, ActorProxyGeneratorTask.class, task -> {
                    // TODO this won't scale with multiple jvm languages
                    TaskProvider<Task> compileJava = project.getTasks().named(inputSourceSet.getCompileJavaTaskName());
                    task.getClasspath().from(inputSourceSet.getCompileClasspath());
                    task.getClasspath().from(compileJava);
                    task.getClassesDirs().from(compileJava);
                    task.getDestinationDirectory().set(codeGenDestDir);
                });

                String compileTaskName = inputSourceSet.getTaskName("compile", "actorProxiesJava");
                Provider<Directory> compileDestDir = project.getLayout().getBuildDirectory().dir("classes/" + codeGenTaskName + "/java/");

                TaskProvider<JavaCompile> compileTask = project.getTasks().register(compileTaskName, JavaCompile.class, task -> {
                    task.dependsOn(codeGenTask);
                    task.setSource(codeGenDestDir);
                    task.setClasspath(project.files(inputSourceSet.getCompileClasspath(), inputSourceSet.getOutput().getClassesDirs().getFiles()));
                    task.setDestinationDir(compileDestDir.map(dir -> dir.getAsFile()));
                });

                inputSourceSet.getOutput().dir(Collections.singletonMap("builtBy", compileTask), compileDestDir);
            });
        });
    }
}
