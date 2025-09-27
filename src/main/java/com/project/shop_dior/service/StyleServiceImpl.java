package com.project.shop_dior.service;

import com.project.shop_dior.dtos.StyleDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Style;
import com.project.shop_dior.repository.StyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StyleServiceImpl implements StyleService {
    private final StyleRepository styleRepository;
    @Override
    @Transactional
    public Style createStyled(StyleDTO styleDTO) {
        if (styleRepository.existsByName(styleDTO.getName())) {
            throw new IllegalArgumentException("Phong cách đã tồn tại");
        }
        Style newStyle =Style.builder().name(styleDTO.getName()).build();
        return styleRepository.save(newStyle);
    }

    @Override
    public Style getStyleById(long id) {
        return styleRepository.findById(id).orElseThrow(()
                ->new RuntimeException("Style not found"));
    }

    @Override
    public List<Style> getAllStyles() {
        return styleRepository.findAllActive();
    }

    @Override
    public List<Style> getAllStylesByAdmin() {
        return styleRepository.findAll();
    }

    @Override
    public Style updateStyle(long styleId, StyleDTO styleDTO) {
        if (styleRepository.existsByName(styleDTO.getName())) {
            throw new IllegalArgumentException("Phong cách đã tồn tại");
        }
        Style existingStyle = getStyleById(styleId);
        existingStyle.setName(styleDTO.getName());
        return styleRepository.save(existingStyle);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long styleId, Boolean active) throws DataNotFoundException {
        Style existingStyle = styleRepository.findById(styleId)
                .orElseThrow(()->new DataNotFoundException("Style not found"));
        existingStyle.setActive(active);
        styleRepository.save(existingStyle);
    }

    }

