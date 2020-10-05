package com.tesco.demo.infrastructure.kafkaReactor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;

@RunWith(MockitoJUnitRunner.class)
public class KafkaProducerConfigTest {
    @InjectMocks
    private KafkaProducerConfig kafkaProducerConfig;

    @Test
    public void test(){
        KafkaSender<String, String> kafka = kafkaProducerConfig.sender();
        Assert.assertNotNull(kafka);
    }
}
