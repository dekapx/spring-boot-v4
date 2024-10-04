package com.dekapx.java.basics;

import static com.dekapx.java.utils.BeanUtils.generateBeanName;

class DummyProcessor {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class Test {
    public static void main(String[] args) {
        System.out.println(generateBeanName(DummyProcessor.class));
    }
}

