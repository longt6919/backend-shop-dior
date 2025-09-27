package com.project.shop_dior.service;

import com.project.shop_dior.dtos.OriginDTO;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Origin;
import com.project.shop_dior.repository.OriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OriginServiceImpl implements OriginService {
    private final OriginRepository originRepository;
    @Override
    @Transactional
    public Origin createOrigin(OriginDTO originsDTO) {
        if (originRepository.existsByName(originsDTO.getName())) {
            throw new IllegalArgumentException("Quốc gia đã tồn tại");
        }
        Origin newOrigin =Origin.builder().name(originsDTO.getName()).build();
        return originRepository.save(newOrigin);
    }

    @Override
    public Origin getOriginById(long id) {
        return originRepository.findById(id).orElseThrow(()
                ->new RuntimeException("Origin not found"));
    }

    @Override
    public List<Origin> getAllOrigins() {
        return originRepository.findAll();
    }

    @Override
    @Transactional
    public Origin updateOrigin(long originId, OriginDTO originDTO) {
        if (originRepository.existsByName(originDTO.getName())) {
            throw new IllegalArgumentException("Quốc gia đã tồn tại");
        }
        Origin existingOrigin = getOriginById(originId);
        existingOrigin.setName(originDTO.getName());
        return originRepository.save(existingOrigin);
    }

    @Override
    public void deleteOrigin(long id) {
        if(!originRepository.existsById(id)){
            throw new RuntimeException("Quoc gia khong ton tai voi ID: "+id);
        }
        originRepository.deleteById(id);
    }
}
