package com.hotelbeds.hotelapimodel.auto.convert.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(final String localDate) throws Exception {
        return LocalDate.parse(localDate, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String marshal(final LocalDate localDate) throws Exception {
        String result = null;
        if (localDate != null) {
            result = localDate.format(DateTimeFormatter.ISO_DATE);
        }
        return result;
    }
}
