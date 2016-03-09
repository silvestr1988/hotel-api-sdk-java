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

import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum HotelContentPaths {

    TYPES_URL(ConstantHolder.TYPE_PATH, AbstractGenericContentResponse.class);

    private final String urlTemplate;
    private final Class<? extends AbstractGenericContentResponse> responseClass;

    HotelContentPaths(final String urlTemplate, Class<? extends AbstractGenericContentResponse> responseClass) {
        this.urlTemplate = urlTemplate;
        this.responseClass = responseClass;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public Class<? extends AbstractGenericContentResponse> getResponseClass() {
        return responseClass;
    }

    public String getUrl(HotelApiService service, HotelApiVersion version) {
        return getUrl(service, version, null);
    }

    public String getUrl(HotelApiService service, HotelApiVersion version, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("path", service.getHotelContentPath());
        params.put("version", version.getVersion());
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(urlTemplate);
    }

    private static class ConstantHolder {
        private final static String TYPE_PATH =
            "${path}/${version}/types/${type}?fields=${fields}&language=${language}&from=${from}&to=${to}&useSecondaryLanguage=${useSecondaryLanguage}&lastUpdateTime=${lastUpdateTime}";
    }

}
