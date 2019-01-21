plugins {
    `kotlin-dsl`
}

group = "io.vlingo"
version = "0"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("io.vlingo:vlingo-actors:0.7.9")
    compileOnly("io.vlingo:vlingo-common:0.7.9")

    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.apache.commons:commons-io:1.3.2")
}
