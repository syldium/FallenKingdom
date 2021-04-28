import fr.devsylone.fallenkingdom.ADVENTURE_VER
import fr.devsylone.fallenkingdom.CONFIGURATE_VER
import fr.devsylone.fallenkingdom.PAPER_VER

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:${PAPER_VER}")
    compileOnly("net.kyori:adventure-api:${ADVENTURE_VER}")
    compileOnly("org.spongepowered:configurate-core:${CONFIGURATE_VER}")
}
