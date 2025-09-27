package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.dtos.OriginDTO;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Origin;
import com.project.shop_dior.responses.CategoryResponse;
import com.project.shop_dior.responses.OriginResponse;
import com.project.shop_dior.responses.UpdateResponse;
import com.project.shop_dior.service.OriginService;
import com.project.shop_dior.service.OriginServiceImpl;
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
@RequestMapping("api/v1/origins")
@RequiredArgsConstructor
public class OriginController {
    private final OriginService originService;
    @GetMapping("")
    public ResponseEntity<List<Origin>> getAllOrigins(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        List<Origin> origins = originService.getAllOrigins();
        return ResponseEntity.ok(origins);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOriginById(
            @PathVariable("id") Long originId
    ){
        try {
            Origin existingOrigin = originService.getOriginById(originId);
            return ResponseEntity.ok(existingOrigin);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<OriginResponse> insertOrigin(
            @Valid @RequestBody OriginDTO originDTO,
            BindingResult result) {
        OriginResponse originResponse = new OriginResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            originResponse.setMessage("Thêm quốc gia thất bại");
            originResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(originResponse);
        }
        Origin origin = originService.createOrigin(originDTO);
        originResponse.setMessage("Thêm quốc gia mới thành công");
        originResponse.setOrigin(origin);
        return ResponseEntity.ok(originResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateOrigin(
            @PathVariable Long id,
            @RequestBody OriginDTO originDTO
    ) {
        UpdateResponse updateResponse = new UpdateResponse();
        originService.updateOrigin(id, originDTO);
        updateResponse.setMessage("Cập nhật quốc gia mới thành công");
        return ResponseEntity.ok(updateResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteOrigin(@PathVariable Long id) {
        try {
            originService.deleteOrigin(id);
            return ResponseEntity.ok("Xóa thành công: "+id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
