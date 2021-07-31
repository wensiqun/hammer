package test.spi;

import java.util.ServiceLoader;

public class Bootstrap {
    public static void main(String[] args) {
        ServiceLoader<HelloService> serviceLoader = ServiceLoader.load(HelloService.class);
        for (HelloService helloService : serviceLoader) {
            System.out.println(helloService.say());
        }
        //Do other things
    }
}
