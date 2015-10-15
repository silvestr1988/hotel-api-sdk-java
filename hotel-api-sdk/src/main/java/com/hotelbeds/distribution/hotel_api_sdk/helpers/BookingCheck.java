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


import java.util.ArrayList;
import java.util.List;

import com.hotelbeds.distribution.hotel_api_model.auto.common.SimpleTypes.HotelbedsCustomerType;
import com.hotelbeds.distribution.hotel_api_model.auto.messages.CheckRateRQ;
import com.hotelbeds.distribution.hotel_api_model.auto.model.BookingRoom;
import com.hotelbeds.distribution.hotel_api_model.auto.model.Pax;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.ConfirmRoom.ConfirmRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.RoomDetail.GuestType;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class BookingCheck {

    @Singular
    private List<ConfirmRoom> rooms;

    public void validate() {

    }

    public CheckRateRQ toCheckRateRQ() {
        validate();
        CheckRateRQ checkRateRQ = new CheckRateRQ();
        //
        checkRateRQ.setRooms(new ArrayList<>());
        for (ConfirmRoom room : rooms) {
            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setRateKey(room.getRatekey());
            bookingRoom.setPaxes(new ArrayList<>());
            for (RoomDetail detail : room.getDetails()) {
                Pax pax = new Pax();
                pax.setType(detail.getType() == GuestType.ADULT ? HotelbedsCustomerType.AD : HotelbedsCustomerType.CH);
                pax.setAge(detail.getAge());
                pax.setName(detail.getName());
                pax.setSurname(detail.getSurname());
                bookingRoom.getPaxes().add(pax);
            }
            checkRateRQ.getRooms().add(bookingRoom);
        }
        return checkRateRQ;
    }

    public static class BookingCheckBuilder {

        public BookingCheckBuilder addRoom(String ratekey, ConfirmRoomBuilder roomBuilder) {
            room(roomBuilder.ratekey(ratekey).build());
            return this;
        }
    }
}
