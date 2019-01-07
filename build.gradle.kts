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

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("io.vlingo:vlingo-actors:0.7.5")

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
