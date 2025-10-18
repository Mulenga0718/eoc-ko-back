package com.ibs.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page; // Renamed from pageNumber
    private int size; // Renamed from pageSize
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber() + 1; // 0-based to 1-based
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
