package org.example.dictionarybot.service.data;

import java.util.List;

/// Basically dto for transferring paged data

public record Pagination<V>(
        Integer pageSize,
        Integer currentPage,
        Integer totalPages,
        List<V> items
) { }
