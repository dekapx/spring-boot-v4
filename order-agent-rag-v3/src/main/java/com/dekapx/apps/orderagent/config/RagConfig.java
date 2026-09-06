package com.dekapx.apps.orderagent.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RagConfig {
    @Bean
    public VectorStore orderKnowledgeVectorStore(EmbeddingModel embeddingModel,
                                   @Qualifier("orderKnowledgeResource") Resource orderKnowledgeResource) throws IOException {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        String knowledgeBase =  orderKnowledgeResource.getContentAsString(StandardCharsets.UTF_8);
        List<Document> documents = new TokenTextSplitter()
                .apply(List.of(new Document(knowledgeBase)));
        return vectorStore;
    }

    @Bean("orderKnowledgeResource")
    public Resource orderKnowledgeResource(ResourceLoader resourceLoader) {
        return resourceLoader.getResource("classpath:reg/order-agent-knowledge.txt");
    }
}
