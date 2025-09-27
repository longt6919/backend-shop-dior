package com.project.shop_dior.controllers;

import com.project.shop_dior.component.LocalizationUtils;
import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.responses.CategoryResponse;
import com.project.shop_dior.responses.UpdateResponse;
import com.project.shop_dior.service.CategoryService;
import com.project.shop_dior.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "limit") int limit) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/all/admin")
    public ResponseEntity<List<Category>> getAllCategoriesByAdmin(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "limit") int limit) {
        List<Category> categories = categoryService.getAllCategoriesByAdmin();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @PathVariable("id") Long categoryId
    ){
        try {
            Category existingCategory = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(existingCategory);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<CategoryResponse> insertCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
            categoryResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.createCategory(categoryDTO);
        categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY));
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO
    ) {
        UpdateResponse updateCategoryResponse = new UpdateResponse();
        categoryService.updateCategory(id, categoryDTO);
        updateCategoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
        return ResponseEntity.ok(updateCategoryResponse);
    }

    @PutMapping("/block/{categoryId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long categoryId,
            @Valid @PathVariable int active
    ) {
        try {
            categoryService.blockOrEnable(categoryId, active > 0);
            String message = active > 0 ? "Successfully enabled category." : "Successfully blocked the category.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("Category not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('admin')")
//    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
//        try {
//            categoryService.deleteCategory(id);
//            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}
