package com.myproject.jms.binder.jms.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.jms.JmsHeaderMapper;
import org.springframework.jms.core.JmsTemplate;

/**
 * Factory to create JMS message handlers
 *
 */
public class JmsSendingMessageHandlerFactory implements ApplicationContextAware, BeanFactoryAware {

    private final JmsTemplate template;

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    private final JmsHeaderMapper headerMapper;

    public JmsSendingMessageHandlerFactory(JmsTemplate template,
                                           JmsHeaderMapper headerMapper) {
        this.template = template;
        this.headerMapper = headerMapper;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public JmsSendingMessageHandler build(String destinationName) {
        JmsSendingMessageHandler handler =  new JmsSendingMessageHandler(this.template, headerMapper, destinationName);
        handler.setApplicationContext(this.applicationContext);
        handler.setBeanFactory(this.beanFactory);
        handler.afterPropertiesSet();
        return handler;
    }
//    public PartitionAwareJmsSendingMessageHandler build(TopicPartitionRegistrar destinations) {
//        template.setPubSubDomain(true);
//        PartitionAwareJmsSendingMessageHandler handler = new PartitionAwareJmsSendingMessageHandler(
//                this.template,
//                destinations,
//                headerMapper);
//        handler.setApplicationContext(this.applicationContext);
//        handler.setBeanFactory(this.beanFactory);
//        handler.afterPropertiesSet();
//        return handler;
//    }

}