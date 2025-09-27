package com.project.shop_dior.service;
import com.project.shop_dior.dtos.MaterialDTO;
import com.project.shop_dior.models.Material;
import com.project.shop_dior.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService{
    private final MaterialRepository materialRepository;
    @Override
    @Transactional
    public Material createMaterial(MaterialDTO materialDTO) {
        if (materialRepository.existsByName(materialDTO.getName())) {
            throw new IllegalArgumentException("Chất liệu đã tồn tại");
        }
        Material newMaterial =Material.builder().name(materialDTO.getName()).build();
        return materialRepository.save(newMaterial);
    }

    @Override
    public Material getMaterialById(long id) {
        return materialRepository.findById(id).orElseThrow(()
                ->new RuntimeException("Material not found"));
    }

    @Override
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Override
    @Transactional
    public Material updateMaterial(long materialId, MaterialDTO materialDTO) {
        if (materialRepository.existsByName(materialDTO.getName())) {
            throw new IllegalArgumentException("Chất liệu đã tồn tại");
        }
        Material existingMaterial = getMaterialById(materialId);
        existingMaterial.setName(materialDTO.getName());
        return materialRepository.save(existingMaterial);
    }

}
