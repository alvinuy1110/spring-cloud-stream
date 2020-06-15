# Custom File Binder

This is to demonstrate using a custom file binder

## Steps
### Create BindingProvider

First, create a BindingProvider.  This is responsible for defining the integration either as a Producer or Consumer

### Create MessageProducer

This is the consumer side that produces messages.

### Create Binder

This uses the BindinProvider and MessageProducer

### Create Spring Configuration

1. Create the Spring Configuration class
1. Create the binder hook

```
under src/main/resources/META-INF/spring.binders file with the binder name followed by the qualified name of the binder’s Spring Configuration:
myFileBinder:com.myproject.custom.file.config.FileMessageBinderConfiguration
```

## Testing

You setup an app and import this library.  

For this demo, the app and library are in one module. This is NOT recommended for actual deployment.

### Configuration

#### Application Properties

Example: Single Binder

```
spring:
  cloud:
    stream:
      bindings:
        
        input:
          # as per binder, where the input file is
          destination: input/input.txt
#          group: dpp-local

          # must match a binder alias
          binder: file
        output:
          # as per binder, where the target output file
          destination: target/output.txt
          # must match a binder alias
          binder: file
    binders:
      # this is just an alias assigned
      file:
        # this value must match the name set in spring.binders
        type: myFileBinder

logging:
  level:
    org.springframework.cloud.stream.messaging.DirectWithAttributesChannel: DEBUG
```

* See https://docs.spring.io/spring-cloud-stream/docs/Brooklyn.RELEASE/reference/html/_binders.html#_binder_configuration_properties

#### More Examples:

see KafkaExtendedBindingProperties and https://cloud.spring.io/spring-cloud-stream-binder-kafka/spring-cloud-stream-binder-kafka.html
