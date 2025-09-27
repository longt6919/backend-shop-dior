package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.ColorDTO;
import com.project.shop_dior.dtos.MaterialDTO;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.models.Material;
import com.project.shop_dior.models.Size;
import com.project.shop_dior.responses.*;
import com.project.shop_dior.service.ColorService;
import com.project.shop_dior.service.ColorServiceImpl;
import com.project.shop_dior.service.SizeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/color")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;
    @GetMapping("")
    public ResponseEntity<Page<Color>> getAllColors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Color> colors = colorService.getAllColorsByAdmin(pageable);
        return ResponseEntity.ok(colors);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getColorById(
            @PathVariable("id") Long colorId
    ){
        try {
            Color existingColor = colorService.getColorById(colorId);
            return ResponseEntity.ok(existingColor);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
//    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ColorResponse> insertColor(
            @Valid @RequestBody ColorDTO colorDTO,
            BindingResult result) {
        ColorResponse colorResponse = new ColorResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            colorResponse.setMessage("Thêm màu sắc thất bại");
            colorResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(colorResponse);
        }
        Color color = colorService.createColor(colorDTO);
        colorResponse.setMessage("Thêm màu sắc thành công");
        colorResponse.setColor(color);
        return ResponseEntity.ok(colorResponse);
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateColor(
            @PathVariable Long id,
            @RequestBody ColorDTO colorDTO
    ) {
        UpdateResponse updateResponse = new UpdateResponse();
        colorService.updateColor(id, colorDTO);
        updateResponse.setMessage("Cập nhật màu thành công");
        return ResponseEntity.ok(updateResponse);
    }
}
