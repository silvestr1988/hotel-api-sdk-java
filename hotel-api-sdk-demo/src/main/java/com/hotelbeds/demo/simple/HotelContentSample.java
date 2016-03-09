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
import com.hotelbeds.hotelcontentapi.auto.messages.BoardsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.RateComments;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelContentSample {

    public static void main(String[] args) {
        // Perform the request and browse the results
        try (final HotelApiClient apiClient = new HotelApiClient()) {
            apiClient.setReadTimeout(20000);
            apiClient.init();
            log.info("Requesting boards...");
            BoardsRQ boardsRQ = new BoardsRQ();
            boardsRQ.setLanguage("ENG");
            boardsRQ.setFrom(1);
            boardsRQ.setTo(100);
            boardsRQ.setFields(new String[] {
                "all"});
            //
            // BoardsRS boardsRS = apiClient.getBoards(boardsRQ);
            // //log.info("boardsRS: {}", LoggingRequestInterceptor.writeJSON(boardsRS, true));
            //            for (Board board : boardsRS.getBoards()) {
            //                log.info("Board: {}/{} - {}", new Object[] {
            //                    board.getCode(), board.getMultiLingualCode(), board.getDescription() != null ? board.getDescription().getContent() : ""});
            //            }
            //
            //            for (Board element : apiClient.getAllBoards("ENG", false)) {
            //                log.info("Board: {}/{} - {}", new Object[] {
            //                    element.getCode(), element.getMultiLingualCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //
            //            for (Chain element : apiClient.getAllChains("ENG", false)) {
            //                log.info("Chain: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //
            //            for (Accommodation element : apiClient.getAllAccommodations("ENG", false)) {
            //                log.info("Accommodation: {} - {}", new Object[] {
            //                    element.getCode(), element.getTypeDescription() != null ? element.getTypeDescription() : ""});
            //            }
            //
            //            for (Category element : apiClient.getAllCategories("ENG", false)) {
            //                log.info("Category: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }

            for (RateComments element : apiClient.getAllRateComments("ENG", false)) {
                log.info("RateComment: {} {}-{}", new Object[] {
                    element.getHotel(), element.getIncoming(), element.getCode()});
            }
            //            //
            //            for (Currency element : apiClient.getAllCurrencies("ENG", false)) {
            //                log.info("Currency: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (Facility element : apiClient.getAllFacilities("ENG", false)) {
            //                log.info("Facility: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (FacilityGroup element : apiClient.getAllFacilityGroups("ENG", false)) {
            //                log.info("FacilityGroup: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (FacilityType element : apiClient.getAllFacilityTypes("ENG", false)) {
            //                log.info("FacilityType: {}", new Object[] {
            //                    element});
            //            }
            //            //
            //            for (Issue element : apiClient.getAllIssues("ENG", false)) {
            //                log.info("Issue: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (Language element : apiClient.getAllLanguages("ENG", false)) {
            //                log.info("Language: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (Promotion element : apiClient.getAllPromotions("ENG", false)) {
            //                log.info("Promotion: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (Room element : apiClient.getAllRooms("ENG", false)) {
            //                log.info("Room: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription()});
            //            }
            //            //
            //            for (Segment element : apiClient.getAllSegments("ENG", false)) {
            //                log.info("Segment: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (Terminal element : apiClient.getAllTerminals("ENG", false)) {
            //                log.info("Terminal: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (ImageType element : apiClient.getAllImageTypes("ENG", false)) {
            //                log.info("ImageType: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }
            //            //
            //            for (GroupCategory element : apiClient.getAllGroupCategories("ENG", false)) {
            //                log.info("GroupCategory: {} - {}", new Object[] {
            //                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            //            }

        } catch (HotelApiSDKException e) {
            log.error("Error requesting content", e);
        }
    }

}
