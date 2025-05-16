package org.example.dictionarybot.neo4j.repo;

import org.example.dictionarybot.neo4j.data.Dictionary;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DictionaryRepository extends Neo4jRepository<Dictionary, String> {
    Optional<Dictionary> findByName(String name);

    @Query("""
        MATCH (d:Dictionary)
        WHERE d.ownerId = $userId
        RETURN d
        SKIP $skip
        LIMIT $limit
    """)
    Set<Dictionary> findDictionariesByOwnerId(String userId, long skip, long limit);

    @Query("""
        MATCH (d:Dictionary)
        WHERE d.ownerId = $userId
        RETURN count(d)
    """)
    Integer getAllDictionariesByOwnerId(String userId);

    Dictionary getDictionaryByPersistentId(String persistentId);
}
