package com.ibs.post.service;

import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import com.ibs.post.domain.Category;
import com.ibs.post.dto.CategoryCreateRequest;
import com.ibs.post.dto.CategoryOrderUpdateRequest;
import com.ibs.post.dto.CategoryResponse;
import com.ibs.post.dto.CategoryUpdateRequest;
import com.ibs.post.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        categoryRepository.findByName(request.name()).ifPresent(c -> {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Category with this name already exists.");
        });

        String slug = generateUniqueSlug(request.name());
        Category category = Category.builder()
                .name(request.name())
                .slug(slug)
                .displayOrder(request.displayOrder())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Category not found."));

        if (!category.getName().equals(request.name())) {
            categoryRepository.findByName(request.name()).ifPresent(c -> {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "Category with this name already exists.");
            });
            category.setName(request.name());
            category.setSlug(generateUniqueSlug(request.name()));
        }

        category.setDisplayOrder(request.displayOrder());

        return CategoryResponse.from(category);
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Category not found."));
        categoryRepository.delete(category);
    }

    public void updateCategoryOrder(CategoryOrderUpdateRequest request) {
        List<Long> categoryIds = request.categoryIds();
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Some category IDs are invalid.");
        }

        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            Category category = categoryMap.get(categoryId);
            if (category != null) {
                category.setDisplayOrder(i);
            }
        }
    }


    private String generateUniqueSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-가-힣]", "")
                .replaceAll("[\\s-]+", "-");

        String slug = baseSlug;
        int counter = 1;
        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
