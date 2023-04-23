package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.version.Version.VersionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gère une liste de métadonnées sur une entité pour les paquets.
 *
 * @see <a href="https://wiki.vg/Entity_metadata#Entity">wiki.vg</a>
 */
public class DataTracker {

    private static final Class<?> TRACKED_DATA_TYPE;
    private static final Constructor<?> TRACKED_DATA;
    private static final @Nullable Method SERIALIZE_TRACKED_DATA;
    private static final Class<?> TRACKED_ENTRY_TYPE;
    private static final Constructor<?> TRACKED_ENTRY;

    static {
        try {
            String package1_17 = "network.syncher";
            TRACKED_DATA_TYPE = NMSUtils.nmsClass(package1_17, "DataWatcherObject");
            TRACKED_DATA = TRACKED_DATA_TYPE.getConstructor(int.class, TrackedDataHandler.HANDLER_TYPE);
            TRACKED_ENTRY_TYPE = NMSUtils.nmsClass(package1_17, "DataWatcher$Item");
            TRACKED_ENTRY = TRACKED_ENTRY_TYPE.getConstructor(TRACKED_DATA_TYPE, Object.class);

            Method serialize = null;
            try {
                final Class<?> serializedType = NMSUtils.nmsClass(package1_17, "DataWatcher$b");
                serialize = Arrays.stream(TRACKED_ENTRY_TYPE.getMethods())
                        .filter(method -> method.getParameterCount() == 0 && serializedType.equals(method.getReturnType()))
                        .findFirst().orElseThrow(NoSuchMethodException::new);
            } catch (ReflectiveOperationException ignored) {} // < 1.19.3
            SERIALIZE_TRACKED_DATA = serialize;
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private final List<Object> trackedValues = new ArrayList<>(5);

    @Contract("-> this")
    public DataTracker invisible() {
        this.track(0, TrackedDataHandler.BYTE, (byte) 0x20);
        return this;
    }

    @Contract("_ -> this")
    public DataTracker customName(@Nullable String customName) {
        if (VersionType.V1_13.isHigherOrEqual()) {
            this.track(2, TrackedDataHandler.OPT_COMPONENT_FROM_STRING, Optional.ofNullable(customName));
        } else {
            this.track(2, TrackedDataHandler.STRING, customName);
        }
        return this;
    }

    @Contract("_ -> this")
    public DataTracker customNameVisible(boolean visible) {
        this.track(3, TrackedDataHandler.BOOLEAN, visible);
        return this;
    }

    @Contract("_ -> this")
    public DataTracker silent(boolean silent) {
        this.track(4, TrackedDataHandler.BOOLEAN, silent);
        return this;
    }

    /**
     * Instancie une nouvelle entrée pour la traquer.
     *
     * <p>Cette méthode utilise directement les classes DataWatcherObject et DataWatcher.Item</p>
     *
     * @param index L'index de la métadonnée
     * @param handler Ce qui va gérer et indiquer comment sérialiser la métadonnée
     * @param value La valeur à attribuer
     * @param <T> Le type de la métadonnée
     */
    private <T> void track(int index, TrackedDataHandler<T> handler, T value) {
        try {
            this.trackedValues.add(TRACKED_ENTRY.newInstance(TRACKED_DATA.newInstance(index, handler.delegate()), handler.map(value)));
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Liste les valeurs traquées.
     *
     * @return Une liste de DataWatcherObject.
     */
    public List<Object> trackedValues() {
        return this.trackedValues;
    }

    /**
     * Liste les valeurs traquées.
     *
     * @return Une liste de DataWatcherObject ou DataWatcher$SerializedEntry selon la version.
     */
    public List<Object> serializedTrackedValues() {
        if (SERIALIZE_TRACKED_DATA == null) {
            return this.trackedValues;
        }
        return this.trackedValues.stream()
                .map(value -> {
                    try {
                        return SERIALIZE_TRACKED_DATA.invoke(value);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
