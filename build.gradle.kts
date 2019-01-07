plugins {
    id("java")
    id("groovy")
    id("java-gradle-plugin")
}

group = "io.vlingo"
version = "0.7.8"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val defaultVLingoVersion = "0.7.5"

val generatedResourcesDir = buildDir.resolve("generated-resources/versions/resources")
val generateVersionsResource by tasks.registering(WriteProperties::class) {
    property("default-vlingo-version", defaultVLingoVersion)
    outputFile = generatedResourcesDir.resolve("io/vlingo/gradle/versions.properties")
}
sourceSets.main {
    resources.srcDir(files(generatedResourcesDir).builtBy(generateVersionsResource))
}

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("io.vlingo:vlingo-actors:$defaultVLingoVersion")

    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.apache.commons:commons-io:1.3.2")
}

gradlePlugin {
    plugins {
        register("vlingo-codegen") {
            id = "io.vlingo.codegen"
            implementationClass = "io.vlingo.gradle.ActorProxyGeneratorPlugin"
        }
    }
}

tasks.test {
    inputs.dir("src/test/samples")
}
