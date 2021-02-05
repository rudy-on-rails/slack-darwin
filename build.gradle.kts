import java.util.Properties

val prop = Properties()
prop.load(file("src/main/resources/application.properties").reader())
version = prop["version"]!!

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("java")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.slack.api:slack-api-client:1.6.0")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "slackdarwin.AppKt"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
