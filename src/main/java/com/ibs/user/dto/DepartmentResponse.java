package com.ibs.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DepartmentResponse {
    private final Long id;
    private final String name;
    private final List<DepartmentResponse> children;

    public DepartmentResponse(Long id, String name, List<DepartmentResponse> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }
}