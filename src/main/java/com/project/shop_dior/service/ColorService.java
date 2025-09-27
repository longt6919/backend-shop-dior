package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ColorDTO;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.responses.ColorListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColorService {
    List<Color> getAllColors();
    Page<Color> getAllColorsByAdmin(Pageable pageable);
    Color createColor(ColorDTO colorDTO);
    Color getColorById(long id);
    Color updateColor(long colorId, ColorDTO colorDTO);
}
