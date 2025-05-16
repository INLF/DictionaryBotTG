package org.example.dictionarybot.neo4j.data;


import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;


import java.util.List;
import java.util.Set;

@Setter
@Getter
@Node("Lexeme")
@Builder
public class Lexeme {
    @Id @NotNull
    private String lexeme;
    private List<String> senses;

    @Relationship(type = "LANGUAGE", direction = Relationship.Direction.OUTGOING)
    private Language language;

    @Relationship(type = "SYNONYM", direction = Relationship.Direction.OUTGOING)
    private Set<Lexeme> synonyms;

    @Relationship(type = "ANTONYM", direction = Relationship.Direction.OUTGOING)
    private Set<Lexeme> antonyms;

    @Relationship(type = "TRANSLATION", direction = Relationship.Direction.OUTGOING)
    private Set<Lexeme> translations;

    @Relationship(type ="DICTIONARY", direction = Relationship.Direction.OUTGOING)
    private Dictionary dictionary;

    @Override
    public int hashCode() {
        return lexeme.hashCode();
    }
}
