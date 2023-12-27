package com.fastcampus.pharmacy_recommendation.pharmacy.service;

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

  public List<PharmacyDto> searchPharmacyDtoList() {
    //redis

    //db
    return pharmacyRepositoryService.findAll()
        .stream()
        .map(this::convertToPharmacyDto)
        .collect(Collectors.toList());
  }

  private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy) {
    return PharmacyDto.builder()
        .id(pharmacy.getId())
        .pharmacyAddress(pharmacy.getPharmacyAddress())
        .pharmacyName(pharmacy.getPharmacyName())
        .latitude(pharmacy.getLatitude())
        .longitude(pharmacy.getLongitude())
        .build();
  }
}
