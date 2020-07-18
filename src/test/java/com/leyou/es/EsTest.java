package com.leyou.es;

import com.leyou.es.pojo.Item;
import com.leyou.es.repository.ItemRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 9:05
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
    //也是 org.springframework.data的
    @Autowired
    ElasticsearchTemplate template;

    //增删改用下面这个多，
    @Autowired
    ItemRepository itemRepository;

    @Test
    public void testCreate() {
        //创建索引库
        template.createIndex(Item.class);
        //映射关系
        template.putMapping(Item.class);
    }

    @Test
    public void testCRUD() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(1L, "小米7", "手机", "小米", 2300.00, "imageurl"));
        items.add(new Item(2L, "小米8", "手机", "小米", 2400.00, "imageurl"));
        items.add(new Item(3L, "小米9", "手机", "小米", 2500.00, "imageurl"));

        itemRepository.saveAll(items);
    }

    @Test
    public void testFind() {
        Iterable<Item> all = itemRepository.findAll();
        for (Item item : all) {
            System.out.println(item);
        }
    }

    //自定义的查找
    @Test
    public void testFindBy() {
        List<Item> list = itemRepository.findByPriceBetween(2000D, 4000D);
        for (Item item : list) {
            System.out.println(item);
        }
    }


    //用一部分原生的方式，过滤、分页、排序等
    @Test
    public void testQuery() {
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "title", "price"}, null));
        //添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米"));
        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //分页
        queryBuilder.withPageable(PageRequest.of(0, 2));   //这个从0开始

        Page<Item> result = itemRepository.search(queryBuilder.build());

        long totalElements = result.getTotalElements();
        System.out.println(totalElements);
        int totalPages = result.getTotalPages();
        System.out.println(totalPages);
        List<Item> list = result.getContent();
        for (Item item : list) {
            System.out.println(item);
        }
    }


    //聚合
    @Test
    public void testAggr() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        String aggName = "popularBrand";    //随便起
        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(aggName).field("brand"));
        //查询并返回聚合结果
        AggregatedPage<Item> result = template.queryForPage(queryBuilder.build(), Item.class);
        //解析聚合
        Aggregations aggregations = result.getAggregations();
        //获取指定名称的聚合
//        Aggregation aggregation = aggregations.get(aggName);  取Aggregation的实现类，才有获得桶的方法
        StringTerms terms = aggregations.get(aggName);

        //获取桶
        List<StringTerms.Bucket> buckets = terms.getBuckets();

        for (StringTerms.Bucket bucket : buckets) {
            System.out.println("Key= " + bucket.getKeyAsString());
            System.out.println("DocCount() = " + bucket.getDocCount())
            ;
        }
    }

}

