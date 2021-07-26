package cn.wensiqun.hammer.commons.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public interface NameableConfigurable extends SPI {

    String getName();

    class Holder {

        private final static Map<Class, Map<String, NameableConfigurable>> spiResources = new ConcurrentHashMap<>();

        public static <T extends NameableConfigurable> T get(Class<T> spiClass, String name) {
            initSpi(spiClass);
            Map<String, NameableConfigurable> map = spiResources.get(spiClass);
            if (map == null) {
                throw new RuntimeException("[0]Not found any spi: " + spiClass.getSimpleName());
            }
            NameableConfigurable v = map.get(name);
            if (v == null) {
                throw new RuntimeException("[1]Not found spi " + spiClass.getSimpleName() + " by name " + name);
            }
            try {
                return (T) v;
            } catch (ClassCastException e) {
                throw new RuntimeException("[2]Illegal type of spi " + spiClass.getSimpleName() + " by name " + name, e);
            }
        }

        public static <T extends NameableConfigurable> Map<String, ? extends T> getAll(Class<T> spiClass) {
            initSpi(spiClass);
            return new HashMap<String, T>((Map<String, ? extends T>) spiResources.get(spiClass));
        }

        private synchronized static <T extends NameableConfigurable> void initSpi(Class<T> spiClass) {
            spiResources.computeIfAbsent(spiClass, Holder::loadSpi);
        }

        private static <T extends NameableConfigurable> Map<String, NameableConfigurable> loadSpi(Class<T> spiClass) {
            return SPIs.get(spiClass).stream().collect(Collectors.toMap(NameableConfigurable::getName, t -> t));
        }

    }

}
