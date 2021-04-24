import net.kyori.indra.sonatypeSnapshots
import org.gradle.api.plugins.JavaPlugin.*

plugins {
    val indraVersion = "1.3.1"
    id("net.kyori.indra") version indraVersion apply false
    id("net.kyori.indra.checkstyle") version indraVersion apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
    idea
}

allprojects {
    group = "fr.devsylone"
    version = "3.0.0-SNAPSHOT"
    description = "A minecraft gamemode plugin"
}

plugins.apply("idea")

subprojects {
    plugins.apply("net.kyori.indra")

    repositories {
        mavenCentral()
        sonatypeSnapshots()

        // <editor-fold desc="Repositories" defaultstate="collapsed">
        // {{{ Repositories
        maven {
            name = "papermc"
            url = uri("https://papermc.io/repo/repository/maven-public/")

            content {
                includeGroup("com.destroystokyo.paper")
                includeGroup("io.papermc")
            }
        }
        // }}}
        // </editor-fold>

        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    dependencies {
        COMPILE_ONLY_API_CONFIGURATION_NAME("org.checkerframework", "checker-qual",  "3.9.1")
        TEST_IMPLEMENTATION_CONFIGURATION_NAME("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
        COMPILE_ONLY_API_CONFIGURATION_NAME("com.google.errorprone", "error_prone_annotations", "2.5.1")
        COMPILE_ONLY_API_CONFIGURATION_NAME("org.jetbrains", "annotations", "20.1.0")
    }
}
