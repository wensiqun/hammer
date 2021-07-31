package cn.wensiqun.hammer.commons.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Defined a spi that support name.
 */
public interface NameableSPI extends SPI {

    String getName();

    class Holder {

        private final static Map<Class, Map<String, NameableSPI>> spiResources = new ConcurrentHashMap<>();

        /**
         * Get spi implements according spi class and name
         * @param spiClass
         * @param name
         * @param <T>
         * @return
         */
        public static <T extends NameableSPI> T get(Class<T> spiClass, String name) {
            initSpi(spiClass);
            Map<String, NameableSPI> map = spiResources.get(spiClass);
            if (map == null) {
                throw new RuntimeException("[0]Not found any spi: " + spiClass.getSimpleName());
            }
            NameableSPI v = map.get(name);
            if (v == null) {
                throw new RuntimeException("[1]Not found spi " + spiClass.getSimpleName() + " by name " + name);
            }
            try {
                return (T) v;
            } catch (ClassCastException e) {
                throw new RuntimeException("[2]Illegal type of spi " + spiClass.getSimpleName() + " by name " + name, e);
            }
        }

        /**
         * Get all spi implements according to spi class.
         * @param spiClass
         * @param <T>
         * @return
         */
        public static <T extends NameableSPI> Map<String, ? extends T> getAll(Class<T> spiClass) {
            initSpi(spiClass);
            return new HashMap<String, T>((Map<String, ? extends T>) spiResources.get(spiClass));
        }

        private synchronized static <T extends NameableSPI> void initSpi(Class<T> spiClass) {
            spiResources.computeIfAbsent(spiClass, Holder::loadSpi);
        }

        private static <T extends NameableSPI> Map<String, NameableSPI> loadSpi(Class<T> spiClass) {
            return SPIs.get(spiClass).stream().collect(Collectors.toMap(NameableSPI::getName, t -> t));
        }

    }

}
