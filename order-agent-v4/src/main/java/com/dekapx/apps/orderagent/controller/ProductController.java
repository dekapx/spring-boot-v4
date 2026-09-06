package com.dekapx.apps.orderagent.controller;

import com.dekapx.apps.orderagent.dto.ProductDtos.ProductSearchResult;
import com.dekapx.apps.orderagent.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductSearchService productSearchService;

    /**
     * Semantic product search, e.g. GET /api/products/search?q=comfortable+chair+for+long+workdays
     */
    @GetMapping("/api/products/search")
    public ResponseEntity<List<ProductSearchResult>> search(
            @RequestParam("q") String query,
            @RequestParam(value = "topK", defaultValue = "5") int topK) {
        return ResponseEntity.ok(productSearchService.search(query, topK));
    }
}
