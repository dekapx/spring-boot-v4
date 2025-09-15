package com.dekapx.apps.service;

import com.dekapx.apps.factory.FileWriterFactory;
import com.dekapx.apps.model.FileModel;
import com.dekapx.apps.writer.FileWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileWriterFactory fileWriterFactory;

    @Override
    public void write(FileModel fileModel) {
        FileWriter fileWriter = this.fileWriterFactory
                .getFileWriter(fileModel.getFileType().getFactoryBeanName());
        fileWriter.write(fileModel.getContents());
    }
}
