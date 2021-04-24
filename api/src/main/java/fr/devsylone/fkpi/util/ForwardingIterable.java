package fr.devsylone.fkpi.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;

/**
 * An iterable that delegates the {@link #iterator()} and {@link #spliterator()} calls to a {@link Function}.
 *
 * @param <T> The iterable type.
 * @param <U> The intermediary type.
 * @param <V> The iterable destination type.
 */
public class ForwardingIterable<T extends Iterable<U>, U, V> implements Iterable<V> {

    private static final Function<Iterable<?>, Spliterator<?>> DEFAULT_SPLITERATOR = it -> Spliterators.spliteratorUnknownSize(it.iterator(), Spliterator.IMMUTABLE & Spliterator.NONNULL);

    private final T iterable;
    private final Function<T, Iterator<V>> toIterator;
    private final Function<T, Spliterator<V>> toSpliterator;

    @SuppressWarnings("unchecked")
    public ForwardingIterable(final @NotNull T iterable, final @NotNull Function<T, Iterator<V>> toIterator) {
        this(iterable, toIterator, (Function<T, Spliterator<V>>) ((Object) DEFAULT_SPLITERATOR));
    }

    public ForwardingIterable(
            final @NotNull T iterable,
            final @NotNull Function<T, Iterator<V>> toIterator,
            final @NotNull Function<T, Spliterator<V>> toSpliterator
    ) {
        this.iterable = iterable;
        this.toIterator = toIterator;
        this.toSpliterator = toSpliterator;
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return this.toIterator.apply(this.iterable);
    }

    @Override
    public @NotNull Spliterator<V> spliterator() {
        return this.toSpliterator.apply(this.iterable);
    }
}
