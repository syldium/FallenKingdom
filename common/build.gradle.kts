import fr.devsylone.fallenkingdom.ADVENTURE_VER
import fr.devsylone.fallenkingdom.CLOUD_VER

dependencies {
    compileOnly(project(":api"))
    testImplementation(project(":api"))

    implementation("cloud.commandframework:cloud-core:${CLOUD_VER}")
    implementation("net.kyori:adventure-api:${ADVENTURE_VER}")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}
