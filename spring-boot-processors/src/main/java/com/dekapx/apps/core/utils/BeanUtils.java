package com.dekapx.apps.core.utils;

import lombok.experimental.UtilityClass;

import java.beans.Introspector;

@UtilityClass
public class BeanUtils {
    public static String generateServiceLocatorLookupBeanName(final Class clazz) {
        return Introspector.decapitalize(clazz.getSimpleName());
    }
}
