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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.Availability;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Booking;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.BookingCheck;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.LoggingRequestInterceptor;
import com.hotelbeds.distribution.hotel_api_sdk.types.AllowedMethod;
import com.hotelbeds.distribution.hotel_api_sdk.types.CancellationFlags;
import com.hotelbeds.distribution.hotel_api_sdk.types.FilterType;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiPaths;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiService;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiVersion;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelSDKException;
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
import com.hotelbeds.hotelapimodel.auto.messages.HotelbedsError;
import com.hotelbeds.hotelapimodel.auto.messages.StatusRS;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
@Slf4j
@Data
public class HotelApiClient {

    public static final String APIKEY_PROPERTY_NAME = "hotelapi.apikey";
    public static final String SHAREDSECRET_PROPERTY_NAME = "hotelapi.sharedsecret";
    public static final String VERSION_PROPERTY_NAME = "hotelapi.version";
    public static final String SERVICE_PROPERTY_NAME = "hotelapi.service";
    public static final String HOTELAPI_PROPERTIES_FILE_NAME = "hotelapi.properties";
    public static final String DEFAULT_LANGUAGE = "ENG";
    private static final int REST_TEMPLATE_READ_TIME_OUT = 5000;

    private static final String HOTEL_API_URL_PROPERTY = "hotel-api.url";
    private static final String API_KEY_HEADER_NAME = "Api-Key";
    private static final String SIGNATURE_HEADER_NAME = "X-Signature";

    private final String apiKey;
    private final String sharedSecret;
    private final HotelApiVersion version;
    private final HotelApiService service;
    private final String basePath;
    private Properties properties = null;
    private OkHttpClient restTemplate = null;
    private boolean initialised = false;
    private int readTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectionRequestTimeout = REST_TEMPLATE_READ_TIME_OUT;

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
        this.version = getHotelApiVersion(version);
        this.service = getHotelApiService(service);
        if (StringUtils.isBlank(this.apiKey) || this.version == null || this.service == null || StringUtils.isBlank(this.sharedSecret)) {
            throw new IllegalArgumentException(
                "HotelApiClient cannot be created without specifying an API key, Shared Secret, the Hotel API version and the service you are connecting to.");
        }
        String customUrl = System.getProperty(HOTEL_API_URL_PROPERTY);
        if (StringUtils.isBlank(customUrl)) {
            basePath = this.service.getVersion();
        } else {
            basePath = customUrl;
        }
        properties = new Properties();
    }

    public void init() {
        // @formatter:off
    restTemplate = new OkHttpClient.Builder()
        .writeTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS)
        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .addInterceptor(new LoggingRequestInterceptor())
        .build();
 // @formatter:on
        initialised = true;
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

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public AvailabilityRS availability(Availability availability) throws HotelSDKException {
        AvailabilityRQ availabilityRQ = availability.toAvailabilityRQ();
        AvailabilityRS availabilityRS = doAvailability(availabilityRQ);
        return availabilityRS;
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public AvailabilityRS doAvailability(final AvailabilityRQ request) throws HotelSDKException {
        return (AvailabilityRS) callRemoteAPI(request, HotelApiPaths.AVAILABILITY);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingListRS list(LocalDate start, LocalDate end, int from, int to, boolean includeCancelled, FilterType filterType)
        throws HotelSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        params.put("from", Integer.toString(from));
        params.put("to", Integer.toString(to));
        params.put("includeCancelled", Boolean.toString(includeCancelled));
        params.put("filterType", filterType.name());
        return (BookingListRS) callRemoteAPI(params, HotelApiPaths.BOOKING_LIST);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingDetailRS detail(String bookingId) throws HotelSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        return (BookingDetailRS) callRemoteAPI(params, HotelApiPaths.BOOKING_DETAIL);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingRS confirm(Booking booking) throws HotelSDKException {
        BookingRQ bookingRQ = booking.toBookingRQ();
        BookingRS bookingRS = doBookingConfirm(bookingRQ);
        return bookingRS;
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingRS doBookingConfirm(BookingRQ request) throws HotelSDKException {
        return (BookingRS) callRemoteAPI(request, HotelApiPaths.BOOKING_CONFIRM);
    }

    public BookingCancellationRS cancel(String bookingId) throws HotelSDKException {
        return cancel(bookingId, false);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingCancellationRS cancel(String bookingId, boolean isSimulation) throws HotelSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        params.put("flag", isSimulation ? CancellationFlags.SIMULATION.name() : CancellationFlags.CANCELLATION.name());
        return (BookingCancellationRS) callRemoteAPI(params, HotelApiPaths.BOOKING_CANCEL);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public CheckRateRS check(BookingCheck bookingCheck) throws HotelSDKException {
        CheckRateRQ bookingCheckRQ = bookingCheck.toCheckRateRQ();
        CheckRateRS bookingCheckRS = doCheckRate(bookingCheckRQ);
        return bookingCheckRS;
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public CheckRateRS doCheckRate(CheckRateRQ request) throws HotelSDKException {
        return (CheckRateRS) callRemoteAPI(request, HotelApiPaths.CHECK_AVAIL);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public StatusRS status() throws HotelSDKException {
        return (StatusRS) callRemoteAPI(HotelApiPaths.STATUS);
    }

    private GenericResponse callRemoteAPI(HotelApiPaths path) throws HotelSDKException {
        return callRemoteAPI(null, null, path);
    }

    private GenericResponse callRemoteAPI(final Map<String, String> params, HotelApiPaths path) throws HotelSDKException {
        return callRemoteAPI(null, params, path);
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest request, HotelApiPaths path) throws HotelSDKException {
        return callRemoteAPI(request, null, path);
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest abstractGenericRequest, final Map<String, String> params, HotelApiPaths path)
        throws HotelSDKException {
        if (isInitialised()) {
            final AllowedMethod allowedMethod = path.getAllowedMethod();
            final String url = path.getUrl(basePath, version, params);
            try {
                // @formatter:off
        Request.Builder requestBuilder = new Request.Builder()
            .headers(getHeaders(allowedMethod))
            .url(url);
        // @formatter:on
                switch (allowedMethod) {
                    case DELETE:
                        requestBuilder.delete(transformToRequestBody(abstractGenericRequest));
                        break;
                    case POST:
                        requestBuilder.post(transformToRequestBody(abstractGenericRequest));
                        break;
                    default:
                        break;
                }

                Response response = restTemplate.newCall(requestBuilder.build()).execute();
                try (ResponseBody body = response.body()) {
                    GenericResponse genericResponse = transformToGenericResponse(body);
                    if (genericResponse.getError() != null) {
                        throw new HotelSDKException(genericResponse.getError());
                    }
                    return genericResponse;
                }
            } catch (HotelSDKException e) {
                throw e;
            } catch (IOException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelSDKException(new HotelbedsError("Timeout", e.getCause().getMessage()));
                } else {
                    throw new HotelSDKException(new HotelbedsError("Error accessing API", e.getCause().getMessage()));
                }
            } catch (Exception e) {
                throw new HotelSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private GenericResponse transformToGenericResponse(ResponseBody body) {
        // TODO Auto-generated method stub
        return null;
    }

    private RequestBody transformToRequestBody(AbstractGenericRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    private Headers getHeaders(AllowedMethod httpMethod) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add(API_KEY_HEADER_NAME, apiKey);
        headersBuilder.add("User-Agent", "hotel-api-sdk-java, " + getClass().getPackage().getImplementationVersion());
        // Hash the Api Key + Shared Secret + Current timestamp in seconds
        headersBuilder.add(SIGNATURE_HEADER_NAME, DigestUtils.sha256Hex(apiKey + sharedSecret + System.currentTimeMillis() / 1000));
        switch (httpMethod) {
            case GET:
            case DELETE:
                headersBuilder.add("Accept", "application/json");
                break;
            case POST:
                // case PUT:
                headersBuilder.add("Content-Type", "application/json");
                break;
            default:
                break;
        }
        return headersBuilder.build();
    }

    // private RestTemplate getRestTemplate()
    // {
    // final LoggingRequestInterceptor loggingRequestInterceptor = new LoggingRequestInterceptor();
    // final List<ClientHttpRequestInterceptor> arrayClientHttpRequestInterceptor = new ArrayList<ClientHttpRequestInterceptor>();
    // arrayClientHttpRequestInterceptor.add(loggingRequestInterceptor);
    // HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    // factory.setReadTimeout(readTimeout);
    // factory.setConnectTimeout(connectTimeout);
    // factory.setConnectionRequestTimeout(connectionRequestTimeout);
    // final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
    // restTemplate.setInterceptors(arrayClientHttpRequestInterceptor);
    // restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), arrayClientHttpRequestInterceptor));
    // restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    // restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    // restTemplate.setErrorHandler(new RestTemplateSpecificErrorHandler());
    // return restTemplate;
    // }
}
