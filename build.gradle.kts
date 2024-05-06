plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.firebirdsql.jdbc:jaybird:5.0.3.java8")
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("io.r2dbc:r2dbc-spi:1.0.0.RELEASE")
    implementation("org.hibernate:hibernate-entitymanager:5.6.15.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("com.opencsv:opencsv:5.9")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.17.0")
    testImplementation("com.h2database:h2:2.2.224")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}