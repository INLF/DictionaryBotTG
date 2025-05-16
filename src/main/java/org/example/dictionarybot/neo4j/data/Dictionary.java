package org.example.dictionarybot.neo4j.data;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.Set;


/// Since our dictionary will be mostly centralised
/// we can just make triplets representing user -> dictionary -> words mapping
/// Should test that!
///
/// NOW WE ONLY SUPPORT RUS-ENG DICT

@Setter
@Getter
@Node("Dictionary")
@Builder
public class Dictionary {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String persistentId;
    public String ownerId;
    public String name;
    public String description;
//    TODO: rework
//    public Set<String> usersWithAccess;
}
