package org.example.dictionarybot.neo4j.repo;


import org.example.dictionarybot.neo4j.data.Lexeme;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LexemeRepository extends Neo4jRepository<Lexeme, String> {
    @Query("""
            MATCH (l:Lexeme)
            WHERE l.lexeme = $lexeme
            RETURN l
            """)
    Optional<Lexeme> findByLexeme(String lexeme);

    @Query("""
            MATCH (l:Lexeme)-[r:SYNONYM]-(s:Lexeme)
            WHERE l.lexeme = $lexeme
            RETURN s AS synonym, r
            SKIP $skip
            LIMIT $limit
            """)
    Set<Lexeme> getSynonyms(@Param("lexeme") String lexeme, @Param("skip") int skip, @Param("limit") int limit);

    @Query("""
         MATCH (l:Lexeme)-[r:SYNONYM]-(s:Lexeme)
         WHERE l.lexeme = $lexeme
         RETURN count(s)
    """)
    Integer getAllSynonyms(@Param("lexeme") String lexeme);

    @Query("""
        MATCH (l:Lexeme)-[:TRANSLATION]-(s:Lexeme)
        WHERE l.lexeme = $lexeme
        RETURN s
        SKIP $skip
        LIMIT $limit
    """)
    Set<Lexeme> getTranslations(@Param("lexeme") String lexeme, @Param("skip") int skip, @Param("limit") int limit);

    @Query("""
         MATCH (l:Lexeme)-[r:TRANSLATION]-(s:Lexeme)
         WHERE l.lexeme = $lexeme
         RETURN count(DISTINCT s)
    """)
    Integer getAllTranslations(@Param("lexeme") String lexeme);

    @Query("""
            CALL db.index.fulltext.queryNodes("lexeme_fulltext_index", $query) YIELD node, score
            WITH node ORDER BY score DESC
            RETURN node
            LIMIT $limit
    """)
    Set<Lexeme> findAllMatchingLexemes(@Param("query") String query, @Param("limit") int limit);
}