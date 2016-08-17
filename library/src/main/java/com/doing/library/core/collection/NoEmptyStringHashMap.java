package com.doing.library.core.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap：
 * key != null ; key.length() != 0
 * value != null ; !value.isEmpty()
 *
 */


public class NoEmptyStringHashMap extends HashMap<String, String> {

    public NoEmptyStringHashMap(final Map<String, String> map) {
        super();
        putAll(map);
    }

    @Override
    public String put(final String key, final String value) {
        if (value == null || value.isEmpty() || key.length() == 0) {
            return null;
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> map) {
        for (Map.Entry<? extends String, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
