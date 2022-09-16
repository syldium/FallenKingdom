package fr.devsylone.fallenkingdom.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

	private ZipUtils() {}

	public static void zipConfig(@NotNull Path configDirectory, @NotNull ZipOutputStream zipOut) throws IOException {
		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{yaml,yml}");
		Files.walkFileTree(configDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				return dir.equals(configDirectory) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!matcher.matches(file)) {
					return FileVisitResult.CONTINUE;
				}
				zipOut.putNextEntry(new ZipEntry(configDirectory.relativize(file).toString()));
				Files.copy(file, zipOut);
				zipOut.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
