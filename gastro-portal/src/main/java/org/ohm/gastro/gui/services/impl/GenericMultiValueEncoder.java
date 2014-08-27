package org.ohm.gastro.gui.services.impl;

/**
 * Created by IntelliJ IDEA.
 * User: eugene
 * Date: 22.06.2009
 * Time: 14:03:11
 * To change this template use File | Settings | File Templates.
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.ohm.gastro.gui.services.MultiValueEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericMultiValueEncoder<T> implements MultiValueEncoder<T> {

    private final static Logger logger = LoggerFactory.getLogger(GenericMultiValueEncoder.class);
    private final BiMap<String, T> objects;

    public GenericMultiValueEncoder(List<T> list, String labelField) {
        Map<String, T> objects = Maps.newHashMap();
        list.stream().forEach(t -> {
            try {
                String property = BeanUtils.getProperty(t, labelField);
                objects.put(property, t);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        });
        this.objects = ImmutableBiMap.copyOf(objects);
    }

    public List<String> toClient(T obj) {
        return Lists.newArrayList(objects.inverse().get(obj));
    }

    @SuppressWarnings("Convert2MethodRef")
    public List<T> toValue(String[] ids) {
        return Arrays.stream(ids).map(t -> objects.get(t)).collect(Collectors.toList());
    }

}