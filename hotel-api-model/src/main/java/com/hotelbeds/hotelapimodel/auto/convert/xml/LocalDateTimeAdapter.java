package com.hotelbeds.hotelapimodel.auto.convert.xml;


    
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    import javax.xml.bind.annotation.adapters.XmlAdapter;

    /**
     * The Class LocalDateTimeAdapter.
     */
    public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

        @Override
        public LocalDateTime unmarshal(final String localDateTime) throws Exception {
            return LocalDateTime.parse(localDateTime, DateTimeFormatter.ISO_DATE_TIME);
        }

        @Override
        public String marshal(final LocalDateTime localDateTime) throws Exception {
            String result = null;
            if (localDateTime != null) {
                return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
            }
            return result;
        }

    }


