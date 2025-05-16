package org.example.dictionarybot.handler.income.action.command.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.neo4j.data.Dictionary;
import org.example.dictionarybot.neo4j.service.DictionaryService;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.data.Pagination;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;



import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyDictionariesCommandAction implements CommandAction {
    private final RedisService redisService;
    private final MessageProcessingHelper messageProcessingHelper;
    private final DictionaryService dictionaryService;

    private final int DEFAULT_PAGE_SIZE = 5;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {

        Map<String, String> params = messageProcessingHelper.getCommandParams(command);

        String currentPageParam = params.get(MessageProcessingHelper.PAGE_PARAM);
        Pagination<Dictionary> dictionaryPagination =
                dictionaryService.getOwnerDictionaries(
                        abstractUserMessageDto.getUserId(),
                        Integer.parseInt(currentPageParam),
                        DEFAULT_PAGE_SIZE);

        Set<MenuRowItemDto> items =
                dictionaryPagination.items().stream().map(
                d -> new MenuRowItemDto(
                                    CommonCommand.SET_DICT.getCommand() + "?" + MessageProcessingHelper.DICT_ID_PARAM + "=" + d.getPersistentId(),
                                    d.getName())
                ).collect(Collectors.toSet());

        Pagination<MenuRowItemDto> page = new Pagination<>(dictionaryPagination.pageSize(), dictionaryPagination.currentPage(), dictionaryPagination.totalPages(), items.stream().toList());


        return Optional.empty();
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.MY_DICTS;
    }
}
