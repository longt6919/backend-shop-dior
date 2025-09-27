package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Danh mục đã tồn tại");
        }
        Category newCategory =Category.builder().name(categoryDTO.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllActive();
    }

    @Override
    public List<Category> getAllCategoriesByAdmin() {

        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Danh mục đã tồn tại");
        }
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);    }

    @Override
    @Transactional
    public void blockOrEnable(Long categoryId, Boolean active) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()->new DataNotFoundException("Category not found"));
        existingCategory.setActive(active);
        categoryRepository.save(existingCategory);
    }

//    @Override
//    public void deleteCategory(long id) {
//        if(!categoryRepository.existsById(id)){
//            throw new RuntimeException("Danh muc khong ton tai voi ID: "+id);
//        }
//        if (categoryRepository.existsProductByCategoryId(id)){
//            throw new DataIntegrityViolationException("Do not delete category: "+id);
//        }
//        categoryRepository.deleteById(id);
//    }
    }

