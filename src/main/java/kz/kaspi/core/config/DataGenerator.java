package kz.kaspi.core.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kz.kaspi.core.product.Product;
import kz.kaspi.core.search.ProductDocument;
import kz.kaspi.core.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kz.kaspi.core.product.ProductRepository;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataGenerator implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Database already populated!");
            return;
        }

        log.info("Populating database with mock data...");

        List<Product> products = new ArrayList<>();
        List<ProductDocument> documents = new ArrayList<>();

        for (int i = 1; i <= 10000; i++) {
            Product product = Product.builder()
                    .sku("KASPI-ITEM-" + i)
                    .name("Смартфон Модель " + i)
                    .price(BigDecimal.valueOf(150000 + (Math.random() * 50000)))
                    .stock(100)
                    .build();
            products.add(product);

            // for elastic
            ProductDocument document = ProductDocument.builder()
                    .id(String.valueOf(i))
                    .sku("KASPI-ITEM-" + i)
                    .name("Смартфон Модель " + i)
                    .price(product.getPrice())
                    .stock(100)
                    .build();
            documents.add(document);

        }

        // save with batch insert
        productRepository.saveAll(products);
        productSearchRepository.saveAll(documents);

        log.info("Database populated with " + products.size() + " products in Postgres and Elasticsearch");
    }

}
