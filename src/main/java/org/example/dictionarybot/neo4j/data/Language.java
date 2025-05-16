package org.example.dictionarybot.neo4j.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.dictionarybot.neo4j.LanguageCodes;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;


@Setter
@Getter
@Builder
@Node("Language")
public class Language {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String persistentId;

    private String fullName;
    private String code;
    private String description;
}
