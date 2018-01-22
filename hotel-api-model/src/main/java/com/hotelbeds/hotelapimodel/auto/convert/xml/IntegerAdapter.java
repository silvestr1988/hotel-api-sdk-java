package com.hotelbeds.hotelapimodel.auto.convert.xml;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;



/**
 * The Class IntegerAdapter.
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(final String integer) throws Exception {
        Integer result = null;
        if (StringUtils.isNotBlank(integer)) {
            result = Integer.valueOf(integer);
        }
        return result;
    }

    @Override
    public String marshal(final Integer integer) throws Exception {
        String result = null;
        if (integer != null) {
            result = integer.toString();
        }
        return result;
    }

}
