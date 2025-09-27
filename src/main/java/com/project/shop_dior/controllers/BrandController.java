package com.project.shop_dior.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.dtos.BrandDTO;
import com.project.shop_dior.dtos.OriginDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Origin;
import com.project.shop_dior.responses.BrandResponse;
import com.project.shop_dior.responses.UpdateResponse;
import com.project.shop_dior.service.BrandService;
import com.project.shop_dior.service.BrandServiceImpl;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/brand")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    @GetMapping("")
    public ResponseEntity<List<Brand>> getAllBrands(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Brand> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }
    @GetMapping("/all/admin")
    public ResponseEntity<List<Brand>> getAllBrandsByAdmin(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Brand> brands = brandService.getAllBrandsByAdmin();
        return ResponseEntity.ok(brands);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(
            @PathVariable("id") Long brandId
    ){
        try {
            Brand existingBrand = brandService.getBrandById(brandId);
            return ResponseEntity.ok(existingBrand);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BrandResponse> insertBrand(
            @Valid @RequestBody BrandDTO brandDTO,
            BindingResult result) {
        BrandResponse brandResponse = new BrandResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            brandResponse.setMessage("Thêm thương hiệu thất bại");
            brandResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(brandResponse);
        }
        Brand brand = brandService.createBrand(brandDTO);
        brandResponse.setMessage("Thêm thương hiệu thành công");
        brandResponse.setBrand(brand);
        return ResponseEntity.ok(brandResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateBrand(
            @PathVariable Long id,
            @RequestBody BrandDTO brandDTO
    ) {
        UpdateResponse updateResponse = new UpdateResponse();
        brandService.updateBrand(id, brandDTO);
        updateResponse.setMessage("Cập nhật thương hiệu thành công");
        return ResponseEntity.ok(updateResponse);
    }

    @PutMapping("/block/{brandId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long brandId,
            @Valid @PathVariable int active
    ) {
        try {
            brandService.blockOrEnable(brandId, active > 0);
            String message = active > 0 ? "Successfully enabled brand." : "Successfully blocked the category.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("Brand not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
