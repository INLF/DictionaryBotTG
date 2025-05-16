package org.example.dictionarybot.neo4j;

public record Relation(String fromId, String toId, String type) {}