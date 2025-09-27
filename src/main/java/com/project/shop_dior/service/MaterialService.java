package com.project.shop_dior.service;

import com.project.shop_dior.dtos.BrandDTO;
import com.project.shop_dior.dtos.MaterialDTO;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Material;

import java.util.List;

public interface MaterialService {
    Material createMaterial(MaterialDTO materialDTO);
    Material getMaterialById(long id);
    List<Material> getAllMaterials();
    Material updateMaterial(long materialId, MaterialDTO materialDTO);
}
