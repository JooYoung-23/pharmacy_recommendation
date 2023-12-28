package com.fastcampus.pharmacy_recommendation.pharmacy.service;

import com.fastcampus.pharmacy_recommendation.api.dto.DocumentDto;
import com.fastcampus.pharmacy_recommendation.api.dto.KakaoApiResponseDto;
import com.fastcampus.pharmacy_recommendation.api.service.KakaoAddressSearchService;
import com.fastcampus.pharmacy_recommendation.direction.dto.OutputDto;
import com.fastcampus.pharmacy_recommendation.direction.entity.Direction;
import com.fastcampus.pharmacy_recommendation.direction.service.Base62Service;
import com.fastcampus.pharmacy_recommendation.direction.service.DirectionService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {

  private final KakaoAddressSearchService kakaoAddressSearchService;
  private final DirectionService directionService;
  private final Base62Service base62Service;

  private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

  @Value("${pharmacy.recommendation.base.url}")
  private String baseUrl;

  public List<OutputDto> recommendPharmacyList(String address) {
    KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(
        address);
    if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(
        kakaoApiResponseDto.getDocumentList())) {
      log.error("[PharmacyRecommendationService.recommendPharmacyList fail] Input address: {}",
          address);
      return Collections.emptyList();
    }

    DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);
    List<Direction> directionList = directionService.buildDirectionList(documentDto);
    return directionService.saveAll(directionList)
        .stream()
        .map(this::convertToOutputDto)
        .collect(Collectors.toList());
  }

  private OutputDto convertToOutputDto(Direction direction) {


    String shortenDirectionUrl = baseUrl + base62Service.encodeDirectionId(direction.getId());

    String roadViewUrl = String.join(",", ROAD_VIEW_BASE_URL,
        String.valueOf(direction.getTargetLatitude()),
        String.valueOf(direction.getTargetLongitude()));


    return OutputDto.builder()
        .pharmacyAddress(direction.getTargetAddress())
        .pharmacyName(direction.getTargetPharmacyName())
        .directionUrl(shortenDirectionUrl)
        .roadViewUrl(roadViewUrl)
        .distance(String.format("%.2f km", direction.getDistance()))
        .build();
  }
}
