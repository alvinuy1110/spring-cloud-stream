package com.myproject.custom.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding({Sink.class, Source.class})
public class CustomFileDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(CustomFileDemoApp.class, args);
    }

    @StreamListener(Sink.INPUT)
    @SendTo(Source.OUTPUT)
    public String handle(String message) {
        return String.format("Received: %s", message);
    }

}
