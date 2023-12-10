package fr.devsylone.fallenkingdom.updater;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PluginVersion implements Comparable<PluginVersion> {

    private final boolean release;
    private final int[] version;

    public PluginVersion(@NotNull String version) {
        final int v = !version.isEmpty() && version.charAt(0) == 'v' ? 1 : 0;
        final int sep = version.indexOf('-');
        final String numbers = sep < 0 ? version.substring(v) : version.substring(v, sep);
        this.release = sep == -1;
        this.version = Arrays.stream(numbers.split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public PluginVersion(int... version) {
        this.release = true;
        this.version = version;
    }

    public boolean isRelease() {
        return this.release;
    }

    @Override
    public int compareTo(@NotNull PluginVersion pluginVersion) {
        final int length = Math.max(this.version.length, pluginVersion.version.length);
        for (int i = 0; i < length; i++) {
            final int diff = Integer.compare(
                    this.version.length > i ? this.version[i] : 0,
                    pluginVersion.version.length > i ? pluginVersion.version[i] : 0
            );
            if (diff != 0) {
                return diff;
            }
        }
        return Boolean.compare(this.release, pluginVersion.release);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginVersion)) return false;
        return this.compareTo((PluginVersion) o) == 0;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(this.release);
        result = 31 * result + Arrays.hashCode(this.version);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.version.length; i++) {
            builder.append(this.version[i]);
            if (i != this.version.length - 1) {
                builder.append('.');
            }
        }
        if (!this.release) {
            builder.append("-SNAPSHOT");
        }
        return builder.toString();
    }
}
