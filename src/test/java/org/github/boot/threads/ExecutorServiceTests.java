package org.github.boot.threads;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest(classes = {ExecutorServiceConfiguration.class, ExecutorServiceProperties.class})
class ExecutorServiceTests {


    @Resource(name = "test1")
    private ThreadPoolExecutor thread1;

    @Resource(name = "test2")
    private ThreadPoolExecutor thread2;

    @Test
    public void testThreadConfig() {
        thread1.submit(() -> {
            System.out.println("111");
            return null;
        });
        thread2.submit(() -> System.out.println("222"));
        System.out.println("test1 pool core size:" + thread1.getCorePoolSize());
        System.out.println("test2 pool core size:" + thread2.getCorePoolSize());

    }

}
