package com.hotelbeds.hotelapimodel.auto.convert.json;

/*
 * #%L HotelAPI Model %% Copyright (C) 2015 - 2018 HOTELBEDS TECHNOLOGY, S.L.U. %% This program is
 * free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hotelbeds.hotelapimodel.auto.util.AssignUtils;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public static final DateTimeFormatter REST_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LocalDateDeserializer() {
        this(null);
    }

    protected LocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        TextNode node = oc.readTree(jsonParser);
        String dateString = node.textValue();
        return LocalDate.parse(dateString, AssignUtils.REST_FORMATTER);
    }
}
