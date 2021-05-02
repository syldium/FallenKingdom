package fr.devsylone.fallenkingdom.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FkConfig extends YamlConfiguration {

    private static final Logger LOGGER = Logger.getLogger("FK");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
    private final File configFile;

    public FkConfig(@NotNull File configFile) {
        super();
        this.configFile = configFile;
    }

    public void load() {
        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LOGGER.log(Level.SEVERE, Messages.CONSOLE_UNABLE_TO_CREATE_PARENT_FOLDER.getMessage());
            }
        }
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    LOGGER.log(Level.SEVERE, Messages.CONSOLE_CANNOT_WRITE.getMessage() + " " + configFile + ".");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, Messages.CONSOLE_CANNOT_WRITE.getMessage() + " " + configFile + ".", e);
            }
        }

        try {
            super.load(configFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, Messages.CONSOLE_CANNOT_LOAD.getMessage() + " " + configFile + ".", e);
        } catch (InvalidConfigurationException e) {
            LOGGER.log(Level.SEVERE, Messages.CONSOLE_CANNOT_READ.getMessage() + " " + configFile + ".", e);
        }
    }

    public boolean delete() {
        return this.configFile.delete();
    }

    public void delayedSave() {
        String data = saveToString();
        if (!data.isEmpty()) {
            pendingDiskWrites.incrementAndGet();
            EXECUTOR_SERVICE.submit(new FileWrite(configFile, data, pendingDiskWrites));
        }
    }

    public static synchronized void awaitSaveEnd() {
        EXECUTOR_SERVICE.shutdown();
        try {
            if (!EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final class FileWrite implements Runnable {
        private final File file;
        private final String serialized;
        private final AtomicInteger pendingDiskWrites;

        private FileWrite(File file, String serialized, AtomicInteger pendingDiskWrites) {
            this.file = file;
            this.serialized = serialized;
            this.pendingDiskWrites = pendingDiskWrites;
        }

        @Override
        public void run() {
            synchronized (file) {
                if (pendingDiskWrites.get() > 1) {
                    pendingDiskWrites.decrementAndGet();
                    return;
                }

                try {
                    File parent = file.getCanonicalFile().getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        LOGGER.log(Level.SEVERE, Messages.CONSOLE_UNABLE_TO_CREATE_PARENT_FOLDER.getMessage());
                    }

                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                LOGGER.log(Level.SEVERE, Messages.CONSOLE_FAILED_TO_CREATE_FILE.getMessage() + " " + file);
                                return;
                            }
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, Messages.CONSOLE_FAILED_TO_CREATE_FILE.getMessage() + " " + file, ex);
                            return;
                        }
                    }

                    try (FileOutputStream fos = new FileOutputStream(file);
                         OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                        writer.write(serialized);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    pendingDiskWrites.decrementAndGet();
                }
            }
        }
    }
}
