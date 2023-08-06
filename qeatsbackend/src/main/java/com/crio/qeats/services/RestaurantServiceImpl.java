
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  // For peak hours: 8AM - 10AM, 1PM-2PM, 7PM-9PM
  //  * - service radius is 3KMs.
  //  * - All other times, serving radius is 5KMs.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
        log.info("Finding all restaurants close by for request: {} at time: {}", getRestaurantsRequest, currentTime);

        Double servingRadiusInKms = normalHoursServingRadiusInKms;

        if (isPeakHour(currentTime)) {
          servingRadiusInKms = peakHoursServingRadiusInKms;
        }
    
        List<Restaurant> restaurants = restaurantRepositoryService.findAllRestaurantsCloseBy(
              getRestaurantsRequest.getLatitude(),
              getRestaurantsRequest.getLongitude(),
              currentTime,
              servingRadiusInKms);
    
        GetRestaurantsResponse response = new GetRestaurantsResponse(restaurants);
        // response.setRestaurants(restaurants);
        log.info("Found {} restaurants for request: {} at time: {}", restaurants.size(), getRestaurantsRequest, currentTime);
        return response;
  }

  /**
   * Determines whether the given time is a peak hour.
   *
   * @param time the time to check
   * @return true if the time is a peak hour, false otherwise
   */
  private boolean isPeakHour(LocalTime time) {
    int hour = time.getHour();
    return (hour >= 8 && hour <= 10) || (hour >= 13 && hour <= 14) || (hour >= 19 && hour <= 21);
  }

}

