package fr.devsylone.fallenkingdom.version.tag;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.papermc.paper.registry.RegistryAccess.registryAccess;
import static net.kyori.adventure.key.Key.key;

public class PaperTagTest<T extends org.bukkit.Keyed> implements Predicate<T> {
    private final Registry<T> registry;
    private final RegistryKey<T> registryKey;
    private final List<TagKey<T>> tags;

    public PaperTagTest(RegistryKey<T> registryKey, TagKey<T> ...tags) {
        this.registry = registryAccess().getRegistry(registryKey);
        this.registryKey = registryKey;
        this.tags = Arrays.asList(tags);
    }

    public PaperTagTest(RegistryKey<T> registryKey, String ...tags) {
        this.registry = registryAccess().getRegistry(registryKey);
        this.registryKey = registryKey;
        this.tags = Arrays.stream(tags)
                .map(tag -> TagKey.create(registryKey, key(tag)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean test(T element) {
        final TypedKey<T> typedKey = this.registryKey.typedKey(element.key());
        for (TagKey<T> tag : tags) {
            try {
                if (this.registry.getTag(tag).contains(typedKey)) {
                    return true;
                }
            } catch (NoSuchElementException ignored) {}
        }
        return false;
    }
}
