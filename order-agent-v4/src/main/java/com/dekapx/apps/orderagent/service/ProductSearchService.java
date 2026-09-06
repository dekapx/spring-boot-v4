package com.dekapx.apps.orderagent.service;

import com.dekapx.apps.orderagent.dto.ProductDtos.ProductSearchResult;
import com.dekapx.apps.orderagent.entity.Product;
import com.dekapx.apps.orderagent.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Semantic (embedding-based) product search: the user's free-text query is embedded and
 * compared against product-description vectors in the shared pgvector store (filtered to
 * {@code docType == 'product'}), then hydrated back into full {@link Product} rows from
 * Postgres, ranked by similarity.
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    public List<ProductSearchResult> search(String query, int topK) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        List<Document> hits = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(0.4)
                        .filterExpression(b.eq("docType", "product").build())
                        .build());

        if (hits == null || hits.isEmpty()) {
            return List.of();
        }

        // Preserve similarity ranking/order while hydrating full product rows.
        Map<Long, Double> scoreByProductId = new LinkedHashMap<>();
        for (Document doc : hits) {
            Object productIdRaw = doc.getMetadata().get("productId");
            if (productIdRaw == null) continue;
            Long productId = Long.valueOf(productIdRaw.toString());
            double score = doc.getScore() == null ? 0.0 : doc.getScore();
            scoreByProductId.putIfAbsent(productId, score);
        }

        List<Product> products = productRepository.findByIdIn(scoreByProductId.keySet().stream().toList());
        Map<Long, Product> productById = new LinkedHashMap<>();
        products.forEach(p -> productById.put(p.getId(), p));

        return scoreByProductId.entrySet().stream()
                .map(e -> {
                    Product p = productById.get(e.getKey());
                    if (p == null) return null;
                    return new ProductSearchResult(
                            p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getCategory(),
                            p.getBrand(), p.getPrice(), p.getStockQuantity(), e.getValue());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
