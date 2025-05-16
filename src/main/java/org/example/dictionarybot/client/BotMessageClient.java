package org.example.dictionarybot.client;

import org.example.dictionarybot.config.RabbitConfig;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BotMessageClient {
    private final RabbitTemplate rabbitTemplate;

    public void sendIncomeMessage(@NonNull AbstractUserMessageDto msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.INCOME_QUERY, msg);
    }

    public void sendOutcomeMessage(@NonNull AbstractMessageDto msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.OUTCOME_QUERY, msg);
    }

}

