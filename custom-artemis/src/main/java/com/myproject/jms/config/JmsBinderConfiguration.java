package com.myproject.jms.config;


import com.myproject.jms.binder.JmsMessageChannelBinder;
import com.myproject.jms.binder.jms.utils.JmsMessageDrivenChannelAdapterFactory;
import com.myproject.jms.binder.jms.utils.JmsSendingMessageHandlerFactory;
import com.myproject.jms.provisioning.JmsProvisioner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;


/**
 * This is the spring config to use.  See META-INF spring.binders for registration
 */
@Configuration
@Import({JmsBinderGlobalConfiguration.class})

// TODO
@ConditionalOnClass({ConnectionFactory.class})
public class JmsBinderConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JmsProvisioner jmsProvisioner(ConnectionFactory connectionFactory) {
        return new JmsProvisioner(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public JmsMessageChannelBinder jmsMessageChannelBinder(JmsProvisioner provisioningProvider,
                                                           JmsSendingMessageHandlerFactory jmsSendingMessageHandlerFactory,
                                                           JmsMessageDrivenChannelAdapterFactory jmsMessageDrivenChannelAdapterFactory,
                                                           JmsTemplate jmsTemplate,
                                                           ConnectionFactory connectionFactory, JmsExtendedBindingProperties jmsExtendedBindingProperties) {
        JmsMessageChannelBinder jmsMessageChannelBinder= new JmsMessageChannelBinder(provisioningProvider, jmsSendingMessageHandlerFactory, jmsMessageDrivenChannelAdapterFactory, jmsTemplate, connectionFactory);

        jmsMessageChannelBinder.setExtendedBindingProperties(jmsExtendedBindingProperties);
        return jmsMessageChannelBinder;
    }

}