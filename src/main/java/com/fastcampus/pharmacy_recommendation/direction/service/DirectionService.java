package com.fastcampus.pharmacy_recommendation.direction.service;

import com.fastcampus.pharmacy_recommendation.api.dto.DocumentDto;
import com.fastcampus.pharmacy_recommendation.direction.entity.Direction;
import com.fastcampus.pharmacy_recommendation.direction.repository.DirectionRepository;
import com.fastcampus.pharmacy_recommendation.pharmacy.dto.PharmacyDto;
import com.fastcampus.pharmacy_recommendation.pharmacy.service.PharmacySearchService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

  private static final int MAX_SEARCH_COUNT = 3; // 약국 최대 검색 갯수
  private static final double RADIUS_KM = 10.0; // 반경 10km

  private final PharmacySearchService pharmacySearchService;
  private final DirectionRepository directionRepository;
  private final Base62Service base62Service;

  private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

  @Transactional
  public List<Direction> saveAll(List<Direction> directionList) {
    if (CollectionUtils.isEmpty(directionList)) {
      return Collections.emptyList();
    }
    return directionRepository.saveAll(directionList);
  }

  public String findDirectionUrlById(String encodedId) {
    Long decodedId = base62Service.decodeDirectionId(encodedId);
    Direction findDirection = directionRepository.findById(decodedId).orElse(null);

    String directionUrlParams = String.join(",", findDirection.getTargetPharmacyName(),
        String.valueOf(findDirection.getTargetLatitude()),
        String.valueOf(findDirection.getTargetLongitude()));
    String directionUrl = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + directionUrlParams)
        .toUriString();

    return directionUrl;
  }

  public List<Direction> buildDirectionList(DocumentDto documentDto) {

    if (Objects.isNull(documentDto)) {
      return Collections.emptyList();
    }
    // 약국 데이터 조회
    List<PharmacyDto> pharmacyDtoList = pharmacySearchService.searchPharmacyDtoList();
    // 거리 계산 알고리즘 적용
    return pharmacyDtoList
        .stream()
        .map(pharmacyDto ->
            Direction.builder()
                .inputAddress(documentDto.getAddressName())
                .inputLatitude(documentDto.getLatitude())
                .inputLongitude(documentDto.getLongitude())
                .targetPharmacyName(pharmacyDto.getPharmacyName())
                .targetAddress(pharmacyDto.getPharmacyAddress())
                .targetLatitude(pharmacyDto.getLatitude())
                .targetLongitude(pharmacyDto.getLongitude())
                .distance(
                    calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                        pharmacyDto.getLatitude(), pharmacyDto.getLongitude())
                )
                .build())
        .filter(direction -> direction.getDistance() <= RADIUS_KM)
        .sorted(Comparator.comparing(Direction::getDistance))
        .limit(MAX_SEARCH_COUNT)
        .collect(Collectors.toList());
    //
  }

  // Haversine formula
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    lat1 = Math.toRadians(lat1);
    lon1 = Math.toRadians(lon1);
    lat2 = Math.toRadians(lat2);
    lon2 = Math.toRadians(lon2);

    double earthRadius = 6371; //Kilometers
    return earthRadius * Math.acos(
        Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
  }
}
