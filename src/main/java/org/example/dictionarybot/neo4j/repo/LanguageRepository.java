package org.example.dictionarybot.neo4j.repo;

import org.example.dictionarybot.neo4j.LanguageCodes;
import org.example.dictionarybot.neo4j.data.Language;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends Neo4jRepository<Language, String> {
    Optional<Language> findByCode(LanguageCodes code);
}
