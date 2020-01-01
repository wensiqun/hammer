package cn.wensiqun.hammer.reflect;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GenericTypeResolverTest {

    public interface GenericInterface<T> {
    }

    public static class AbstractGeneric<T0, T>
            implements GenericInterface<T> {
    }

    public static class IntegerGeneric
            implements GenericInterface<Integer> {
    }

    public static class LongGeneric
            implements GenericInterface<Long> {
    }

    public static class MapGeneric
            extends AbstractGeneric<List, Map> {
    }

    @Test
    public void test() {
        GenericTypeResolver resolver =
                new GenericTypeResolver(GenericInterface.class);
        assertEquals(Integer.class,
                resolver.resolve(IntegerGeneric.class).get(0));
        assertEquals(Long.class,
                resolver.resolve(LongGeneric.class).get(0));
        assertEquals(Map.class,
                resolver.resolve(MapGeneric.class).get(0));
    }

}
