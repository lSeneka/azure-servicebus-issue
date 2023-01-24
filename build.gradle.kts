plugins {
    java
    groovy
    idea
}

group = "org.seneca"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
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

    //spock
    testImplementation("org.spockframework","spock-core","2.3-groovy-4.0")
    testImplementation("org.apache.groovy","groovy-all", "4.0.7")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}