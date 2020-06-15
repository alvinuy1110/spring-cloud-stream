package com.myproject.kafka.config;


import com.myproject.kafka.stream.GreetingsStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
@EnableBinding(GreetingsStream.class)
public class StreamConfig {
}
