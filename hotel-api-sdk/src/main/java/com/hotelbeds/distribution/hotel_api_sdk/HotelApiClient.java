package com.hotelbeds.distribution.hotel_api_sdk;

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


import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.Availability;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Booking;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.BookingCheck;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.BookingList;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.LoggingRequestInterceptor;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.RestTemplateSpecificErrorHandler;
import com.hotelbeds.distribution.hotel_api_sdk.types.CancellationFlags;
import com.hotelbeds.distribution.hotel_api_sdk.types.ContentType;
import com.hotelbeds.distribution.hotel_api_sdk.types.FilterType;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiPaths;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiService;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiVersion;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelbedsError;
import com.hotelbeds.hotelapimodel.auto.messages.AbstractGenericRequest;
import com.hotelbeds.hotelapimodel.auto.messages.AvailabilityRQ;
import com.hotelbeds.hotelapimodel.auto.messages.AvailabilityRS;
import com.hotelbeds.hotelapimodel.auto.messages.BookingCancellationRS;
import com.hotelbeds.hotelapimodel.auto.messages.BookingDetailRS;
import com.hotelbeds.hotelapimodel.auto.messages.BookingListRS;
import com.hotelbeds.hotelapimodel.auto.messages.BookingRQ;
import com.hotelbeds.hotelapimodel.auto.messages.BookingRS;
import com.hotelbeds.hotelapimodel.auto.messages.CheckRateRQ;
import com.hotelbeds.hotelapimodel.auto.messages.CheckRateRS;
import com.hotelbeds.hotelapimodel.auto.messages.GenericResponse;
import com.hotelbeds.hotelapimodel.auto.messages.StatusRS;
import com.hotelbeds.hotelcontentapi.auto.convert.json.DateSerializer;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentRequest;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;
import com.hotelbeds.hotelcontentapi.auto.messages.Accommodation;
import com.hotelbeds.hotelcontentapi.auto.messages.Board;
import com.hotelbeds.hotelcontentapi.auto.messages.Category;
import com.hotelbeds.hotelcontentapi.auto.messages.Chain;
import com.hotelbeds.hotelcontentapi.auto.messages.Currency;
import com.hotelbeds.hotelcontentapi.auto.messages.Facility;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityGroup;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityType;
import com.hotelbeds.hotelcontentapi.auto.messages.GroupCategory;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageType;
import com.hotelbeds.hotelcontentapi.auto.messages.Issue;
import com.hotelbeds.hotelcontentapi.auto.messages.Language;
import com.hotelbeds.hotelcontentapi.auto.messages.Promotion;
import com.hotelbeds.hotelcontentapi.auto.messages.RateComments;
import com.hotelbeds.hotelcontentapi.auto.messages.Room;
import com.hotelbeds.hotelcontentapi.auto.messages.Segment;
import com.hotelbeds.hotelcontentapi.auto.messages.Terminal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
@Slf4j
@Data
public class HotelApiClient implements AutoCloseable {

    public static final String APIKEY_PROPERTY_NAME = "hotelapi.apikey";
    public static final String SHAREDSECRET_PROPERTY_NAME = "hotelapi.sharedsecret";
    public static final String VERSION_PROPERTY_NAME = "hotelapi.version";
    public static final String SERVICE_PROPERTY_NAME = "hotelapi.service";
    public static final String HOTELAPI_PROPERTIES_FILE_NAME = "hotelapi.properties";
    public static final String DEFAULT_LANGUAGE = "ENG";
    private static final int REST_TEMPLATE_READ_TIME_OUT = 5000;


    private static final String API_KEY_HEADER_NAME = "Api-Key";
    private static final String SIGNATURE_HEADER_NAME = "X-Signature";

    private final String apiKey;
    private final String sharedSecret;
    private final HotelApiVersion hotelApiversion;
    private final HotelApiService hotelApiService;
    private Properties properties = null;
    private RestTemplate restTemplate = null;
    private boolean initialised = false;
    private int readTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectionRequestTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private ExecutorService executorService = null;

    public HotelApiClient() {
        this((String) null, null);
    }

    public HotelApiClient(HotelApiService service) {
        this(service, null, null);
    }

    public HotelApiClient(HotelApiVersion version, HotelApiService service) {
        this(version, service, null, null);
    }

    public HotelApiClient(String apiKey, String sharedSecret) {
        this(HotelApiVersion.DEFAULT, HotelApiService.TEST, apiKey, sharedSecret);
    }

    public HotelApiClient(HotelApiService service, String apiKey, String sharedSecret) {
        this(HotelApiVersion.DEFAULT, service, apiKey, sharedSecret);
    }

    public HotelApiClient(HotelApiVersion version, HotelApiService service, String apiKey, String sharedSecret) {
        this.apiKey = getHotelApiKey(apiKey);
        this.sharedSecret = getHotelApiSharedSecret(sharedSecret);
        hotelApiversion = getHotelApiVersion(version);
        hotelApiService = getHotelApiService(service);
        if (StringUtils.isBlank(this.apiKey) || hotelApiversion == null || hotelApiService == null || StringUtils.isBlank(this.sharedSecret)) {
            throw new IllegalArgumentException(
                "HotelApiClient cannot be created without specifying an API key, Shared Secret, the Hotel API version and the service you are connecting to.");
        }
        properties = new Properties();
    }

    public void init() {
        restTemplate = getRestTemplate();
        initialised = true;
        executorService = Executors.newFixedThreadPool(8);
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    private String getHotelApiProperty(String propertyName) {
        if (properties == null) {
            try (InputStream hotelApiPropertiesIS = ClassLoader.getSystemResourceAsStream(HOTELAPI_PROPERTIES_FILE_NAME)) {
                properties = new Properties();
                if (hotelApiPropertiesIS != null) {
                    properties.load(hotelApiPropertiesIS);
                }
            } catch (IOException e) {
                log.error("Error loading properties (){}.", HOTELAPI_PROPERTIES_FILE_NAME, e);
            }
        }
        return properties.getProperty(propertyName);
    }

    private String getHotelApiKey(String providedDefault) {
        String result = providedDefault;
        String fromProperties = getValueFromProperties("Api Key", APIKEY_PROPERTY_NAME);
        if (fromProperties != null) {
            result = fromProperties;
        }
        return result;
    }

    private String getHotelApiSharedSecret(String providedDefault) {
        String result = providedDefault;
        String fromProperties = getValueFromProperties("Shared Secret", SHAREDSECRET_PROPERTY_NAME);
        if (fromProperties != null) {
            result = fromProperties.trim();
        }
        return result;
    }

    private HotelApiVersion getHotelApiVersion(HotelApiVersion providedDefault) {
        HotelApiVersion result = providedDefault;
        String fromProperties = getValueFromProperties("HotelAPI version", VERSION_PROPERTY_NAME);
        if (fromProperties != null) {
            try {
                result = HotelApiVersion.valueOf(fromProperties.trim());
            } catch (Exception e) {
                log.error("Incorrect value provided for HotelAPI version: {}, it has to be one of {}. Using {}", new Object[] {
                    fromProperties, HotelApiVersion.values(), providedDefault});
                result = providedDefault;
            }
        }
        return result;
    }

    private HotelApiService getHotelApiService(HotelApiService providedDefault) {
        HotelApiService result = providedDefault;
        String fromProperties = getValueFromProperties("HotelAPI service", SERVICE_PROPERTY_NAME);
        if (fromProperties != null) {
            try {
                result = HotelApiService.valueOf(fromProperties.trim());
            } catch (Exception e) {
                log.error("Incorrect value provided for HotelAPI service: {}, it has to be one of {}. Using {}", new Object[] {
                    fromProperties, HotelApiService.values(), providedDefault});
                result = providedDefault;
            }
        }
        return result;
    }

    private String getValueFromProperties(String name, String propertyName) {
        String apiKey = System.getProperty(propertyName);
        if (apiKey == null) {
            apiKey = getHotelApiProperty(propertyName);
            if (apiKey != null) {
                log.debug("{} loaded from properties file. {}", name, apiKey);
            } else {
                log.debug("No {} loaded from properties, value not specified.", name);
            }
        } else {
            apiKey = apiKey.trim();
            log.debug("{} loaded from system properties. {}", name, apiKey);
        }
        return apiKey;
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS availability(Availability availability) throws HotelApiSDKException {
        AvailabilityRQ availabilityRQ = availability.toAvailabilityRQ();
        AvailabilityRS availabilityRS = doAvailability(availabilityRQ);
        return availabilityRS;
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS doAvailability(final AvailabilityRQ request) throws HotelApiSDKException {
        final ResponseEntity<AvailabilityRS> responseEntity = callRemoteAPI(request, HotelApiPaths.AVAILABILITY);
        final AvailabilityRS response = responseEntity.getBody();
        return response;
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingListRS list(LocalDate start, LocalDate end, int from, int to, boolean includeCancelled, FilterType filterType)
        throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        params.put("from", Integer.toString(from));
        params.put("to", Integer.toString(to));
        params.put("includeCancelled", Boolean.toString(includeCancelled));
        params.put("filterType", filterType.name());
        final ResponseEntity<BookingListRS> responseEntity = callRemoteAPI(params, HotelApiPaths.BOOKING_LIST);
        return responseEntity.getBody();
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingListRS list(BookingList bookingList) throws HotelApiSDKException {
        return list(bookingList.getFromDate(), bookingList.getToDate(), bookingList.getFrom(), bookingList.getTo(),
            !bookingList.isExcludeCancelled(), bookingList.getUsingDate());
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingDetailRS detail(String bookingId) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        final ResponseEntity<BookingDetailRS> responseEntity = callRemoteAPI(params, HotelApiPaths.BOOKING_DETAIL);
        return responseEntity.getBody();
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS confirm(Booking booking) throws HotelApiSDKException {
        BookingRQ bookingRQ = booking.toBookingRQ();
        BookingRS bookingRS = doBookingConfirm(bookingRQ);
        return bookingRS;
    }


    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS doBookingConfirm(BookingRQ request) throws HotelApiSDKException {
        final ResponseEntity<BookingRS> responseEntity = callRemoteAPI(request, HotelApiPaths.BOOKING_CONFIRM);
        final BookingRS response = responseEntity.getBody();
        return response;
    }

    public BookingCancellationRS cancel(String bookingId) throws HotelApiSDKException {
        return cancel(bookingId, false);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingCancellationRS cancel(String bookingId, boolean isSimulation) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        params.put("flag", isSimulation ? CancellationFlags.SIMULATION.name() : CancellationFlags.CANCELLATION.name());
        final ResponseEntity<BookingCancellationRS> responseEntity = callRemoteAPI(params, HotelApiPaths.BOOKING_CANCEL);
        return responseEntity.getBody();
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS check(BookingCheck bookingCheck) throws HotelApiSDKException {
        CheckRateRQ bookingCheckRQ = bookingCheck.toCheckRateRQ();
        CheckRateRS bookingCheckRS = doCheckRate(bookingCheckRQ);
        return bookingCheckRS;
    }


    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS doCheckRate(CheckRateRQ request) throws HotelApiSDKException {
        final ResponseEntity<CheckRateRS> responseEntity = callRemoteAPI(request, HotelApiPaths.CHECK_AVAIL);
        final CheckRateRS response = responseEntity.getBody();
        return response;
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public StatusRS status() throws HotelApiSDKException {
        final ResponseEntity<StatusRS> responseEntity = callRemoteAPI(HotelApiPaths.STATUS);
        return responseEntity.getBody();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// HOTEL CONTENT
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    //    public BoardsRS getBoards(BoardsRQ request) throws HotelApiSDKException {
    //        final Map<String, String> params = new HashMap<>();
    //        addCommonParameters(request, ContentType.BOARD, params);
    //        final ResponseEntity<BoardsRS> responseEntity = callRemoteContentAPI(request, params, ContentType.BOARD);
    //        final BoardsRS response = responseEntity.getBody();
    //        return response;
    //    }

    public List<Board> getAllBoards(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.BOARD);
    }

    public List<Chain> getAllChains(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CHAIN);
    }

    public List<Accommodation> getAllAccommodations(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ACCOMODATION);
    }

    public List<Category> getAllCategories(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CATEGORY);
    }

    public List<RateComments> getAllRateComments(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.RATECOMMENT);
    }

    public List<Currency> getAllCurrencies(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CURRENCY);
    }

    public List<Facility> getAllFacilities(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY);
    }

    public List<FacilityGroup> getAllFacilityGroups(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY_GROUP);
    }

    public List<FacilityType> getAllFacilityTypes(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY_TYPE);
    }

    public List<Issue> getAllIssues(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ISSUE);
    }

    public List<Language> getAllLanguages(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.LANGUAGE);
    }

    public List<Promotion> getAllPromotions(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.PROMOTION);
    }

    public List<Room> getAllRooms(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ROOM);
    }

    public List<Segment> getAllSegments(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.SEGMENT);
    }

    public List<Terminal> getAllTerminals(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.TERMINAL);
    }

    public List<ImageType> getAllImageTypes(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.IMAGE_TYPE);
    }

    public List<GroupCategory> getAllGroupCategories(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.GROUP_CATEGORY);
    }

    @Data
    private class RemoteApiCallable implements Callable<AbstractGenericContentResponse> {
        private final ContentType type;
        private final AbstractGenericContentRequest abstractGenericContentRequest;
        private final Map<String, String> callableParams;

        @Override
        public AbstractGenericContentResponse call() throws Exception {
            ResponseEntity<AbstractGenericContentResponse> responseEntity = callRemoteContentAPI(abstractGenericContentRequest, callableParams, type);
            return responseEntity.getBody();
        }
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    @SuppressWarnings("unchecked")
    public <T> List<T> getAllElements(final String language, final boolean useSecondaryLanguage, ContentType type) throws HotelApiSDKException {
        AbstractGenericContentRequest abstractGenericContentRequest;
        List<T> elements = new ArrayList<>();
        int total = 0;
        try {
            abstractGenericContentRequest = type.getRequestClass().newInstance();
            abstractGenericContentRequest.setFields(new String[] {"all"});

            Set<Callable<AbstractGenericContentResponse>> callables = new HashSet<Callable<AbstractGenericContentResponse>>();

            final Map<String, String> params = new HashMap<>();
            addCommonParameters(abstractGenericContentRequest, type, params);
            params.put("from", Integer.toString(1));
            params.put("to", Integer.toString(1000));
            ResponseEntity<AbstractGenericContentResponse> responseEntity = callRemoteContentAPI(abstractGenericContentRequest, params, type);
            AbstractGenericContentResponse response = responseEntity.getBody();
            log.info("Retrieving {} elements of type {}...", response.getTotal(), type);
            total = response.getTotal();
            Collection<T> responseElements = (Collection<T>) type.getResultsFunction().apply(response);
            if (CollectionUtils.isNotEmpty(responseElements)) {
                elements.addAll(responseElements);
                int from = response.getFrom() + 1000;
                while (from < response.getTotal()) {
                    params.put("from", Integer.toString(from));
                    params.put("to", Integer.toString(from + 999));
                    //
                    final Map<String, String> callableParams = new HashMap<>();
                    callableParams.putAll(params);
                    Callable<AbstractGenericContentResponse> callable = new RemoteApiCallable(type, abstractGenericContentRequest, callableParams);
                    callables.add(callable);
                    from += 1000;
                }
            }
            List<Future<AbstractGenericContentResponse>> futures = executorService.invokeAll(callables);
            for (Future<AbstractGenericContentResponse> future : futures) {
                try {
                    response = future.get();
                    responseElements = (Collection<T>) type.getResultsFunction().apply(response);
                    if (CollectionUtils.isNotEmpty(responseElements)) {
                        elements.addAll(responseElements);
                    }
                } catch (ExecutionException e) {
                    log.error("Error while retrieving a block of elements of type {}", type.name(), e);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HotelApiSDKException(new HotelbedsError("SDK Configuration error", e.getCause().getMessage()));
        } catch (InterruptedException e) {
            throw new HotelApiSDKException(new HotelbedsError("Interrupted while calling service", e.getCause().getMessage()));
        }
        log.info("Retrieved {} of {} elements of type {}", new Object[] {
            elements.size(), total, type});
        return elements;
    }

    private void addCommonParameters(AbstractGenericContentRequest request, final ContentType type, final Map<String, String> params) {
        params.put("type", type.getUrlTag());
        params.put("language", request.getLanguage() != null ? request.getLanguage() : "ENG");
        params.put("from", request.getFrom() != null ? request.getFrom().toString() : "1");
        params.put("to", request.getTo() != null ? request.getTo().toString() : "100");
        params.put("fields", String.join(",", request.getFields()));
        params.put("useSecondaryLanguage", Boolean.toString(request.isUseSecondaryLanguage()));
        params.put("lastUpdateTime", request.getLastUpdateTime() != null ? DateSerializer.REST_FORMATTER.format(request.getLastUpdateTime()) : "");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// INTERNALS
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends GenericResponse> ResponseEntity<T> callRemoteAPI(HotelApiPaths path) throws HotelApiSDKException {
        return callRemoteAPI(null, null, path);
    }

    private <T extends GenericResponse> ResponseEntity<T> callRemoteAPI(final Map<String, String> params, HotelApiPaths path)
        throws HotelApiSDKException {
        return callRemoteAPI(null, params, path);
    }

    private <T extends GenericResponse> ResponseEntity<T> callRemoteAPI(final AbstractGenericRequest request, HotelApiPaths path)
        throws HotelApiSDKException {
        return callRemoteAPI(request, null, path);
    }

    private <T extends GenericResponse> ResponseEntity<T> callRemoteAPI(final AbstractGenericRequest abstractGenericRequest,
        final Map<String, String> params, HotelApiPaths path) throws HotelApiSDKException {
        if (isInitialised()) {
            final HttpMethod httpMethod = path.getHttpMethod();
            final String url = path.getUrl(hotelApiService, hotelApiversion, params);
            HttpEntity<?> httpEntity = null;
            if (httpMethod.equals(HttpMethod.POST) && abstractGenericRequest != null) {
                httpEntity = new HttpEntity<>(abstractGenericRequest, getHeaders(httpMethod));
            } else {
                httpEntity = new HttpEntity<>(getHeaders(httpMethod));
            }
            try {
                @SuppressWarnings("unchecked")
                final ResponseEntity<T> responseEntity =
                    (ResponseEntity<T>) restTemplate.exchange(url, httpMethod, httpEntity, path.getResponseClass());
                if (responseEntity.getBody().getError() != null) {
                    throw new HotelApiSDKException(responseEntity.getBody().getError());
                }
                return responseEntity;
            } catch (HotelApiSDKException e) {
                throw e;
            } catch (ResourceAccessException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelApiSDKException(new HotelbedsError("Timeout", e.getCause().getMessage()));
                } else {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getCause().getMessage()));
                }
            } catch (Exception e) {
                throw new HotelApiSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelApiSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private <T extends AbstractGenericContentResponse> ResponseEntity<T> callRemoteContentAPI(
        final AbstractGenericContentRequest abstractGenericContentResponse, final Map<String, String> params, ContentType type)
        throws HotelApiSDKException {
        if (isInitialised()) {
            final HttpMethod httpMethod = HttpMethod.GET;
            final String url = type.getPath().getUrl(hotelApiService, hotelApiversion, params);
            log.debug("HTTP Get: {}", url);
            HttpEntity<?> httpEntity = new HttpEntity<>(getHeaders(httpMethod));
            try {
                @SuppressWarnings("unchecked")
                final ResponseEntity<T> responseEntity =
                    (ResponseEntity<T>) restTemplate.exchange(url, httpMethod, httpEntity, type.getResponseClass());
                if (responseEntity.getBody().getError() != null) {
                    throw new HotelApiSDKException(responseEntity.getBody().getError());
                }
                return responseEntity;
            } catch (HotelApiSDKException e) {
                throw e;
            } catch (ResourceAccessException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelApiSDKException(new HotelbedsError("Timeout", e.getCause().getMessage()));
                } else {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getCause().getMessage()));
                }
            } catch (Exception e) {
                throw new HotelApiSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelApiSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private MultiValueMap<String, String> getHeaders(HttpMethod httpMethod) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(API_KEY_HEADER_NAME, apiKey);
        headers.add("User-Agent", "hotel-api-sdk-java, " + getClass().getPackage().getImplementationVersion());
        // Hash the Api Key + Shared Secret + Current timestamp in seconds
        String signature = org.apache.commons.codec.digest.DigestUtils.sha256Hex(apiKey + sharedSecret + System.currentTimeMillis() / 1000);
        headers.add(SIGNATURE_HEADER_NAME, signature);
        switch (httpMethod) {
            case GET:
            case DELETE:
                headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
                break;
            case POST:
            case PUT:
                headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                break;
            default:
                break;
        }
        return headers;
    }

    private RestTemplate getRestTemplate() {
        final LoggingRequestInterceptor loggingRequestInterceptor = new LoggingRequestInterceptor();
        final List<ClientHttpRequestInterceptor> arrayClientHttpRequestInterceptor = new ArrayList<ClientHttpRequestInterceptor>();
        arrayClientHttpRequestInterceptor.add(loggingRequestInterceptor);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectTimeout);
        factory.setConnectionRequestTimeout(connectionRequestTimeout);
        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
        restTemplate.setInterceptors(arrayClientHttpRequestInterceptor);
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), arrayClientHttpRequestInterceptor));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setErrorHandler(new RestTemplateSpecificErrorHandler());
        return restTemplate;
    }

    @Override
    public void close() {
        try {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error closing HotelAPI client resources", e);
        }
    }
}
