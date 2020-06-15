package com.myproject.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@Configuration
@Profile("embedded")
public class KafkaEmbeddedBrokerConfig {

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1);

        embeddedKafkaBroker.kafkaPorts(9092);
        return embeddedKafkaBroker;

    }
}
