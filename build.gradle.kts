/*
 * Copyright (C) 2020  Rosetta Roberts <rosettafroberts@gmail.com>
 *
 * This file is part of VettingBot.
 *
 * VettingBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VettingBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VettingBot.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val kotlinVer = "1.4.21"
val springVer = "2.4.1"
plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("com.github.ben-manes.versions") version "0.36.0"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    id("com.google.cloud.tools.jib") version "2.7.0"
}

group = "vettingbot"
version = "1.0.2-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { setUrl("https://repo.spring.io/milestone") }
    mavenCentral()
    jcenter()
}

dependencies {

    //Kotlin deps
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVer")

    //Spring deps
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springVer")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j:$springVer")
    implementation("org.springframework.boot:spring-boot-starter-logging:$springVer")


    implementation("net.bytebuddy:byte-buddy-agent:1.10.15")
    implementation("com.discord4j:discord4j-core:3.2.0-M3")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.1")
    implementation("org.liquigraph:liquigraph-core:4.0.2")
    implementation("io.github.microutils:kotlin-logging:2.0.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVer") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.kotest:kotest-runner-junit5:4.3.2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:4.3.2") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property:4.3.2") // for kotest property test
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xinline-classes", "-Xopt-in=kotlin.ExperimentalUnsignedTypes")
        jvmTarget = "11"
        languageVersion = "1.4"
    }
}

jib {
    to {
        image = "thenumeralone/vettingbot:$version"
        tags = setOf(
            if ((version as String).endsWith("SNAPSHOT")) {
                "latest-snapshot"
            } else {
                "latest"
            }
        )
        val usr = findProperty("dockerUsername") as? String
        val pswd = findProperty("dockerPassword") as? String
        if (usr != null && pswd != null) {
            auth {
                username = usr
                password = pswd
            }
        }
    }
}
