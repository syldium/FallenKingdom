plugins {
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion apply false
    id("net.kyori.indra.checkstyle") version indraVersion apply false
    id("com.gradleup.shadow") version "9.0.0-rc1" apply false
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

        // <editor-fold desc="Repositories" defaultstate="collapsed">
        // {{{ Repositories
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        // }}}
        // </editor-fold>

        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}
