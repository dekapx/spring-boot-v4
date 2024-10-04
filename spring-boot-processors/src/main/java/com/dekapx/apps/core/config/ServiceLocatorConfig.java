package com.dekapx.apps.core.config;

import com.dekapx.apps.core.processors.ProcessorFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceLocatorConfig {
    @Bean
    public ServiceLocatorFactoryBean serviceLocatorProcessorFactoryBean() {
        return getServiceLocatorFactoryBean(ProcessorFactory.class);
    }

    private ServiceLocatorFactoryBean getServiceLocatorFactoryBean(Class clazz) {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(clazz);
        return factoryBean;
    }
}
