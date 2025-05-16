package org.example.dictionarybot.neo4j.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Neo4jConfig {
    private final ImplLexemeRepository lexemeRepository;
    @PostConstruct
    public void init() {
        lexemeRepository.createBtreeIndex();
        lexemeRepository.createFulltextIndex();
    }

}
