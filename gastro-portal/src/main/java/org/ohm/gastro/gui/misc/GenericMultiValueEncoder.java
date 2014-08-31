package org.ohm.gastro.gui.misc;

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
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.ohm.gastro.misc.Throwables.propagate;

public class GenericMultiValueEncoder<T> implements MultiValueEncoder<T> {

    private final static Logger logger = LoggerFactory.getLogger(GenericMultiValueEncoder.class);
    private final BiMap<String, T> objects;

    public GenericMultiValueEncoder(List<T> list, final String labelField) {
        this.objects = ImmutableBiMap.copyOf(list.stream()
                                                     .collect(Collectors.toMap(t -> propagate(() -> BeanUtils.getProperty(t, labelField)),
                                                                               t -> t)));
    }

    public List<String> toClient(T obj) {
        return Lists.newArrayList(objects.inverse().get(obj));
    }

    @SuppressWarnings("Convert2MethodRef")
    public List<T> toValue(String[] ids) {
        return Arrays.stream(ids).map(t -> objects.get(t)).collect(Collectors.toList());
    }

}