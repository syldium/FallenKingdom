package fr.devsylone.fallenkingdom.commands.abstraction;

public class IntegerArgument extends Argument<Integer> {

    private final int minimum;
    private final int maximum;

    public IntegerArgument(String name, boolean required, String description, int minimum, int maximum) {
        super(name, required, description, int.class);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public IntegerArgument(String name, boolean required, String description, int minimum) {
        this(name, required, description, 0, Integer.MAX_VALUE);
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }
}
