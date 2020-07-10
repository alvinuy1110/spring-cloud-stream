package com.myproject.jms.config;


import com.myproject.jms.stream.MessageStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(MessageStream.class)
public class StreamConfig {
}
