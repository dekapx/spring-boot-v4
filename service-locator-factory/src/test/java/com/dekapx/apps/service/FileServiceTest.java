package com.dekapx.apps.service;

import com.dekapx.apps.model.FileModel;
import com.dekapx.apps.model.FileType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileServiceTest {
    @Autowired
    private FileService fileService;

    @ParameterizedTest
    @EnumSource(FileType.class)
    public void givenFileModel_shouldWriteContentsToFile(FileType fileType) {
        FileModel fileModel = buildFileModel(fileType);
        this.fileService.write(fileModel);
    }

    private FileModel buildFileModel(FileType fileType) {
        return FileModel.builder()
            .fileType(fileType)
            .contents("Sample Contents")
            .build();
    }
}
