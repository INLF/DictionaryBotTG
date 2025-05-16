package org.example.dictionarybot.neo4j.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.neo4j.data.Dictionary;
import org.example.dictionarybot.neo4j.data.Language;
import org.example.dictionarybot.neo4j.repo.DictionaryRepository;
import org.example.dictionarybot.neo4j.repo.LanguageRepository;
import org.example.dictionarybot.service.data.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Set;


// TODO : Add exceptions and handling

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryRepository dictionaryRepository;
    private final LanguageRepository languageRepository;

    @Transactional
    public Pagination<Dictionary> getOwnerDictionaries(String ownerId, int page, int size) {
        if (size == 0 || page < 0) {
            throw new IllegalArgumentException("Page number must be greater than zero");
        }

        int allPages = dictionaryRepository.getAllDictionariesByOwnerId(ownerId) / size + 1;
        long skip = (long) page * size;
        Set<Dictionary> dictionaries = dictionaryRepository.findDictionariesByOwnerId(ownerId, skip, size);

        return new Pagination<>(size, page, allPages, dictionaries.stream().toList());
    }

    public Dictionary getDictionaryById(String id) {
        return dictionaryRepository.getDictionaryByPersistentId(id);
    }


    // TODO: add unique constraint
    // we only support eng-rus!!!
    public Dictionary addNewDictionary(String ownerId, String name, String description) {
        Dictionary dictionary = Dictionary.builder()
                .name(name)
                .description(description)
                .ownerId(ownerId)
                .build();
        return dictionaryRepository.save(dictionary);
    }

    public Language addNewLanguage(String name, String description) {
        Language lang = Language.builder().fullName(name).description(description).build();
        return languageRepository.save(lang);
    }

}
