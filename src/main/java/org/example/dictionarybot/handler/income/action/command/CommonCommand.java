package org.example.dictionarybot.handler.income.action.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum CommonCommand {
    START("/start"),
    HELP("/help"),
    CREATE_DICT("/create_dict"),
    MY_DICTS("/my_dicts"),
    SET_DICT("/set_dict"),
    DICTIONARY_EDITOR("/dictionary_editor"),
    FIND_MATCHING_LEXEMES("/find_lexeme"),
    GET_SYNONYMS("/get_synonyms"),
    GET_TRANSLATIONS("/get_translations"),
    GET_LEXEME_SENSES("/get_lexeme_senses"),
    CURRENT_LEXEME("/current_lexeme");

    private final String command;

    public static Optional<CommonCommand> getByCommand(String command) {
        return Arrays.stream(values()).filter(commonCommandEnum -> commonCommandEnum.getCommand().equals(command)).findFirst();
    }
}
