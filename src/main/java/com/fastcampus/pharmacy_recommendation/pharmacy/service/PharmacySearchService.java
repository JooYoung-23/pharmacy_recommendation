package com.fastcampus.pharmacy_recommendation.pharmacy.service;

import com.fastcampus.pharmacy_recommendation.pharmacy.cache.PharmacyRedisTemplateService;
import com.fastcampus.pharmacy_recommendation.pharmacy.dto.PharmacyDto;
import com.fastcampus.pharmacy_recommendation.pharmacy.entity.Pharmacy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PharmacySearchService {

  private final PharmacyRepositoryService pharmacyRepositoryService;
  private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

  public List<PharmacyDto> searchPharmacyDtoList() {

    // redis
    List<PharmacyDto> pharmacyDtoList = pharmacyRedisTemplateService.findAll();
    log.error("searchPharmacyDtoList()");
    if (pharmacyDtoList == null)
      log.error("NULL");
    if(!pharmacyDtoList.isEmpty()) return pharmacyDtoList;

    // db
    return pharmacyRepositoryService.findAll()
        .stream()
        .map(this::convertToPharmacyDto)
        .collect(Collectors.toList());
  }

  private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy) {

    return PharmacyDto.builder()
        .id(pharmacy.getId())
        .pharmacyName(pharmacy.getPharmacyName())
        .pharmacyAddress(pharmacy.getPharmacyAddress())
        .latitude(pharmacy.getLatitude())
        .longitude(pharmacy.getLongitude())
        .build();
  }
}
