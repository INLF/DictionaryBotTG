package org.example.dictionarybot.neo4j.repo;

import jakarta.transaction.Transactional;
import javassist.compiler.Lex;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.neo4j.data.Dictionary;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.service.data.Pagination;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.example.dictionarybot.neo4j.Relation;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ImplLexemeRepository {
    private final Neo4jClient neo4jClient;
    private final LexemeRepository lexemeRepository;


    public void createBtreeIndex() {
        String query = """
            CREATE INDEX lexeme_exact_index IF NOT EXISTS
            FOR (l:Lexeme) ON (l.lexeme)
        """;
        neo4jClient.query(query).run();
    }

    public void createFulltextIndex() {
        String query = """
            CREATE FULLTEXT INDEX lexeme_fulltext_index
            IF NOT EXISTS FOR (l:Lexeme) ON EACH [l.lexeme]
        """;
        neo4jClient.query(query).run();
    }

    private Map<String, Long> getLanguageIdMap() {
        return neo4jClient.query("MATCH (l:Language) RETURN l.code AS code, id(l) AS id")
                .fetch()
                .all()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("code"),
                        row -> (Long) row.get("id")
                ));
    }

    @Transactional
    public Pagination<Lexeme> getSynonymsOfLexeme(String lexeme, int page, int size) {
        if (size == 0 || page < 0) {
            throw new IllegalArgumentException("Page number must be greater than zero");
        }

        int allPages = lexemeRepository.getAllSynonyms(lexeme) / size + 1;
        int skip = page * size;
        Set<Lexeme> dictionaries = lexemeRepository.getSynonyms(lexeme, skip, size);

        return new Pagination<>(size, page, allPages, dictionaries.stream().toList());
    }



    @Transactional
    public Pagination<Lexeme> getTranslationsOfLexeme(String lexeme, int page, int size) {
        if (size <= 0 || page < 0) {
            throw new IllegalArgumentException("Page size must be positive and page non-negative");
        }

        int total = lexemeRepository.getAllTranslations(lexeme);
        if (total == 0) {
            return new Pagination<>(size, 0, 0, List.of());
        }

        int allPages = (total + size - 1) / size;
        int skip = page * size;

        Set<Lexeme> translations = Optional.ofNullable(
                lexemeRepository.getTranslations(lexeme, skip, size)
        ).orElse(Set.of());

        log.info("Found {} translations", translations.size());
        log.info("Found {} total", total);

        return new Pagination<>(size, page, allPages, translations.stream().toList());
    }


    @Transactional
    public Pagination<Lexeme> getMatchingLexemes(String lexeme,int limit, int page, int size) {
        if (size == 0 || page < 0) {
            throw new IllegalArgumentException("Page number must be greater than zero");
        }

        Set<Lexeme> foundLexemes = lexemeRepository.findAllMatchingLexemes(lexeme, limit);

        int allPages = foundLexemes.size() / size + 1;
        long skip = (long) page * size;
        return new Pagination<>(size, page, allPages, foundLexemes.stream().skip(skip).limit(limit).toList());
    }

    private Map<String, Long> getDictionaryIdMap() {
        return neo4jClient.query("MATCH (d:Dictionary) RETURN d.name AS name, id(d) AS id")
                .fetch()
                .all()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("name"),
                        row -> (Long) row.get("id")
                ));
    }

    public void saveRelationsBulk(List<Relation> relations) {
        var query = """
        CALL apoc.periodic.iterate(
          "UNWIND $relations AS rel RETURN rel",
          "
          MATCH (a:Lexeme {lexeme: rel.fromId}), (b:Lexeme {lexeme: rel.toId})
          CALL apoc.create.relationship(a, $relType, {}, b) YIELD rel as createdRel
          RETURN count(createdRel)
          ",
          {batchSize: 1000, parallel: true, params: {relations: $relations, relType: $relType}}
        )
        """;

        Map<String, List<Map<String, Object>>> grouped = relations.stream()
                .collect(Collectors.groupingBy(
                        Relation::type,
                        Collectors.mapping(r -> Map.of("fromId", r.fromId(), "toId", r.toId()), Collectors.toList())
                ));

        grouped.forEach((type, rels) -> {
            neo4jClient.query(query)
                    .bind(rels).to("relations")
                    .bind(type).to("relType")
                    .run();
        });
    }

    @Transactional
    public void saveAllFast(List<Lexeme> lexemes) {
        Map<String, Long> languageIdMap = getLanguageIdMap();
        Map<String, Long> dictionaryIdMap = getDictionaryIdMap();

        List<Lexeme> valid = lexemes.stream()
                .filter(l -> {
                    boolean validLang = languageIdMap.containsKey(l.getLanguage().getCode());
                    boolean validDict = dictionaryIdMap.containsKey(l.getDictionary().getName());
                    if (!validLang || !validDict) {
                        System.err.printf("⚠️ Skipped lexeme '%s': missing lang=%s or dict=%s%n",
                                l.getLexeme(),
                                l.getLanguage().getCode(),
                                l.getDictionary().getName());
                    }
                    return validLang && validDict;
                })
                .toList();

        int batchSize = 500;
        for (int i = 0; i < valid.size(); i += batchSize) {
            int end = Math.min(i + batchSize, valid.size());

            List<Map<String, Object>> batch = valid.subList(i, end).stream()
                    .map(l -> {
                        Long languageId = languageIdMap.get(l.getLanguage().getCode());
                        Long dictionaryId = dictionaryIdMap.get(l.getDictionary().getName());

                        if (languageId == null || dictionaryId == null) {
                            System.err.printf("Missing data for lexeme: %s | langCode=%s | dictName=%s%n",
                                    l.getLexeme(),
                                    l.getLanguage().getCode(),
                                    l.getDictionary().getName());
                            throw new RuntimeException("Missing lang or dict for lexeme: " + l.getLexeme());
                        }

                        return Map.of(
                                "lexeme", l.getLexeme(),
                                "senses", l.getSenses(),
                                "languageId", languageId,
                                "dictionaryId", dictionaryId
                        );
                    })
                    .toList();

            String query = """
            UNWIND $batch AS row
            MERGE (l:Lexeme {lexeme: row.lexeme})
            SET l.senses = row.senses
            WITH l, row
            MATCH (lang) WHERE id(lang) = row.languageId
            MATCH (dict) WHERE id(dict) = row.dictionaryId

            MERGE (l)-[:LANGUAGE]->(lang)
            MERGE (l)-[:DICTIONARY]->(dict)
        """;

            neo4jClient.query(query)
                    .bind(batch).to("batch")
                    .run();
        }
    }



}
