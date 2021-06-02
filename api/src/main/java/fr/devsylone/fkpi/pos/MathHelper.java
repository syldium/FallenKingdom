package fr.devsylone.fkpi.pos;

final class MathHelper {

    private MathHelper() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    static int square(int value) {
        return value * value;
    }

    static double square(double value) {
        return value * value;
    }

    static int floor(double value) {
        return (int) Math.floor(value);
    }
}
