package com.fastcampus.pharmacy_recommendation.pharmacy.service;

import com.fastcampus.pharmacy_recommendation.api.dto.DocumentDto;
import com.fastcampus.pharmacy_recommendation.api.dto.KakaoApiResponseDto;
import com.fastcampus.pharmacy_recommendation.api.service.KakaoAddressSearchService;
import com.fastcampus.pharmacy_recommendation.direction.entity.Direction;
import com.fastcampus.pharmacy_recommendation.direction.service.DirectionService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {

  private final KakaoAddressSearchService kakaoAddressSearchService;
  private final DirectionService directionService;

  public void recommendPharmacyList(String address) {
    KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(
        address);
    if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(
        kakaoApiResponseDto.getDocumentList())) {
      log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}",
          address);
      return;
    }
    DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);

    List<Direction> directionList = directionService.buildDirectionList(documentDto);
    directionService.saveAll(directionList);
  }
}
