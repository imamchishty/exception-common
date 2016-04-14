package com.shedhack.exception.core;

import java.util.Collection;
import java.util.Map;

/**
 * @author imamchishty
 *
 * Created simple string utils, could use external libraries
 * but I don't want to enforce any tran-dependencies.
 */
public class Utils {

    public static boolean isEmptyOrNull(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isCollectionNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isMapNullOrEmpty(Map<String, Object> map) {
        return map == null || map.isEmpty();
    }
}
