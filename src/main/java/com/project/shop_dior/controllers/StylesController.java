package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.StyleDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Style;
import com.project.shop_dior.responses.StyleResponse;
import com.project.shop_dior.responses.UpdateResponse;
import com.project.shop_dior.service.StyleService;
import com.project.shop_dior.service.StyleServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/styles")
@RequiredArgsConstructor
public class StylesController {
    private final StyleService styleService;
    @GetMapping("")
    public ResponseEntity<List<Style>> getAllStyles(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Style> styles = styleService.getAllStyles();
        return ResponseEntity.ok(styles);
    }
    @GetMapping("/all/admin")
    public ResponseEntity<List<Style>> getAllStylesByAdmin(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Style> styles = styleService.getAllStylesByAdmin();
        return ResponseEntity.ok(styles);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getStyleById(
            @PathVariable("id") Long styleId
    ){
        try {
            Style existingStyle = styleService.getStyleById(styleId);
            return ResponseEntity.ok(existingStyle);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<StyleResponse> insertStyle(
            @Valid @RequestBody StyleDTO styleDTO,
            BindingResult result) {
        StyleResponse styleResponse = new StyleResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            styleResponse.setMessage("Thêm phong cách thất bại");
            styleResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(styleResponse);
        }
        Style style = styleService.createStyled(styleDTO);
        styleResponse.setMessage("Thêm phong cách thành công");
        styleResponse.setStyle(style);
        return ResponseEntity.ok(styleResponse);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateStyle(
            @PathVariable Long id,
            @RequestBody StyleDTO styleDTO
    ) {
        UpdateResponse updateResponse = new UpdateResponse();
        styleService.updateStyle(id, styleDTO);
        updateResponse.setMessage("Cập nhật phong cách thành công");
        return ResponseEntity.ok(updateResponse);
    }

    @PutMapping("/block/{styleId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long styleId,
            @Valid @PathVariable int active
    ) {
        try {
            styleService.blockOrEnable(styleId, active > 0);
            String message = active > 0 ? "Successfully enabled style." : "Successfully blocked the category.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("Style not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
