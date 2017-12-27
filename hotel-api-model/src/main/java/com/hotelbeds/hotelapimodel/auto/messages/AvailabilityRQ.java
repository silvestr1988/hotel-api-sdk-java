/**
 * Autogenerated code by SdkModelGenerator.
 * Do not edit. Any modification on this file will be removed automatically after project build
 *
 */
package com.hotelbeds.hotelapimodel.auto.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.Accommodation;
import com.hotelbeds.hotelapimodel.auto.model.Boards;
import com.hotelbeds.hotelapimodel.auto.model.Destination;
import com.hotelbeds.hotelapimodel.auto.model.Filter;
import com.hotelbeds.hotelapimodel.auto.model.GeoLocation;
import com.hotelbeds.hotelapimodel.auto.model.HotelsFilter;
import com.hotelbeds.hotelapimodel.auto.model.KeywordsFilter;
import com.hotelbeds.hotelapimodel.auto.model.Occupancy;
import com.hotelbeds.hotelapimodel.auto.model.ReviewFilter;
import com.hotelbeds.hotelapimodel.auto.model.Rooms;
import com.hotelbeds.hotelapimodel.auto.model.Source;
import com.hotelbeds.hotelapimodel.auto.model.Stay;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "availabilityRQ", namespace = "http://www.hotelbeds.com/schemas/messages")
@JsonInclude(Include.NON_NULL)
@ToString
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class AvailabilityRQ extends AbstractGenericRequest {

	@XmlElement
	@NotNull
	@Valid
	private Stay stay;
	@XmlElementWrapper(name = "occupancies")
	@XmlElement(name = "occupancy")
	@Valid
	private List<Occupancy> occupancies;
	@XmlElement
	@Valid
	private GeoLocation geolocation;
	@XmlElement
	@Valid
	private Destination destination;
	@XmlElement(name = "keywords")
	@JsonProperty("keywords")
	@Valid
	private KeywordsFilter keywordsFilter;
	@XmlElement(name = "hotels")
	@JsonProperty("hotels")
	@Valid
	private HotelsFilter hotelsFilter;
	@XmlElementWrapper(name = "reviews")
	@XmlElement(name = "review")
	@JsonProperty("reviews")
	@Valid
	private List<ReviewFilter> reviewsFilter;
	@XmlElement
	@Valid
	private Filter filter;
	@XmlElement
	@Valid
	private Boards boards;
	@XmlElement
	@Valid
	private Rooms rooms;
	@XmlAttribute
	private Boolean dailyRate;
	@XmlAttribute
	private String sourceMarket;
	@XmlElementWrapper(name = "accommodations")
	@XmlElement(name = "accommodation")
	@Valid
	private List<Accommodation> accommodations;
	@XmlElement
	@Valid
	private Source source;
	@XmlElement
	private Boolean aifUse;
	@XmlAttribute
	private Integer platform;


}
