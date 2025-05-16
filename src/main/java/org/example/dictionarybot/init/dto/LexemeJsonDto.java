package org.example.dictionarybot.init.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.dictionarybot.neo4j.LanguageCodes;

import java.util.List;



@Getter
@Setter
public class LexemeJsonDto {
    private String lexeme;
    private LanguageCodes lang;
    private List<String> translations;
    private List<String> synonyms;
    private List<String> antonyms;
    private List<String> senses;
}

