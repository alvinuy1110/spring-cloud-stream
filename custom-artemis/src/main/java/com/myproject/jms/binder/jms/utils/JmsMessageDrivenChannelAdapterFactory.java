package com.myproject.jms.binder.jms.utils;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import com.myproject.jms.config.JmsConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.dsl.MessageChannels;
//import org.springframework.integration.dsl.jms.JmsMessageDrivenChannelAdapter;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.jms.dsl.JmsMessageDrivenChannelAdapterSpec;
import org.springframework.integration.jms.ChannelPublishingJmsMessageListener;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * Component responsible of building up endpoint required to bind consumers.
 */
@Slf4j
public class JmsMessageDrivenChannelAdapterFactory implements ApplicationContextAware {

    private final ListenerContainerFactory listenerContainerFactory;

    private final MessageRecoverer messageRecoverer;

    private GenericApplicationContext applicationContext;

    public JmsMessageDrivenChannelAdapterFactory(ListenerContainerFactory listenerContainerFactory,
                                                 MessageRecoverer messageRecoverer) {
        this.listenerContainerFactory = listenerContainerFactory;
        this.messageRecoverer = messageRecoverer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.isInstanceOf(GenericApplicationContext.class, applicationContext);
        this.applicationContext = (GenericApplicationContext)applicationContext;
    }
// Old pre spring integration 5.0
//    public JmsMessageDrivenChannelAdapter build(Queue destination,
//                                                final ExtendedConsumerProperties<JmsConsumerProperties> properties) {
//        RetryingChannelPublishingJmsMessageListener listener = new RetryingChannelPublishingJmsMessageListener(
//                properties, messageRecoverer, properties.getExtension().getDlqName());
//        listener.setBeanFactory(this.beanFactory);
//        JmsMessageDrivenChannelAdapter adapter = new JmsMessageDrivenChannelAdapter(
//                listenerContainerFactory.build(destination), listener);
//        adapter.setApplicationContext(this.applicationContext);
//        adapter.setBeanFactory(this.beanFactory);
//
////        Jms.messageDrivenChannelAdapter(
////                Jms.container(this.jmsConnectionFactory, "containerSpecDestination")
////                        .pubSubDomain(false)
////                        .taskExecutor(Executors.newCachedThreadPool())
////                        .get()))
////      .transform(String::trim)
////                ;
//        JmsMessageDrivenChannelAdapterSpec j=
//        Jms.messageDrivenChannelAdapter( listenerContainerFactory.build(destination));
//
//        adapter = j;
//        return adapter;
//    }


    protected ConfigurableListableBeanFactory getBeanFactory() {
        return this.applicationContext.getBeanFactory();
    }
    public JmsMessageDrivenChannelAdapterSpec build(Queue destination,
                                                final ExtendedConsumerProperties<JmsConsumerProperties> properties) {
        RetryingChannelPublishingJmsMessageListener listener = new RetryingChannelPublishingJmsMessageListener(
                properties, messageRecoverer, properties.getExtension().getDlqName());
        listener.setBeanFactory(this.getBeanFactory());
//        JmsMessageDrivenChannelAdapter adapter = new JmsMessageDrivenChannelAdapter(
//                listenerContainerFactory.build(destination), listener);
//        adapter.setApplicationContext(this.applicationContext);
//        adapter.setBeanFactory(this.beanFactory);

//        Jms.messageDrivenChannelAdapter(
//                Jms.container(this.jmsConnectionFactory, "containerSpecDestination")
//                        .pubSubDomain(false)
//                        .taskExecutor(Executors.newCachedThreadPool())
//                        .get()))
//      .transform(String::trim)
//                ;

        AbstractMessageListenerContainer abstractMessageListenerContainer  = listenerContainerFactory.build(destination);
        abstractMessageListenerContainer.setMessageListener(listener);
        JmsMessageDrivenChannelAdapterSpec j=
                Jms.messageDrivenChannelAdapter( abstractMessageListenerContainer);
        j.setBeanFactory(this.getBeanFactory());

        // TODO somehow the output channel is not being assigned by the binder!!
        // TODO hard-coding just for test
j.outputChannel("custom-artemis.greetings.out");
//        adapter = j;
        return j;
    }

    private static class RetryingChannelPublishingJmsMessageListener extends ChannelPublishingJmsMessageListener {

        private final String RETRY_CONTEXT_MESSAGE_ATTRIBUTE = "message";

        private final ConsumerProperties properties;

        private final MessageRecoverer messageRecoverer;

        private final String deadLetterQueueName;

        RetryingChannelPublishingJmsMessageListener(ConsumerProperties properties, MessageRecoverer messageRecoverer, String deadLetterQueueName) {
            this.properties = properties;
            this.messageRecoverer = messageRecoverer;
            this.deadLetterQueueName = deadLetterQueueName;
        }

        @Override
        public void onMessage(final Message jmsMessage, final Session session) throws JMSException {
            getRetryTemplate(properties).execute(
                    new RetryCallback<Object, JMSException>() {

                        @Override
                        public Object doWithRetry(RetryContext retryContext) throws JMSException {
                            try {
                                retryContext.setAttribute(RETRY_CONTEXT_MESSAGE_ATTRIBUTE, jmsMessage);
                                RetryingChannelPublishingJmsMessageListener.super.onMessage(jmsMessage, session);
                            }
                            catch (JMSException e) {
                                log.error("Failed to send message",
                                        e);
                                resetMessageIfRequired(jmsMessage);
                                throw new RuntimeException(e);
                            }
                            catch (Exception e) {
                                resetMessageIfRequired(jmsMessage);
                                throw e;
                            }
                            return null;
                        }

                    },
                    new RecoveryCallback<Object>() {

                        @Override
                        public Object recover(RetryContext retryContext) throws Exception {
                            if (messageRecoverer != null) {
                                Message message = (Message) retryContext.getAttribute(RETRY_CONTEXT_MESSAGE_ATTRIBUTE);
                                messageRecoverer.recover(message, deadLetterQueueName, retryContext.getLastThrowable());
                            }
                            else {
                                log.warn("No message recoverer was configured. Messages will be discarded.");
                            }
                            return null;
                        }

                    }
            );
        }

        private void resetMessageIfRequired(Message jmsMessage) throws JMSException {
            if (jmsMessage instanceof BytesMessage) {
                BytesMessage message = (BytesMessage) jmsMessage;
                message.reset();
            }
        }

        private RetryTemplate getRetryTemplate(ConsumerProperties properties) {
            RetryTemplate template = new RetryTemplate();
            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
            retryPolicy.setMaxAttempts(properties.getMaxAttempts());
            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(properties.getBackOffInitialInterval());
            backOffPolicy.setMultiplier(properties.getBackOffMultiplier());
            backOffPolicy.setMaxInterval(properties.getBackOffMaxInterval());
            template.setRetryPolicy(retryPolicy);
            template.setBackOffPolicy(backOffPolicy);
            return template;
        }

    }

}
