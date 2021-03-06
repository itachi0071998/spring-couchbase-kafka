package com.tesco.demo.infrastructure.kafkaReactor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.kafka.receiver.KafkaReceiver;

@RunWith(MockitoJUnitRunner.class)
public class KafkaConsumerConfigTest {

    @InjectMocks
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Test
    public void test(){
        KafkaReceiver<String, String> kafka = kafkaConsumerConfig.receiver();
        Assert.assertNotNull(kafka);
    }
}
