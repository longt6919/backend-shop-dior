package com.project.shop_dior.service;

import com.project.shop_dior.dtos.OriginDTO;
import com.project.shop_dior.models.Origin;

import java.util.List;


public interface OriginService {
    Origin createOrigin(OriginDTO originsDTO);
    Origin getOriginById(long id);
    List<Origin> getAllOrigins();
    Origin updateOrigin(long originId, OriginDTO originDTO);
    void deleteOrigin(long id);
}
