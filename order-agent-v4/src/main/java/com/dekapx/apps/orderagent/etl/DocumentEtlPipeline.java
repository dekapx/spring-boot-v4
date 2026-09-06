package com.dekapx.apps.orderagent.etl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.document.DocumentWriter;

import java.util.List;

/**
 * A minimal, reusable Extract-Transform-Load pipeline for feeding content into the
 * vector store, following Spring AI's {@link DocumentReader} / {@link DocumentTransformer} /
 * {@link DocumentWriter} abstractions:
 *
 * <pre>
 *   Extract: DocumentReader.get()               (markdown files, JPA rows mapped to Documents, ...)
 *   Transform: DocumentTransformer.apply(...)    (chunking/splitting, metadata enrichment/tagging)
 *   Load: DocumentWriter.accept(...)             (VectorStore.add(...))
 * </pre>
 *
 * Every ingestion pipeline in this application (policy docs, product catalog, and any
 * future content source) is expressed as one instance of this class, which keeps the
 * ETL stages explicit, testable, and independent of *what* is being ingested.
 */
@Slf4j
public class DocumentEtlPipeline {

    private final String name;
    private final DocumentReader reader;
    private final List<DocumentTransformer> transformers;
    private final DocumentWriter writer;

    public DocumentEtlPipeline(String name, DocumentReader reader, List<DocumentTransformer> transformers, DocumentWriter writer) {
        this.name = name;
        this.reader = reader;
        this.transformers = transformers;
        this.writer = writer;
    }

    /**
     * Runs Extract -> Transform (each transformer applied in sequence) -> Load.
     *
     * @return number of document chunks written
     */
    public int run() {
        log.info("[ETL:{}] extracting documents...", name);
        List<Document> documents = reader.get();
        log.info("[ETL:{}] extracted {} raw document(s)", name, documents.size());

        for (DocumentTransformer transformer : transformers) {
            documents = transformer.apply(documents);
        }
        log.info("[ETL:{}] transformed into {} chunk(s)", name, documents.size());

        writer.accept(documents);
        log.info("[ETL:{}] loaded {} chunk(s) into vector store", name, documents.size());
        return documents.size();
    }
}
