package org.example.dictionarybot.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.config.RabbitConfig;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.handler.income.IncomeMessageHandler;
import org.example.dictionarybot.handler.outcome.OutcomeMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final IncomeMessageHandler incomeMessageHandler;
    private final OutcomeMessageHandler outcomeMessageHandler;

    @RabbitListener(queues = RabbitConfig.INCOME_QUERY)
    public void handleBotIncome(AbstractUserMessageDto dto) {
        incomeMessageHandler.handle(dto);
    }

    @RabbitListener(queues = RabbitConfig.OUTCOME_QUERY)
    public void handleBotOutcome(AbstractMessageDto dto) {
        outcomeMessageHandler.handle(dto);
    }
}
