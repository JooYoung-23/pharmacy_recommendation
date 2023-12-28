package com.fastcampus.pharmacy_recommendation.pharmacy.service

import com.fastcampus.pharmacy_recommendation.pharmacy.cache.PharmacyRedisTemplateService
import com.fastcampus.pharmacy_recommendation.pharmacy.entity.Pharmacy
import spock.lang.Specification
import spock.lang.Subject

class PharmacySearchServiceTest extends Specification {

    @Subject
    private PharmacySearchService pharmacySearchService

    private PharmacyRepositoryService pharmacyRepositoryService = Mock()
    private PharmacyRedisTemplateService pharmacyRedisTemplateService = Mock()

    private List<Pharmacy> pharmacyList

    def setup() {
        pharmacySearchService = new PharmacySearchService(pharmacyRepositoryService, pharmacyRedisTemplateService)

        pharmacyList = new ArrayList<>()
        pharmacyList.addAll(
                Pharmacy.builder()
                        .id(1L)
                        .pharmacyName("호수온누리약국")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build(),
                Pharmacy.builder()
                        .id(2L)
                        .pharmacyName("돌곶이온누리약국")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build()
        )
        println(pharmacyList.toString())
    }

    def "레디스 장애시 DB를 이용 하여 약국 데이터 조회"() {

        when:
        pharmacyRedisTemplateService.findAll() >> []
        pharmacyRepositoryService.findAll() >> pharmacyList

        def result = pharmacySearchService.searchPharmacyDtoList()

        then:
        result.size() == 2
    }
}
