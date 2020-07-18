package com.leyou.es.repository;

import com.leyou.es.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 9:39
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {
    //按照格式定义方法名即可自定义查找!!!
    List<Item> findByPriceBetween(Double beginPrice, Double endPrice);
}
