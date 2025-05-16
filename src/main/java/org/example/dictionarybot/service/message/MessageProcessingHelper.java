package org.example.dictionarybot.service.message;


import lombok.RequiredArgsConstructor;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.webapp.WebAppLinkDto;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.data.Pagination;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.dictionarybot.handler.income.action.command.CommonCommand.*;

@Service
@RequiredArgsConstructor
public class MessageProcessingHelper {
    public static final String PAGE_PARAM = "p";
    public static final String QUERY_PARAM = "q";
    public static final String DICT_ID_PARAM = "dict_id";
    public static final String LEXEME_PARAM = "lexeme";
    private final PropertyService propertyService;

    public TelegramMessageMetadata getDefaultMessageMetadata(@Nullable String editMessageId,
                                                             @Nullable String pathCommand) {
        if (pathCommand == null || pathCommand.equals(START.getCommand()) ) {
            return new TelegramMessageMetadata(editMessageId, true, List.of(getReturnButtonRow()));
        }
        return new TelegramMessageMetadata(editMessageId, false, List.of(getReturnButtonRow()));
    }

    public MenuRowDto getReturnButton(String prevCommand) {
        return new MenuRowDto(List.of(new MenuRowItemDto(prevCommand, "Назад")));
    }

    public MenuRowDto getLexemeSensesButtonRow(String lexeme) {
        return new MenuRowDto(List.of(new MenuRowItemDto(GET_LEXEME_SENSES.getCommand() + "?" + LEXEME_PARAM + "=" + lexeme, propertyService.getMessageProperty("telegram.button.get_all_senses"))));
    }

    public MenuRowDto getLexemeTranslationsButtonRow(String lexeme) {
        return new MenuRowDto(List.of(new MenuRowItemDto(GET_TRANSLATIONS.getCommand() + "?" + LEXEME_PARAM + "=" + lexeme + "&" + PAGE_PARAM + "=" + "0", propertyService.getMessageProperty("telegram.button.get_all_translations"))));
    }

    public MenuRowDto getReturnButtonRow() {
        return new MenuRowDto(List.of(new MenuRowItemDto(START.getCommand(), propertyService.getMessageProperty("telegram.pagination.return"))));
    }

    public MenuRowDto getLexemeSearchButtonRow() {
        return new MenuRowDto(List.of(new MenuRowItemDto(FIND_MATCHING_LEXEMES.getCommand(), propertyService.getMessageProperty("telegram.button.find_lexeme"))));
    }

    public MenuRowDto getHelpButtonRow() {
        return new MenuRowDto(List.of(new MenuRowItemDto(HELP.getCommand(), propertyService.getMessageProperty("telegram.button.help"))));
    }

    public Optional<String> getCommand(String text) {
        String command = text.split("\\s")[0];
        if (command.length() > 1 && command.charAt(0) == '/') {
            return Optional.of(command);
        }
        return Optional.empty();
    }

    public boolean isCommand(String text) {
        return text.stripIndent().startsWith("/");
    }
    public String getPath(String command) {
        if (command.split("\\s")[0].equals(START.getCommand())) {
            return START.getCommand();
        }
        String formattedCommand = command.replaceAll("\\s+", "");
        if (formattedCommand.contains("?")) {
            return formattedCommand.substring(0, command.indexOf("?"));
        }
        return formattedCommand;
    }

    public Map<String, String> getCommandParams(@NonNull String command) {
        URI uri = URI.create(command.replaceAll("\\s+", ""));
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
            }
        }
        return queryPairs;
    }

    public MessageMenuDto getCommandNotRecognizedMsg(String chatId, String editableMsgId) {
        return new MessageMenuDto(
                chatId,
                MessageFormat.format(propertyService.getMessageProperty("command_not_recognized_msg"), START),
                List.of(getReturnButtonRow()),
                getDefaultMessageMetadata(editableMsgId, null)
        );
    }

    public AbstractMessageDto getErrorMessage(String chatId, TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageMenuDto(chatId,
                propertyService.getMessageProperty("something_wrong_message"),
                List.of(getReturnButtonRow()),
                defaultMessageMetadata
        );
    }

    // Bad practice btw
    // Прямое преобразование: заменяет любые пробелы на delimiter
    public String concatWith(String input, String delimiter) {
        return Arrays.stream(input.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(delimiter));
    }

    // Обратное преобразование: разбивает по delimiter
    public String splitFrom(String input, String delimiter) {
        return Arrays.stream(input.trim().split(Pattern.quote(delimiter)))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    public MessageMenuDto getPaginationMenu(String chatId, String pathCommand, boolean asSecondParam, String text, MenuRowDto backButton, TelegramMessageMetadata defaultMessageMetadata,  Pagination<MenuRowItemDto> page) {
        List<MenuRowDto> rows = new ArrayList<>(convertItemsToMenuRows(page.items().stream().toList()));

        rows.add(getPaginationControlRow(pathCommand, asSecondParam, null, page.currentPage(), page.totalPages()));
        rows.add(backButton);
        return new MessageMenuDto(
                chatId,
                text,
                rows,
                defaultMessageMetadata
        );
    }

    private List<MenuRowDto> convertItemsToMenuRows(@NonNull List<MenuRowItemDto> items) {
        return items.stream()
                .map(item -> new MenuRowDto(List.of(item)))
                .toList();
    }


    private MenuRowDto getPaginationControlRow(String pathCommand, boolean asSecondParam, String query, int page, int total) {
        List<MenuRowItemDto> items = new ArrayList<>();

        if ( page > 0  && total > 1) {
            items.add(getLeftArrowButton(pathCommand, asSecondParam, query, page));
        }

        // for correct interpretation in UI
        items.add(getCenterButton(page + 1, total));

        if ( page < total - 1 && total > 1) {
            items.add(getRightArrowButton(pathCommand, asSecondParam, query, page));
        }

        return new MenuRowDto(items);
    }

    private MenuRowItemDto getLeftArrowButton(String pathCommand, boolean asSecondParam, String query, int page) {
        String callbackCommand = "";
        if (asSecondParam) {
            callbackCommand = pathCommand + "&" + PAGE_PARAM + "=" + (page - 1);
        } else {
            callbackCommand = pathCommand + "?" + PAGE_PARAM + "=" + (page - 1);
        }
        if (query != null) {
            callbackCommand += "&" + QUERY_PARAM + "=" + query;
        }
        return new MenuRowItemDto(callbackCommand, propertyService.getMessageProperty("telegram.pagination.left"));
    }

    private MenuRowItemDto getRightArrowButton(String pathCommand, boolean asSecondParam, String query, int page) {
        String callbackCommand = "";
        if (asSecondParam) {
            callbackCommand = pathCommand + "&" + PAGE_PARAM + "=" + (page + 1);
        } else {
            callbackCommand = pathCommand + "?" + PAGE_PARAM + "=" + (page + 1);
        }
        if (query != null) {
            callbackCommand += "&" + QUERY_PARAM + "=" + query;
        }
        return new MenuRowItemDto(callbackCommand, propertyService.getMessageProperty("telegram.pagination.right"));
    }

    private MenuRowItemDto getCenterButton(Integer page, Integer total) {
        return new MenuRowItemDto("/nothing", page + "/" + total);
    }

}
