package com.leyou.es.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author chenxm
 * @date 2020/7/11 - 8:59
 */


@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "el_java", type = "item", shards = 1/*,replicas = 1*/)
public class Item {
    @Field(type = FieldType.Long)
    @Id     //不是数据库主键id
    private Long id;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword,index = false)
    private String images;


}
