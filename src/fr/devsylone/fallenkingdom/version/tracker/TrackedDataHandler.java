package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.jetbrains.annotations.NotNull;

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
            HANDLER_TYPE = NMSUtils.nmsClass("DataWatcherSerializer");
            HANDLERS = NMSUtils.nmsClass("DataWatcherRegistry").getFields();
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    static final TrackedDataHandler<Byte> BYTE = of(Byte.class);
    static final TrackedDataHandler<Boolean> BOOLEAN = of(Boolean.class);
    static final TrackedDataHandler<String> STRING = of(String.class);
    static final TrackedDataHandler<Optional<String>> OPT_COMPONENT_FROM_STRING = ofOptional((Class<Object>) ChatMessage.CRAFT_CHAT_MESSAGE, ChatMessage::fromString);

    private final Object delegate;
    private final Function<T, Object> mapper;

    TrackedDataHandler(Object delegate, Function<T, Object> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    static <T> @NotNull TrackedDataHandler<T> of(Class<T> type) {
        return of(type, Function.identity());
    }

    static <T, U> @NotNull TrackedDataHandler<T> of(Class<U> type, Function<T, U> mapper) {
        for (Field field : HANDLERS) {
            Type fieldType = field.getGenericType();
            if (fieldType instanceof ParameterizedType && genericType(fieldType).isAssignableFrom(type)) {
                try {
                    return new TrackedDataHandler<>(field.get(null), (Function<T, Object>) mapper);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw new HookFailed(type.getSimpleName());
    }

    static <T> @NotNull TrackedDataHandler<Optional<T>> ofOptional(Class<T> optionalType) {
        return ofOptional(optionalType, Function.identity());
    }

    static <T, U> @NotNull TrackedDataHandler<Optional<T>> ofOptional(Class<U> optionalType, Function<T, U> mapper) {
        for (Field field : HANDLERS) {
            Type fieldType = field.getGenericType();
            if (!(fieldType instanceof ParameterizedType) || !genericType(fieldType).isAssignableFrom(Optional.class)) {
                continue;
            }
            Type handlerDataType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            if (genericType(handlerDataType).isAssignableFrom(optionalType)) {
                try {
                    return new TrackedDataHandler<>(field.get(null), opt -> opt.map(mapper));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw new HookFailed("Optional<" + optionalType.getSimpleName() + '>');
    }

    static Class<?> genericType(Type genericType) {
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
