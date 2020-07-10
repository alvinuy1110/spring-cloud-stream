# Custom JMS Binder

## Reference

see https://github.com/spring-cloud/spring-cloud-stream-binder-jms/tree/master/spring-cloud-stream-binder-jms-activemq


## Steps
### Create the Properties

JmsConsumerProperties
JmsProducerProperties
JmsBindingProperties
JmsExtendedBindingProperties

### Create BindingProvider

create a ProvisioningProvider
  see KafkaTopicProvisioner
  

Create a BindingProvider.  This is responsible for defining the integration either as a Producer or Consumer.
This will use the BindingProperties to instantiate Producer/ Consumer.


### Create MessageProducer

This is the consumer side that produces messages.

### Create Binder

see KafkaMessageChannelBinder

-- createConsumerEndpoint
-- createProducerMessageHandler

This uses the BindingProvider and MessageProducer

### Create Spring Configuration
 TODO continue here???
 
 then add an app (DONE)
 start broker; (DONE)
 test it out
 
 
 The producer jms endpoint is created.  Getting error
 
 ```
Dispatcher has no subscribers for channel 'application.greetings-in'.; nested exception is org.springframework.integration.MessageDispatchingException: Dispatcher has no subscribers, failedMessage=GenericMessage [payload=hello there, headers={jms_redelivered=true, JMSXDeliveryCount=8, jms_destination=ActiveMQQueue[greetings-in], id=1b71bf06-af3c-34f6-7f52-cb390dff67df, priority=0, jms_timestamp=1594331359847, contentType=application/json, timestamp=1594331359884}]
```


The channel names are follow the syntax:

<spring_application_name>.<destination>
