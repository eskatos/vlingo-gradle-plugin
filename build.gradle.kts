plugins {
    `kotlin-dsl`
}

group = "io.vlingo"
version = "0.7.8"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
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

tasks.test {
    inputs.dir("src/test/samples")
}
