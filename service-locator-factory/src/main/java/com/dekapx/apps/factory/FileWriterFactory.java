package com.dekapx.apps.factory;

import com.dekapx.apps.writer.FileWriter;

public interface FileWriterFactory {
    FileWriter getFileWriter(String beanName);
}
