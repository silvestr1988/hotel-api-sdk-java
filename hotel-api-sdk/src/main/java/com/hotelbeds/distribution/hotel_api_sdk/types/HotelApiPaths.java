package com.hotelbeds.distribution.hotel_api_sdk.types;

/*
 * #%L
 * hotel-api-sdk
 * %%
 * Copyright (C) 2015 HOTELBEDS, S.L.U.
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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.http.HttpMethod;

import com.hotelbeds.distribution.hotel_api_model.auto.messages.AbstractGenericResponse;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.AvailabilityRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.BookingCancellationRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.BookingDetailRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.BookingListRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.BookingRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.CheckRateRS;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.StatusRS;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum HotelApiPaths {

    AVAILABILITY("${path}/${version}/hotels", HttpMethod.POST, AvailabilityRS.class),
    BOOKING_LIST(
        "${path}/${version}/bookings?from=${from}&to=${to}&includeCancelled=${includeCancelled}&filterType=${filterType}",
        HttpMethod.GET,
        BookingListRS.class),
    BOOKING_DETAIL("${path}/${version}/bookings/${bookingId}", HttpMethod.GET, BookingDetailRS.class),
    BOOKING_CONFIRM("${path}/${version}/bookings", HttpMethod.POST, BookingRS.class),
    BOOKING_CANCEL("${path}/${version}/bookings/${bookingId}?cancellationFlag=${flag}", HttpMethod.DELETE, BookingCancellationRS.class),
    CHECK_AVAIL("${path}/${version}/checkrates", HttpMethod.POST, CheckRateRS.class),
    STATUS("${path}/${version}/status", HttpMethod.GET, StatusRS.class), ;

    private final String urlTemplate;
    private final HttpMethod httpMethod;
    private final Class<? extends AbstractGenericResponse> responseClass;

    HotelApiPaths(final String urlTemplate, final HttpMethod httpMethod, Class<? extends AbstractGenericResponse> responseClass) {
        this.urlTemplate = urlTemplate;
        this.httpMethod = httpMethod;
        this.responseClass = responseClass;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public Class<? extends AbstractGenericResponse> getResponseClass() {
        return responseClass;
    }

    public String getUrl(String basePath, HotelApiVersion version) {
        return getUrl(basePath, version, null);
    }

    public String getUrl(String basePath, HotelApiVersion version, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("path", basePath);
        params.put("version", version.getVersion());
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(urlTemplate);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
