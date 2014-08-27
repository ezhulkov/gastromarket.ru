package org.ohm.gastro.gui.services;

/**
 * Created by IntelliJ IDEA.
 * User: eugene
 * Date: 22.06.2009
 * Time: 13:54:07
 * To change this template use File | Settings | File Templates.
 */

import java.util.List;

public interface MultiValueEncoder<V> {

    List<String> toClient(V value);

    List<V> toValue(String[] clientValue);

}