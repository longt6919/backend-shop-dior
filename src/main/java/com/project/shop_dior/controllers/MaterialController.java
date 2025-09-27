package com.project.shop_dior.controllers;

import com.project.shop_dior.component.LocalizationUtils;
import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.dtos.MaterialDTO;
import com.project.shop_dior.models.Material;
import com.project.shop_dior.responses.MaterialResponse;
import com.project.shop_dior.responses.UpdateResponse;
import com.project.shop_dior.service.MaterialService;
import com.project.shop_dior.service.MaterialServiceImpl;
import com.project.shop_dior.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/material")
@RequiredArgsConstructor
public class MaterialController {
private final MaterialService materialService;
private final LocalizationUtils localizationUtils;
    @GetMapping("")
    public ResponseEntity<List<Material>> getAllMaterials(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMaterialById(
            @PathVariable("id") Long materialId
    ){
        try {
            Material existingMaterial = materialService.getMaterialById(materialId);
            return ResponseEntity.ok(existingMaterial);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("")
//    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MaterialResponse> insertMaterial(
            @Valid @RequestBody MaterialDTO materialDTO,
            BindingResult result) {
        MaterialResponse materialResponse = new MaterialResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            materialResponse.setMessage("Thêm chất liệu thất bại");
            materialResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(materialResponse);
        }
        Material material = materialService.createMaterial(materialDTO);
        materialResponse.setMessage("Thêm chất liệu thành công");
        materialResponse.setMaterial(material);
        return ResponseEntity.ok(materialResponse);
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateMaterial(
            @PathVariable Long id,
            @RequestBody MaterialDTO materialDTO
    ) {
        UpdateResponse updateResponse = new UpdateResponse();
        materialService.updateMaterial(id, materialDTO);
        updateResponse.setMessage("Cập nhật chất liệu thành công");
        return ResponseEntity.ok(updateResponse);
    }

}
