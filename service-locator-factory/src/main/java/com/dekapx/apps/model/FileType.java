package com.dekapx.apps.model;

import com.dekapx.apps.writer.CsvFileWriter;
import com.dekapx.apps.writer.FileWriter;
import com.dekapx.apps.writer.TextFileWriter;
import com.dekapx.apps.writer.XmlFileWriter;

import static com.dekapx.apps.util.BeanUtils.generateBeanName;

public enum FileType {
    CSV (CsvFileWriter.class),
    TEXT (TextFileWriter.class),
    XML (XmlFileWriter.class);

    private final Class<? extends FileWriter> clazz;
    FileType(Class<? extends FileWriter> clazz) {
        this.clazz = clazz;
    }

    public String getFactoryBeanName() {
        return generateBeanName(this.clazz);
    }
}
