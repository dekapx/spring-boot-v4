package com.dekapx.apps.factory;


import com.dekapx.apps.model.FileType;
import com.dekapx.apps.writer.CsvFileWriter;
import com.dekapx.apps.writer.FileWriter;
import com.dekapx.apps.writer.TextFileWriter;
import com.dekapx.apps.writer.XmlFileWriter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.dekapx.apps.model.FileType.CSV;
import static com.dekapx.apps.model.FileType.TEXT;
import static com.dekapx.apps.model.FileType.XML;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FileWriterFactoryITest {
    @Autowired
    private FileWriterFactory fileWriterFactory;

    @ParameterizedTest
    @MethodSource("fileWriterProvider")
    public void shouldReturnFileWriterInstanceForGivenFileType(FileType type, Class<? extends FileWriter> expectedClass) {
        FileWriter fileWriter = this.fileWriterFactory.getFileWriter(type.getFactoryBeanName());
        assertThat(fileWriter)
                .isNotNull()
                .isInstanceOfAny(expectedClass);
    }

    private static Stream<Arguments> fileWriterProvider() {
        return Stream.of(
                Arguments.of(CSV, CsvFileWriter.class),
                Arguments.of(TEXT, TextFileWriter.class),
                Arguments.of(XML, XmlFileWriter.class)
        );
    }
}
