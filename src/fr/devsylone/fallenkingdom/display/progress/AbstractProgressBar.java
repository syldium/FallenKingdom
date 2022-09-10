package fr.devsylone.fallenkingdom.display.progress;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.util.Color;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Implémentation du formatage commun à toutes les barres de progression.
 */
abstract class AbstractProgressBar implements ProgressBar {

    private final ProviderImpl provider;

    AbstractProgressBar(@NotNull ProviderImpl provider) {
        this.provider = provider;
    }

    /**
     * Formate le texte avec la progression.
     *
     * @param progress Progression
     * @return Texte avec les placeholders remplacés
     */
    protected @NotNull String formatText(double progress) {
        final String text = this.provider.text.replace("{PROGRESS}", ProgressBar.percent(progress));
        if (this.provider.totalBars == 0) {
            return text;
        }
        return text.substring(0, this.provider.barsStart)
                + this.progressBar('|', progress)
                + text.substring(this.provider.barsEnd + 1);
    }

    protected @NotNull String progressBar(char bar, double progress) {
        final int completedBars = (int) (this.provider.totalBars * progress);
        return this.provider.completedColor + repeat(bar, completedBars)
                + this.provider.notCompletedColor + repeat(bar, this.provider.totalBars - completedBars);
    }

    private static @NotNull String repeat(char c, int repeat) {
        final char[] chars = new char[repeat];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    static abstract class ProviderImpl implements ProgressBar.Provider {

        protected static final String TYPE = "type";
        protected static final String TEXT = "text";

        protected static final String ACTIONBAR = "actionbar";
        protected static final String BOSSBAR = "bossbar";
        protected static final String HOLOGRAM = "hologram";

        private final String text;

        private final int barsStart;
        private final int barsEnd;
        private final int totalBars;
        private final ChatColor completedColor;
        private final ChatColor notCompletedColor;

        public ProviderImpl(@NotNull ConfigurationSection config) {
            this(config.getString("text", "{PROGRESS}%"));
        }

        public ProviderImpl(@NotNull String text) {
            this.text = text;
            this.barsStart = text.indexOf("{BARS");
            this.barsEnd = text.indexOf('}', this.barsStart);
            if (this.barsStart != -1 && this.barsEnd != -1) {
                final String[] params = text.substring(this.barsStart, this.barsEnd).split(":");
                this.totalBars = params.length > 1 ? ArgumentParser.parsePositiveInt(params[1], false, Messages.CMD_ERROR_POSITIVE_INT) : 10;
                this.completedColor = params.length > 2 ? Color.of(params[2]).getChatColor() : ChatColor.BLUE;
                this.notCompletedColor = params.length > 3 ? Color.of(params[3]).getChatColor() : ChatColor.GRAY;
            } else {
                this.totalBars = 0;
                this.completedColor = ChatColor.RESET;
                this.notCompletedColor = ChatColor.RESET;
            }
        }

        @Override
        @MustBeInvokedByOverriders
        public void save(@NotNull ConfigurationSection config) {
            config.set(TYPE, this.type());
            config.set(TEXT, this.text);
        }

        /**
         * Retourne le type de la barre de progression.
         *
         * @return Le type pour sauvegarder la configuration
         */
        abstract @NotNull String type();
    }
}
