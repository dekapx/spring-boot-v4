package com.dekapx.apps.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class TextFileWriter implements FileWriter {
    @Override
    public void write(final String contents) {
        log.info("Writing TEXT: [{}]", contents);
    }
}
