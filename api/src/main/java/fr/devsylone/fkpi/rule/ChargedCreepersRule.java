package fr.devsylone.fkpi.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.VisibleForTesting;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class ChargedCreepersRule {

    private final int spawnChance;
    private final int dropChance;
    private final int tntAmount;

    ChargedCreepersRule() {
        this(10, 50, 1);
    }

    private ChargedCreepersRule(int spawnChance, int dropChance, int tntAmount) {
        this.spawnChance = spawnChance;
        this.dropChance = dropChance;
        this.tntAmount = tntAmount;
    }

    public static @NotNull ChargedCreepersRule of(
            @Range(from = 0, to = 100) int spawnChance,
            @Range(from = 0, to = 100) int dropChance,
            @Range(from = 0, to = Integer.MAX_VALUE) int tntAmount
    ) {
        return new ChargedCreepersRule(
                checkPercentage(spawnChance, "spawn chance"),
                checkPercentage(dropChance, "drop chance"),
                checkAmount(tntAmount)
        );
    }

    public int spawnChance() {
        return this.spawnChance;
    }

    public int dropChance() {
        return this.dropChance;
    }

    public int tntAmount() {
        return this.tntAmount;
    }

    public @NotNull ChargedCreepersRule spawnChance(@Range(from = 0, to = 100) int value) {
        return new ChargedCreepersRule(checkPercentage(value, "spawn chance"), this.dropChance, this.tntAmount);
    }

    public @NotNull ChargedCreepersRule dropChance(@Range(from = 0, to = 100) int value) {
        return new ChargedCreepersRule(this.spawnChance, checkPercentage(value, "drop chance"), this.tntAmount);
    }

    public @NotNull ChargedCreepersRule tntAmount(@Range(from = 0, to = Integer.MAX_VALUE) int value) {
        return new ChargedCreepersRule(this.spawnChance, this.dropChance, checkAmount(value));
    }

    @VisibleForTesting
    static int checkPercentage(int value, String name) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(String.format("A percentage must be in [0-100] range (%s: %s).", name, value));
        }
        return value;
    }

    @VisibleForTesting
    static int checkAmount(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(String.format("The tnt drop must be strictly positive (got %s).", value));
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargedCreepersRule that = (ChargedCreepersRule) o;
        return this.spawnChance == that.spawnChance && this.dropChance == that.dropChance && this.tntAmount == that.tntAmount;
    }

    @Override
    public int hashCode() {
        return this.spawnChance | (this.dropChance << 8) | (this.tntAmount << 16);
    }

    @Override
    public String toString() {
        return "ChargedCreepersRule{" +
                "spawnChance=" + this.spawnChance +
                ", dropChance=" + this.dropChance +
                ", tntAmount=" + this.tntAmount +
                '}';
    }
}
