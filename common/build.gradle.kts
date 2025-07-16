dependencies {
    compileOnly(project(":api"))
    testImplementation(project(":api"))

    implementation(libs.annotations)
    compileOnly(libs.adventureApi)
    implementation(libs.cloudCore)

    testImplementation(libs.adventureApi)
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
