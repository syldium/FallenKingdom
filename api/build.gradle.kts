java {
    disableAutoTargetJvm()
}

dependencies {
    compileOnly(libs.paperApi)
    compileOnly(libs.configurateCore)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
