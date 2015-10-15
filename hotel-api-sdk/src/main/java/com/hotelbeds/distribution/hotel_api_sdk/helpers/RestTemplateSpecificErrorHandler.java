package com.hotelbeds.distribution.hotel_api_sdk.helpers;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTemplateSpecificErrorHandler implements ResponseErrorHandler {

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final StringWriter writer = new StringWriter();
        try (InputStream theIS = response.getBody();
            InputStreamReader theISR = new InputStreamReader(theIS, StandardCharsets.UTF_8);
            BufferedReader theBR = new BufferedReader(theISR)) {
            char[] buffer = new char[4096];
            int count = theBR.read(buffer);
            while (count >= 0) {
                writer.write(buffer, 0, count);
                count = theBR.read(buffer);
            }
        }
        log.error("Response error: {} {}", response.getStatusText(), writer.toString());
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return isError(response.getStatusCode());
    }

    private boolean isError(final HttpStatus status) {
        final HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
