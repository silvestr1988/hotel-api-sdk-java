package com.hotelbeds.demo.simple;

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelSDKException;
import com.hotelbeds.hotelapimodel.auto.messages.StatusRS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckingServiceStatus {

    public static void main(String[] args) {
        // First initialise the client
        HotelApiClient apiClient = new HotelApiClient();
        apiClient.setReadTimeout(20000);
        apiClient.init();
        // Perform the request and browse the results
        try {
            log.info("Requesting status...");
            StatusRS statusRS = apiClient.status();
            log.info("Status: {}", statusRS.getStatus());
        } catch (HotelSDKException e) {
            log.error("Error requesting status", e);
        }
    }

}
