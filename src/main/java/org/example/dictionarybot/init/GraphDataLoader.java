package org.example.dictionarybot.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.compiler.Lex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.config.AppProperties;
import org.example.dictionarybot.init.dto.LexemeJsonDto;
import org.example.dictionarybot.neo4j.LanguageCodes;
import org.example.dictionarybot.neo4j.Relation;
import org.example.dictionarybot.neo4j.data.Dictionary;
import org.example.dictionarybot.neo4j.data.Language;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.DictionaryRepository;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.example.dictionarybot.neo4j.repo.LanguageRepository;
import org.example.dictionarybot.neo4j.repo.LexemeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;




@Slf4j
@Component
@RequiredArgsConstructor
public class GraphDataLoader implements ApplicationRunner {
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LexemeRepository lexemeRepository;
    private final DictionaryRepository dictionaryRepository;
    private final LanguageRepository languageRepository;
    private final ImplLexemeRepository customLexemeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!args.containsOption(appProperties.getImportArgName())) {
            return;
        }
        String path = args.getOptionValues(appProperties.getImportArgName()).getFirst();
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        log.info("Start loading data from {}", file.getAbsolutePath());
        loadGraph(file);
    }

    private record Pair<Lexeme, LexemeJsonDto>(org.example.dictionarybot.init.dto.LexemeJsonDto dto, org.example.dictionarybot.neo4j.data.Lexeme lexeme) {}

    /// ULTRA BLOCKING OPERATION
    /// SINCE IT PERFORMED ONCE I GUESS IT's FINE!
    /// TODO : SPLIT PARSING AND POPULATING INTO 2 DIFFERENT METHODS!
    public void loadGraph(File file) throws Exception {

        List<LexemeJsonDto> entries = objectMapper.readValue(
                file, new TypeReference<List<LexemeJsonDto>>() {}
        );

        Dictionary cathedralDictionary = dictionaryRepository
                .findByName("Main Dictionary")
                .orElseGet(() -> dictionaryRepository.save(
                        Dictionary.builder()
                                .description("Main Dictionary")
                                .name("Main Dictionary")
                                .ownerId("ANY")
                                .build()
                ));

        Language en = languageRepository.findByCode(LanguageCodes.ENGLISH)
                .orElseGet(() -> languageRepository.save(Language.builder().code(LanguageCodes.ENGLISH.toValue()).build()));

        Language ru = languageRepository.findByCode(LanguageCodes.RUSSIAN)
                .orElseGet(() -> languageRepository.save(Language.builder().code(LanguageCodes.RUSSIAN.toValue()).build()));

        // Phase 1: batch save basic Lexemes
        HashMap<String, Pair<LexemeJsonDto, Lexeme>> lexemes = new HashMap<>();
        List<Lexeme> batch = new ArrayList<>();
        int batchSize = 500;
        log.info("Starting parsing! size of {}", entries.size());
        for (LexemeJsonDto entry : entries) {
            Lexeme l = Lexeme.builder()
                    .dictionary(cathedralDictionary)
                    .lexeme(entry.getLexeme())
                    .language(entry.getLang() == LanguageCodes.ENGLISH ? en : ru)
                    .senses(entry.getSenses())
                    .build();
            lexemes.put(l.getLexeme(), new Pair<>(entry, l));
            batch.add(l);

            if (batch.size() >= batchSize) {
                long start = System.nanoTime();
                log.info("batch started {}", batch.size());
                customLexemeRepository.saveAllFast(batch);
                long end = System.nanoTime();
                log.info("batch ended {}", (long)(end - start) / 1_000_000);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            customLexemeRepository.saveAllFast(batch);
        }

        // Phase 2: generate Relation list and push via APOC
        List<Relation> relations = new ArrayList<>();

        for (Pair<LexemeJsonDto, Lexeme> pair : lexemes.values()) {
            LexemeJsonDto dto = pair.dto();
            Lexeme source = pair.lexeme();

            try {
                getSetOfLexemes(dto.getSynonyms(), lexemes).ifPresent(syns ->
                        syns.forEach(target -> relations.add(new Relation(source.getLexeme(), target.getLexeme(), "SYNONYM")))
                );
                getSetOfLexemes(dto.getTranslations(), lexemes).ifPresent(trans ->
                        trans.forEach(target -> relations.add(new Relation(source.getLexeme(), target.getLexeme(), "TRANSLATION")))
                );
                getSetOfLexemes(dto.getAntonyms(), lexemes).ifPresent(ants ->
                        ants.forEach(target -> relations.add(new Relation(source.getLexeme(), target.getLexeme(), "ANTONYM")))
                );
            } catch (IllegalArgumentException e) {
                log.warn("Wiring error for lexeme '{}': {}", source.getLexeme(), e.getMessage());
            }
        }

        customLexemeRepository.saveRelationsBulk(relations);

        //Phase 3: Load it in neo4j

        log.info("✅ ALL LEXEMES SUCCESSFULLY PROCESSED! count = {}", lexemes.size());
    }

    private Optional<Set<Lexeme>> getSetOfLexemes(List<String> list, HashMap<String, Pair<LexemeJsonDto, Lexeme>> hashMap) {
        if (list.stream().anyMatch(l -> hashMap.get(l) == null)) {
            return Optional.empty();
        }
        Set<Lexeme> result = list.stream()
                .map(l -> hashMap.get(l).lexeme())
                .collect(Collectors.toSet());
        return Optional.of(result);
    }

}
