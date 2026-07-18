package com.pawsulin.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    private static final int MAX_PAGE_SIZE = 100;

    private PaginationUtil() {
    }

    public static Pageable createPageable(int page, int size, String sortBy, Sort.Direction direction) {
        int validatedPage = ValidationUtil.requireNonNegative(page, "page");
        int validatedSize = ValidationUtil.requirePositive(size, "size");
        String validatedSortBy = ValidationUtil.requireHasText(sortBy, "sortBy");
        Sort.Direction validatedDirection = direction == null ? Sort.Direction.DESC : direction;
        int normalizedSize = Math.min(validatedSize, MAX_PAGE_SIZE);

        return PageRequest.of(validatedPage, normalizedSize, Sort.by(validatedDirection, validatedSortBy));
    }
}
