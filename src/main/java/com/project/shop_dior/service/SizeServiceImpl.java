package com.project.shop_dior.service;

import com.project.shop_dior.models.Size;
import com.project.shop_dior.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService{
private final SizeRepository sizeRepository;
    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }
}
