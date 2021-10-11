package org.github.boot.threads;

import org.github.boot.threads.utils.NamedThreadFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 2021/10/10 11:33
 * veegn
 *
 * @author veegn.me@gmail.com
 */
@EnableConfigurationProperties(ExecutorServiceProperties.class)
@ConditionalOnProperty(prefix = ExecutorServiceProperties.PREFIX, name = "enable")
@Configuration
public class ExecutorServiceConfiguration implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ExecutorServiceProperties properties = getProperties();
        for (Map.Entry<String, ExecutorServiceProperties.ThreadPoolProperties> entry
                : properties.getPools().entrySet()) {
            ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(entry.getKey(), entry.getValue());
            register(registry, entry.getKey(), threadPoolExecutor);
        }
    }

    private void register(BeanDefinitionRegistry registry, String name, ThreadPoolExecutor threadPoolExecutor) {
        BeanDefinitionBuilder builder
                = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolExecutor.class, () -> threadPoolExecutor);
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        registry.registerBeanDefinition(name, definition);
    }

    private ThreadPoolExecutor createThreadPoolExecutor(String name, ExecutorServiceProperties.ThreadPoolProperties properties) {
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                properties.getUnit(),
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                new NamedThreadFactory(name),
                createRejectedPolicy(properties.getHandler()));
    }

    private RejectedExecutionHandler createRejectedPolicy(Class<? extends RejectedExecutionHandler> handlerClazz) {
        try {
            return handlerClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }


    private ExecutorServiceProperties getProperties() {
        Binder binder = Binder.get(environment);
        ConfigurationPropertyName propertyName = ConfigurationPropertyName.of(ExecutorServiceProperties.PREFIX);
        Bindable<ExecutorServiceProperties> target = Bindable.of(ExecutorServiceProperties.class);
        BindResult<ExecutorServiceProperties> result = binder.bind(propertyName, target);
        return result.get();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
