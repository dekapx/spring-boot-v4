package com.dekapx.apps.orderagent.etl;

import com.dekapx.apps.orderagent.entity.Product;
import com.dekapx.apps.orderagent.repository.ProductRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;

import java.util.List;
import java.util.Map;

/**
 * ETL "extract" stage for the product catalog: reads {@link Product} rows from Postgres
 * via JPA and maps each one to a {@link Document} whose text is a natural-language
 * description suitable for embedding, and whose metadata carries the structured fields
 * needed to hydrate full product records after a semantic search hit.
 */
public class ProductDocumentReader implements DocumentReader {

    private final ProductRepository productRepository;

    public ProductDocumentReader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Document> get() {
        return productRepository.findAll().stream()
                .map(this::toDocument)
                .toList();
    }

    private Document toDocument(Product product) {
        String text = """
                %s (%s brand, category: %s)
                %s
                Price: $%s
                """.formatted(
                product.getName(),
                product.getBrand() == null ? "unbranded" : product.getBrand(),
                product.getCategory(),
                product.getDescription(),
                product.getPrice());

        Map<String, Object> metadata = Map.of(
                "productId", product.getId(),
                "sku", product.getSku(),
                "category", product.getCategory(),
                "price", product.getPrice().toPlainString()
        );

        return Document.builder()
                .id("product-" + product.getId())
                .text(text)
                .metadata(metadata)
                .build();
    }
}
