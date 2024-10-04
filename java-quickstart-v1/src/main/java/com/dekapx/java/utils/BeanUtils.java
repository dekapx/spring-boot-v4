package com.dekapx.java.utils;

import lombok.experimental.UtilityClass;

import java.beans.Introspector;

@UtilityClass
public class BeanUtils {
    public static String generateBeanName(Class clazz) {
     return Introspector.decapitalize(clazz.getSimpleName());
    }
}
