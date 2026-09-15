package kz.kaspi.core.search;

import org.springframework.data.elasticsearch.annotations.Document;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.math.BigDecimal;

// @Document говорит Эластику создать индекс "products" для быстрого поиска
@Document(indexName = "products")
@Getter 
@Setter
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id 
    private String id;

    @Field(type = FieldType.Keyword)
    private String sku; //точно совподание без ошибок

    //Text позволяет искать с опечатками (Fuzzy Search) и по частям слова
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    //сток нам нужен для фильтра
    @Field(type = FieldType.Integer)
    private Integer stock;
}
