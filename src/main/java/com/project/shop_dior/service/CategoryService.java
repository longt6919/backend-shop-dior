package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    List<Category> getAllCategoriesByAdmin();
    Category updateCategory(long categoryId, CategoryDTO categoryDTO);
    void blockOrEnable(Long categoryId, Boolean active) throws DataNotFoundException;
//    void deleteCategory(long id);
}
