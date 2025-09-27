package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ColorDTO;
import com.project.shop_dior.models.*;
import com.project.shop_dior.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService{
    private final ColorRepository colorRepository;
    @Override
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }


    @Override
    public Page<Color> getAllColorsByAdmin(Pageable pageable) {
        return colorRepository.findAll(pageable);
    }


    @Override
    @Transactional
    public Color createColor(ColorDTO colorDTO) {
        if (colorRepository.existsByName(colorDTO.getName())) {
            throw new IllegalArgumentException("Màu sắc đã tồn tại");
        }
        Color newColor =Color.builder().name(colorDTO.getName()).build();
        return colorRepository.save(newColor);    }

    @Override
    public Color getColorById(long id) {
        return colorRepository.findById(id).orElseThrow(()
                ->new RuntimeException("Color not found"));    }

    @Override
    public Color updateColor(long colorId, ColorDTO colorDTO) {
        if (colorRepository.existsByName(colorDTO.getName())) {
            throw new IllegalArgumentException("màu sắc đã tồn tại");
        }
        Color existingColor = getColorById(colorId);
        existingColor.setName(colorDTO.getName());
        return colorRepository.save(existingColor);    }
}
