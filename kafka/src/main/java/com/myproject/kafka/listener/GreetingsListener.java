package com.myproject.kafka.listener;


import com.myproject.kafka.domain.Greetings;
import com.myproject.kafka.stream.GreetingsStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class GreetingsListener {
    @StreamListener(GreetingsStream.INPUT)
    public void handleGreetings(@Payload Greetings greetings) {
        log.info("Received greetings: {}", greetings);
    }
}
