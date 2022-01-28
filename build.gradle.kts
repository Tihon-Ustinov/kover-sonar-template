plugins {
    kotlin("multiplatform") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.5.0-RC2"
    id("org.sonarqube") version "3.3"
}

group = "org.rubicon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

/**-------------------- kover start settings --------------------*/
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:kover:0.5.0-RC2")
    }
}

apply(plugin = "kover")

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
    intellijEngineVersion.set("1.0.614")
    jacocoEngineVersion.set("0.8.7")
}
/**-------------------- kover end settings --------------------*/


/**-------------------- sonarqube start settings --------------------*/
sonarqube {
    properties {
        property("sonar.projectName", "ksonar")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", listOf(
            "src/commonMain/kotlin/",
            "src/jsMain/kotlin/",
            "src/jvmMain/kotlin/",
            "src/nativeMain/kotlin/"
        ).joinToString(","))
        property("sonar.tests", listOf(
            "src/commonTest/kotlin/",
            "src/jsTest/kotlin/",
            "src/jvmTest/kotlin/",
            "src/nativeTest/kotlin/"
        ).joinToString(","))
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.core.codeCoveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/kover/project-xml/report.xml")
    }
}

tasks.sonarqube {
    dependsOn(tasks.koverXmlReport)
}
/**-------------------- sonarqube end settings --------------------*/