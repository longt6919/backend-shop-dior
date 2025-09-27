package com.project.shop_dior.service;

import com.project.shop_dior.dtos.BrandDTO;
import com.project.shop_dior.dtos.StyleDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Style;

import java.util.List;

public interface StyleService {
    Style createStyled(StyleDTO styleDTO);
    Style getStyleById(long id);
    List<Style> getAllStyles();
    List<Style> getAllStylesByAdmin();
    Style updateStyle(long styleId, StyleDTO styleDTO);
    void blockOrEnable(Long styleId, Boolean active) throws DataNotFoundException;

}
