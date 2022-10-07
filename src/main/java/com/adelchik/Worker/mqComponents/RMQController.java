package com.adelchik.Worker.mqComponents;

import com.adelchik.Worker.services.TextProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RMQController {

    @Autowired
    private TextProcessingService textProcessingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RMQController.class);

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receivedMessage(String message) {

        LOGGER.info(String.format("Message received with id -> %s", message.substring(0, 36)));
        textProcessingService.processText(message);

    }
}
