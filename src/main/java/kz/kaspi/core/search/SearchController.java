package kz.kaspi.core.search;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchRepository searchRepository;

    @GetMapping
    @Cacheable(value = "products_search", key = "#query")
    public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(searchRepository.findByNameContainingIgnoreCase(query));
    }
}