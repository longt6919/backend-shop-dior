package com.project.shop_dior.controllers;

import com.project.shop_dior.models.Size;
import com.project.shop_dior.service.SizeService;
import com.project.shop_dior.service.SizeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/size")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;
    @GetMapping("")
    public List<Size> getAllSizes() {
        return sizeService.getAllSizes();
    }
}
