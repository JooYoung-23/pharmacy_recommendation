package com.fastcampus.pharmacy_recommendation.direction.controller;

import com.fastcampus.pharmacy_recommendation.direction.entity.Direction;
import com.fastcampus.pharmacy_recommendation.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DirectionController {

  private final DirectionService directionService;

  @GetMapping("/dir/{encodedId}")
  public String searchDirection(@PathVariable("encodedId") String encodedId) {
    String directionUrl = directionService.findDirectionUrlById(encodedId);

    return "redirect:" + directionUrl;
  }
}
