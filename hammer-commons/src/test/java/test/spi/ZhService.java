package test.spi;

/**
 * @author Created by sqwen
 * Create at: 2021/07/28 01:04
 */
public class ZhService implements HelloService {
    @Override
    public String say() {
        return "你好";
    }
}
