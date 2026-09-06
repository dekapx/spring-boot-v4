package com.dekapx.apps.orderagent.etl;

import com.dekapx.apps.orderagent.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Runs the two ETL ingestion pipelines on application startup:
 *  1. Policy knowledge base (markdown files -> chunks tagged docType=policy)
 *  2. Product catalog (Postgres rows -> one document per product, tagged docType=product)
 *
 * Both pipelines load into the *same* pgvector table, distinguished by the {@code docType}
 * metadata field, which downstream retrieval (RAG advisor, semantic product search) filters on.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IngestionOrchestrator {

    private static final String POLICY_DOCS_LOCATION = "classpath:/docs/*.md";

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void ingestOnStartup() {
        ingestPolicyDocsIfNeeded();
        ingestProductCatalogIfNeeded();
    }

    private void ingestPolicyDocsIfNeeded() {
        if (alreadyIngested("policy")) {
            log.info("[ETL] policy docs already present in vector store, skipping ingestion.");
            return;
        }
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(POLICY_DOCS_LOCATION);

            int total = 0;
            for (Resource resource : resources) {
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .build();

                DocumentEtlPipeline pipeline = new DocumentEtlPipeline(
                        "policy-docs:" + resource.getFilename(),
                        new MarkdownDocumentReader(resource, config),
                        List.of(
                                new TokenTextSplitter(),
                                new MetadataTaggingTransformer("policy", Map.of("source", resource.getFilename()))
                        ),
                        vectorStore::add
                );
                total += pipeline.run();
            }
            log.info("[ETL] ingested {} total policy chunks", total);
        } catch (Exception e) {
            log.error("[ETL] failed to ingest policy knowledge base", e);
        }
    }

    private void ingestProductCatalogIfNeeded() {
        if (alreadyIngested("product")) {
            log.info("[ETL] product catalog already present in vector store, skipping ingestion.");
            return;
        }
        try {
            DocumentEtlPipeline pipeline = new DocumentEtlPipeline(
                    "product-catalog",
                    new ProductDocumentReader(productRepository),
                    List.of(new MetadataTaggingTransformer("product", Map.of())),
                    vectorStore::add
            );
            pipeline.run();
        } catch (Exception e) {
            log.error("[ETL] failed to ingest product catalog", e);
        }
    }

    private boolean alreadyIngested(String docType) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        List<Document> existing = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(docType)
                        .topK(1)
                        .filterExpression(b.eq("docType", docType).build())
                        .build());
        return existing != null && !existing.isEmpty();
    }
}
