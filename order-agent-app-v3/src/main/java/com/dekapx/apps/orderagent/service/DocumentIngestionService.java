package com.dekapx.apps.orderagent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads the markdown knowledge base (delivery / carrier / cancellation policies) from
 * {@code src/main/resources/docs} into the pgvector store on application startup, so the
 * agent can perform Retrieval Augmented Generation over company policy when answering
 * customer questions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    private static final String DOCS_LOCATION = "classpath:/docs/*.md";

    @EventListener(ApplicationReadyEvent.class)
    public void ingestKnowledgeBaseOnStartup() {
        try {
            // Skip re-ingestion if the store already has content matching our docs.
            List<Document> existing = vectorStore.similaritySearch(
                    SearchRequest.builder().query("delivery policy").topK(1).build());
            if (existing != null && !existing.isEmpty()) {
                log.info("Vector store already contains documents, skipping ingestion.");
                return;
            }

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(DOCS_LOCATION);

            TokenTextSplitter splitter = new TokenTextSplitter();

            for (Resource resource : resources) {
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                List<Document> docs = reader.get();
                List<Document> chunks = splitter.apply(docs);
                vectorStore.add(chunks);
                log.info("Ingested {} chunks from {}", chunks.size(), resource.getFilename());
            }
        } catch (Exception e) {
            log.error("Failed to ingest RAG knowledge base documents", e);
        }
    }
}
