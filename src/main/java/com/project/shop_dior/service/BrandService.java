package com.project.shop_dior.service;

import com.project.shop_dior.dtos.BrandDTO;
import com.project.shop_dior.dtos.CategoryDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;

import java.util.List;

public interface BrandService {
    Brand createBrand(BrandDTO branDTO);
    Brand getBrandById(long id);
    List<Brand> getAllBrands();
    List<Brand> getAllBrandsByAdmin();
    Brand updateBrand(long brandId, BrandDTO brandDTO);
    void blockOrEnable(Long brandId, Boolean active) throws DataNotFoundException;
}
