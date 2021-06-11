package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
class TrackedDataHandler<T> {

    static final Class<?> HANDLER_TYPE;
    private static final Field[] HANDLERS;

    static {
        try {
            String package1_17 = "network.syncher";
            HANDLER_TYPE = NMSUtils.nmsClass(package1_17, "DataWatcherSerializer");
            HANDLERS = NMSUtils.nmsClass(package1_17, "DataWatcherRegistry").getFields();
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    static final TrackedDataHandler<Byte> BYTE = need(Byte.class);
    static final TrackedDataHandler<Boolean> BOOLEAN = need(Boolean.class);
    static final TrackedDataHandler<String> STRING = need(String.class);
    static final TrackedDataHandler<Optional<String>> OPT_COMPONENT_FROM_STRING = findOptional((Class<Object>) ChatMessage.CHAT_BASE_COMPONENT, ChatMessage::fromString);

    private final Object delegate;
    private final Function<T, Object> mapper;

    private TrackedDataHandler(Object delegate, Function<T, Object> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    static <T> @NotNull TrackedDataHandler<T> need(Class<T> type) {
        return need(type, Function.identity());
    }

    static <T, U> @NotNull TrackedDataHandler<T> need(Class<U> type, Function<T, U> mapper) {
        TrackedDataHandler<T> handler = find(type, mapper);
        if (handler == null) {
            throw new HookFailed(type.getSimpleName());
        }
        return handler;
    }

    static <T, U> @NotNull TrackedDataHandler<Optional<T>> needOptional(Class<U> optionalType, Function<T, U> mapper) {
        TrackedDataHandler<Optional<T>> handler = findOptional(optionalType, mapper);
        if (handler == null) {
            throw new HookFailed("Optional<" + optionalType.getSimpleName() + '>');
        }
        return handler;
    }

    static <T> @Nullable TrackedDataHandler<T> find(Class<T> type) {
        return find(type, Function.identity());
    }

    static <T, U> @Nullable TrackedDataHandler<T> find(Class<U> type, Function<T, U> mapper) {
        for (Field field : HANDLERS) {
            Type serializerType = serializerType(field);
            if (serializerType != null && !(serializerType instanceof ParameterizedType) && ((Class<?>) serializerType).isAssignableFrom(type)) {
                try {
                    return new TrackedDataHandler<>(field.get(null), (Function<T, Object>) mapper);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    static <T> @Nullable TrackedDataHandler<Optional<T>> findOptional(Class<T> optionalType) {
        return findOptional(optionalType, Function.identity());
    }

    static <T, U> @Nullable TrackedDataHandler<Optional<T>> findOptional(Class<U> optionalType, Function<T, U> mapper) {
        for (Field field : HANDLERS) {
            Type serializerType = serializerType(field);
            if (serializerType instanceof ParameterizedType && genericType(serializerType).isAssignableFrom(optionalType)) {
                try {
                    return new TrackedDataHandler<>(field.get(null), opt -> opt.map(mapper));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Type serializerType(Field field) {
        Type fieldType = field.getGenericType();
        if (!(fieldType instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType pType = (ParameterizedType) fieldType;
        return pType.getActualTypeArguments()[0];
    }

    private static Class<?> genericType(Type genericType) {
        return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    Object delegate() {
        return this.delegate;
    }

    Object map(T value) {
        return this.mapper.apply(value);
    }

    private static class HookFailed extends RuntimeException {

        private static final long serialVersionUID = 8123977838158944863L;

        HookFailed(String type) {
            super("Unable to match a handler with the " + type + " type.");
        }
    }
}
