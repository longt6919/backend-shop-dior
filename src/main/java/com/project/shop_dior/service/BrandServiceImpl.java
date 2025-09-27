package com.project.shop_dior.service;

import com.project.shop_dior.dtos.BrandDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService{
    private final BrandRepository brandRepository;
    @Override
    @Transactional
    public Brand createBrand(BrandDTO brandDTO) {
        if (brandRepository.existsByName(brandDTO.getName())) {
            throw new IllegalArgumentException("Thương hiệu đã tồn tại");
        }
        Brand newBrand =Brand.builder().name(brandDTO.getName())
                .description(brandDTO.getDescription()).build();
        return brandRepository.save(newBrand);    }

    @Override
    public Brand getBrandById(long id) {
        return brandRepository.findById(id).orElseThrow(()->new RuntimeException("Brand not found"));
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAllActive();
    }

    @Override
    public List<Brand> getAllBrandsByAdmin() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand updateBrand(long brandId, BrandDTO brandDTO) {
        if (brandRepository.existsByName(brandDTO.getName())) {
            throw new IllegalArgumentException("Thương hiệu đã tồn tại");
        }
        Brand existingBrand = getBrandById(brandId);
        existingBrand.setName(brandDTO.getName());
        existingBrand.setDescription(brandDTO.getDescription());
        return brandRepository.save(existingBrand);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long brandId, Boolean active) throws DataNotFoundException {
        Brand existingBrand = brandRepository.findById(brandId)
                .orElseThrow(()->new DataNotFoundException("Brand not found"));
        existingBrand.setActive(active);
        brandRepository.save(existingBrand);
    }


}
