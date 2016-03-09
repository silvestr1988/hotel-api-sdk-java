package com.hotelbeds.demo.simple;

/*
 * #%L
 * HotelAPI SDK Demo
 * %%
 * Copyright (C) 2015 - 2016 HOTELBEDS TECHNOLOGY, S.L.U.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
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
        } catch (HotelApiSDKException e) {
            log.error("Error requesting status", e);
        }
    }

}
