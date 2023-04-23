package fr.devsylone.fallenkingdom.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PluginLog {

    private static final Pattern IPV4_PATTERN = Pattern.compile("((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}");
    private static final Pattern IPV6_PATTERN = Pattern.compile("(?<!([0-9]|-|\\w))(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}(?!([0-9]|-|\\w))", Pattern.CASE_INSENSITIVE);

    private StringBuilder content;

    public PluginLog() {
        this.content = new StringBuilder();
    }

    public PluginLog(@NotNull String content) {
        this.content = new StringBuilder(content);
    }

    public void addFileContent(Path path) throws IOException {
        try (final InputStream stream = Files.newInputStream(path);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            this.content.append('\n');
            this.content.append('\n');
            this.content.append('\n');
            this.content.append("----------------------------------------------------------\n");
            this.content.append("           ").append(path.getFileName()).append('\n');
            this.content.append("----------------------------------------------------------\n");
            this.content.append(reader.lines().collect(Collectors.joining("\n")));
        }
    }

    public static @NotNull String anonymize(@NotNull String content) {
        final PluginLog log = new PluginLog(content);
        log.filter();
        return log.content.toString();
    }

    private void filter() {
        filterIPv4();
        filterIPv6();
    }

    private void filterIPv4() {
        final Matcher matcher = IPV4_PATTERN.matcher(this.content);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // Java 9 would take a StringBuilder instead of a StringBuffer
            matcher.appendReplacement(sb, "**.**.**.**");
        }
        matcher.appendTail(sb);
        this.content = new StringBuilder(sb);
    }

    private void filterIPv6() {
        final Matcher matcher = IPV6_PATTERN.matcher(this.content);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "****:****:****:****:****:****:****:****");
        }
        matcher.appendTail(sb);
        this.content = new StringBuilder(sb);
    }

    public String getContent() {
        filter();
        return this.content.toString();
    }

    public void addLine(String line) {
        this.content.append(line);
        this.content.append('\n');
    }
}
