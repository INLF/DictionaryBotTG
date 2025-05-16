package org.example.dictionarybot.neo4j;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum LanguageCodes {
    RUSSIAN("ru"),
    ENGLISH("en");

    private final String code;

    @JsonCreator
    public static LanguageCodes fromCode(String code) {
        for (LanguageCodes lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }

    @JsonValue
    public String toValue() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
