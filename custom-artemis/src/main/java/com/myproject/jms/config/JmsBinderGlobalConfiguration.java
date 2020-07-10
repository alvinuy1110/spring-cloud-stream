package com.myproject.jms.config;

import com.myproject.jms.binder.jms.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@EnableConfigurationProperties(JmsExtendedBindingProperties.class)
public class JmsBinderGlobalConfiguration {


    // TODO what if there are multiple???
    @Autowired
    private ConnectionFactory connectionFactory;


    @Bean
    @ConditionalOnMissingBean(MessageRecoverer.class)
    MessageRecoverer defaultMessageRecoverer() throws Exception {
        return new RepublishMessageRecoverer(jmsTemplate(), new SpecCompliantJmsHeaderMapper());
    }

    @Bean
    ListenerContainerFactory listenerContainerFactory() throws Exception {
        return new ListenerContainerFactory(connectionFactory);
    }

    @Bean
    public JmsMessageDrivenChannelAdapterFactory jmsMessageDrivenChannelAdapterFactory(
            MessageRecoverer messageRecoverer, ListenerContainerFactory listenerContainerFactory) throws Exception {
        return new JmsMessageDrivenChannelAdapterFactory(listenerContainerFactory, messageRecoverer);
    }

    @Bean
    @ConditionalOnMissingBean(JmsSendingMessageHandlerFactory.class)
    public JmsSendingMessageHandlerFactory jmsSendingMessageHandlerFactory() throws Exception {
        return new JmsSendingMessageHandlerFactory(jmsTemplate(), new SpecCompliantJmsHeaderMapper());
    }

    @Bean
    @ConditionalOnMissingBean(JmsTemplate.class)
    public JmsTemplate jmsTemplate() throws Exception {
        return new JmsTemplate(connectionFactory);
    }
}
