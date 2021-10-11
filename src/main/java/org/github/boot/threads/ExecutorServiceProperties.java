package org.github.boot.threads;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 2021/10/10 11:38
 * veegn
 *
 * @author veegn.me@gmail.com
 */
@Data
@ConfigurationProperties(prefix = ExecutorServiceProperties.PREFIX)
public class ExecutorServiceProperties {

    transient static final String PREFIX = "executor.service";

    private Boolean enable = false;

    private Map<String, ThreadPoolProperties> pools;

    @Data
    public static class ThreadPoolProperties {
        private Integer corePoolSize = 2;
        private Integer maximumPoolSize = 4;
        private Long keepAliveTime = 10L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private Integer queueCapacity = 128;
        private Class<? extends RejectedExecutionHandler> handler = ThreadPoolExecutor.AbortPolicy.class;
    }
}
