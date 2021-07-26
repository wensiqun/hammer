package cn.wensiqun.hammer.collections;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Created by joe wen
 * Create at: 2021/07/26 23:53
 */
public class HammerCollections {

    private static final String UNMODIFIABLE_TYPE_PREFIX = "java.util.Collections$Unmodifiable";

    @Nonnull
    public static <T> Collection<T> unmodifiableCollection(@Nonnull Collection<T> source) {
        if (isUnmodifiableType(source)) {
            return source;
        }
        return Collections.unmodifiableCollection(source);
    }

    @Nonnull
    public static <T> List<T> unmodifiableList(@Nonnull List<T> source) {
        if (isUnmodifiableType(source)) {
            return source;
        }
        return Collections.unmodifiableList(source);
    }

    @Nonnull
    public static <T> Set<T> unmodifiableSet(@Nonnull Set<T> source) {
        if (isUnmodifiableType(source)) {
            return source;
        }
        return Collections.unmodifiableSet(source);
    }

    @Nonnull
    public static <T> SortedSet<T> unmodifiableSortedSet(@Nonnull SortedSet<T> source) {
        if (isUnmodifiableType(source)) {
            return source;
        }
        return Collections.unmodifiableSortedSet(source);
    }

    @Nonnull
    public static <T> NavigableSet<T> unmodifiableNavigableSet(@Nonnull NavigableSet<T> source) {
        if (isUnmodifiableType(source)) {
            return source;
        }
        return Collections.unmodifiableNavigableSet(source);
    }

    @Nonnull
    public static <K, V> Map<K, V> unmodifiableMap(@Nonnull Map<? extends K, ? extends V> source) {
        if (isUnmodifiableType(source)) {
            return (Map<K, V>) source;
        }
        return Collections.unmodifiableMap(source);
    }

    @Nonnull
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(@Nonnull SortedMap<K, ? extends V> source) {
        if (isUnmodifiableType(source)) {
            return (SortedMap<K, V>) source;
        }
        return Collections.unmodifiableSortedMap(source);
    }

    @Nonnull
    public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(@Nonnull NavigableMap<K, ? extends V> source) {
        if (isUnmodifiableType(source)) {
            return (NavigableMap<K, V>) source;
        }
        return Collections.unmodifiableNavigableMap(source);
    }

    public static <T> boolean isUnmodifiableType(@Nonnull T type) {
        return type.getClass().getName().startsWith(UNMODIFIABLE_TYPE_PREFIX);
    }

}
