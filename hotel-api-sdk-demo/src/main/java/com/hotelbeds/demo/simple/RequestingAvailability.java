package com.hotelbeds.demo.simple;

import java.time.LocalDate;

import com.hotelbeds.distribution.hotel_api_model.auto.messages.AvailabilityRS;
import com.hotelbeds.distribution.hotel_api_model.auto.model.Hotel;
import com.hotelbeds.distribution.hotel_api_model.auto.model.Rate;
import com.hotelbeds.distribution.hotel_api_model.auto.model.Room;
import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.AvailRoom;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.AvailRoom.AvailRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Availability;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Availability.AvailabilityBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelSDKException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestingAvailability
{

  private static final String ENGLISH = "ENG";
  private static final String LONDON = "LON";

  public static void main(String[] args)
  {
    // First initialise the client
    HotelApiClient apiClient = new HotelApiClient();
    apiClient.setReadTimeout(20000);
    apiClient.init();
    // We want an availability starting in one month... for 3 nights
    LocalDate checkIn = LocalDate.now().plusMonths(1);
    LocalDate checkOut = LocalDate.now().plusMonths(1).plusDays(3);
    // For 2 adults and a child of four (age is mandatory for children)
    AvailRoomBuilder availRoom = AvailRoom.builder().adults(2).children(1).childOf(4);
    // Create an availability object using the mandatory fields
    // Mandatory are:
    // .- Language
    // .- Checking & checkout dates
    // .- Room data
    // .- one main filtering option
    AvailabilityBuilder availabilityBuilder = Availability.builder().language(ENGLISH).checkIn(checkIn).checkOut(checkOut).addRoom(availRoom);
    // Then add one of the main filtering options (uncomment just one and comment the rest)
    // You can filter by destination
    availabilityBuilder.destination(LONDON);
    // xor you can filter by hotel codes
    // availabilityBuilder.includeHotels(Arrays.asList(191438, 6607, 232920));
    // xor you can filter by geoposition (a given square)
    // availabilityBuilder.withinThis(new Availability.Square("45.37680856570233", "-2.021484375", "38.548165423046584", "8.658203125"));
    // xor you can filter by geoposition (a circle)
    // availabilityBuilder.withinThis(new Availability.Circle("51.49", "-0.14", 200));

    // Optional filters

    // Perform the request and browse the results
    try
    {
      log.info("Requesting availability...");
      AvailabilityRS availabilityRS = apiClient.availability(availabilityBuilder.build());
      // Uncomment the following line if you want to see the actual JSON response
      // log.info("AvailabilityRS: {}", LoggingRequestInterceptor.writeJSON(availabilityRS, true));
      if (availabilityRS.getHotels() != null)
      {
        log.info("There are {} hotels", availabilityRS.getHotels().getTotal());
        if (availabilityRS.getHotels().getTotal() > 0)
        {
          for (Hotel hotel : availabilityRS.getHotels().getHotels())
          {
            log.info("-----------------------------------------");
            log.info("Hotel ({}-{}) - {} ({}; from {}{})", new Object[]
            { hotel.getDestinationCode(), hotel.getCode(), hotel.getName(), hotel.getCategoryName(), hotel.getMinRate(), hotel.getCurrency() });
            for (Room room : hotel.getRooms())
            {
              for (Rate rate : room.getRates())
              {
                log.info("{} - {} {}{}", new Object[]
                { room.getName(), rate.getBoardName(), rate.getSellingRate() != null ? rate.getSellingRate() : rate.getNet(), hotel.getCurrency() });
              }
            }
          }
        }
        else
        {
          log.info("Upps, no hotel for us!");
        }
      }
      else
      {
        log.info("No availability was obtained {}", availabilityRS.getError().getMessage());
      }
    }
    catch (HotelSDKException e)
    {
      log.error("Error requesting availability", e);
    }
  }

}
