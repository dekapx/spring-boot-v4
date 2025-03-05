package com.dekapx.apps.factory;


import com.dekapx.apps.model.FileType;
import com.dekapx.apps.writer.CsvFileWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FileWriterFactoryITest {
    @Autowired
    private FileWriterFactory fileWriterFactory;

    @Test
    public void givenBeanNameShouldLookupAndReturnFileWriter() {
        var fileWriter = this.fileWriterFactory.getFileWriter(FileType.CSV.getFactoryBeanName());
        assertThat(fileWriter).isNotNull()
                .satisfies(o -> {
                    assertThat(o).isInstanceOf(CsvFileWriter.class);
                });
    }
}
