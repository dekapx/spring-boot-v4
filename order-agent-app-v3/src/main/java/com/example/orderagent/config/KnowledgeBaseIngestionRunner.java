package com.example.orderagent.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads the markdown files under src/main/resources/knowledge-base/ into the pgvector
 * store on application startup, so the agent can answer policy/FAQ questions via RAG.
 *
 * Idempotent: skips ingestion if the vector_store table already has rows, so restarts
 * don't keep re-embedding the same documents.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseIngestionRunner implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Integer existingCount = jdbcTemplate.queryForObject(
                "select count(*) from vector_store", Integer.class);

        if (existingCount != null && existingCount > 0) {
            log.info("Vector store already contains {} chunks, skipping knowledge base ingestion.", existingCount);
            return;
        }

        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:knowledge-base/*.md");

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> allChunks = new ArrayList<>();

        for (Resource resource : resources) {
            TextReader reader = new TextReader(resource);
            reader.getCustomMetadata().put("source", resource.getFilename());
            List<Document> chunks = splitter.apply(reader.get());
            allChunks.addAll(chunks);
            log.info("Loaded {} chunks from {}", chunks.size(), resource.getFilename());
        }

        if (!allChunks.isEmpty()) {
            vectorStore.add(allChunks);
            log.info("Ingested {} total chunks into the vector store.", allChunks.size());
        }
    }
}
