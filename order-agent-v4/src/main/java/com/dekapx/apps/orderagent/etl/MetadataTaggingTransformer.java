package com.dekapx.apps.orderagent.etl;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ETL "transform" stage: stamps every document with a {@code docType} (and any extra
 * static metadata) so that downstream retrieval can filter the shared vector store by
 * content type (e.g. {@code docType == 'policy'} vs {@code docType == 'product'}).
 */
public class MetadataTaggingTransformer implements DocumentTransformer {

    private final String docType;
    private final Map<String, Object> extraMetadata;

    public MetadataTaggingTransformer(String docType, Map<String, Object> extraMetadata) {
        this.docType = docType;
        this.extraMetadata = extraMetadata == null ? Map.of() : extraMetadata;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        return documents.stream()
                .map(doc -> {
                    Map<String, Object> metadata = new java.util.HashMap<>(doc.getMetadata());
                    metadata.put("docType", docType);
                    metadata.putAll(extraMetadata);
                    return Document.builder()
                            .id(doc.getId())
                            .text(doc.getText())
                            .metadata(metadata)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
