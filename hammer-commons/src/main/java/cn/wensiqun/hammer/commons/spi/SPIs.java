package cn.wensiqun.hammer.commons.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;
import static java.security.AccessController.doPrivileged;

public class SPIs {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPIs.class);

    private static class ProviderAndClassloader<T extends SPI> {

        @Nonnull
        private ClassLoader classLoader;

        @Nonnull
        private T provider;

        public ProviderAndClassloader(@Nonnull ClassLoader classLoader, @Nonnull T provider) {
            this.classLoader = classLoader;
            this.provider = provider;
        }

        @Nonnull
        public ClassLoader getClassLoader() {
            return classLoader;
        }

        @Nonnull
        public T getProvider() {
            return provider;
        }
    }

    /**
     * Some time, only one implement allows in classpath runtime.
     *
     * @param spi
     * @param <T>
     * @return
     * @throws SpiException exception if number of implements more than 1 or empty.
     */
    @Nonnull
    public static <T extends SPI> T getOnly(@Nonnull Class<T> spi) throws SpiException {
        List<T> spis = get(spi, false);
        if (spis.isEmpty()) {
            throw new SpiException("Not found spi of " + spi.getSimpleName());
        }
        if (spis.size() > 1) {
            throw new SpiException("Found SPI " + spis.size() + " " + spi.getSimpleName() + ", but just support 1 when use getOnly method.");
        }
        return spis.get(0);
    }

    /**
     * Same to {@link #getOnly(Class) }, return null if not found any implement.
     *
     * @param spi
     * @param <T>
     * @return
     * @throws SpiException
     * @see #getOnly(Class)
     */
    @Nullable
    public static <T extends SPI> T getOnlyOrNull(@Nonnull Class<T> spi) throws SpiException {
        List<T> spis = get(spi, false);
        if (spis.isEmpty()) {
            return null;
        }
        if (spis.size() > 1) {
            throw new SpiException("Found SPI " + spis.size() + " " + spi.getSimpleName() + ", but just support 1 when use getOnly method.");
        }
        return spis.get(0);
    }

    /**
     * Get all implements according spi class.
     * @param spi
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T extends SPI> List<T> get(@Nonnull Class<T> spi) {
        return get(spi, false);
    }

    /**
     * Get all implements according spi class.
     * @param spi
     * @param allowDuplicated allow duplicated implement
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T extends SPI> List<T> get(@Nonnull Class<T> spi, boolean allowDuplicated) {
        List<ProviderAndClassloader<T>> providerAndClassloaders = new ArrayList<>();
        ClassLoader cl = doPrivileged((PrivilegedAction<ClassLoader>) () -> currentThread().getContextClassLoader());
        if (cl == null) {
            cl = SPIs.class.getClassLoader();
        }
        try {
            loadTo(providerAndClassloaders, cl, spi);
        } catch (Exception e) {
            LOGGER.warn("Unable load: " + spi.getName(), e);
        }
        Stream<ProviderAndClassloader<T>> stream;
        if (allowDuplicated) {
            stream = providerAndClassloaders.stream();
        } else {
            Map<String, ProviderAndClassloader<T>> providerMap = new HashMap<>();
            for (ProviderAndClassloader<T> providerAndClassloader : providerAndClassloaders) {
                T provider = providerAndClassloader.provider;
                ProviderAndClassloader<T> previous = providerMap.putIfAbsent(provider.getClass().getName(), providerAndClassloader);
                if (previous != null) {
                    LOGGER.info("Duplicated SPI {} found in different classloader {} and {}, using the first classloader.",
                            previous.provider.getClass().getName(),
                            previous.getClassLoader(),
                            providerAndClassloader.getClassLoader());
                }
            }
            stream = providerMap.values().stream();
        }
        return stream.map(ProviderAndClassloader::getProvider).collect(Collectors.toList());
    }

    private static <T extends SPI> void loadTo(List<ProviderAndClassloader<T>> providers, ClassLoader cl, Class<T> spiType) {
        if (cl == null) {
            return;
        }
        for (T spi : ServiceLoader.load(spiType, cl)) {
            providers.add(new ProviderAndClassloader<>(cl, spi));
        }
    }

    public static class SpiException extends Exception {

        public SpiException(String message) {
            super(message);
        }

    }
}
