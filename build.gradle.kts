plugins {
    id("java")
}

group = "org.seneca"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.azure", "azure-messaging-servicebus", "7.13.0")

    //lombok
    val lombokVersion = "1.18.24"
    compileOnly("org.projectlombok", "lombok", lombokVersion)
    annotationProcessor("org.projectlombok", "lombok", lombokVersion)

    //log4j
    implementation("org.apache.logging.log4j", "log4j-api", ":2.19.0")
    implementation("org.apache.logging.log4j", "log4j-core", "2.19.0")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.19.0")

    //vavr
    implementation("io.vavr", "vavr", "0.10.4")

    //junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    //await
    testImplementation("org.awaitility", "awaitility", "4.2.0")

    testImplementation("org.assertj","assertj-core","3.6.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}