package cn.wensiqun.hammer.commons.spi;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class NameableSPITest {

    @Test
    public void test() {
        try {
            NameableSPI.Holder.get(NotFoundAnySPI.class, "SPI1");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().startsWith("[1]"));
        }
        MySuccessNameSPI spi1 = NameableSPI.Holder.get(MySuccessNameSPI.class, "SPI1");
        Assert.assertTrue(spi1 instanceof MySuccessNameSPI1);

        Map<String, ? extends MySuccessNameSPI> map = NameableSPI.Holder.getAll(MySuccessNameSPI.class);
        Assert.assertEquals(2, map.size());
    }

}
