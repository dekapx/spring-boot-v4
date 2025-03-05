package com.dekapx.apps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FileModel {
    private FileType fileType;
    private String contents;
}
