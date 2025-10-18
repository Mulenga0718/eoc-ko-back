package com.ibs.global.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class PageRequest {

    private int page = 1; // 기본 페이지 번호 (1부터 시작)
    private int size = 10; // 기본 페이지 크기
    private String sortBy = "id"; // 기본 정렬 기준
    private Sort.Direction direction = Sort.Direction.DESC; // 기본 정렬 방향 (내림차순)

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page - 1, size, direction, sortBy);
    }
}
