package com.jetec.zero.repository;


import com.jetec.zero.model.StockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author jetec
 */
public interface StockRepository extends JpaRepository<StockBean,String> {
    boolean existsByStockdayAndName(String stockday, String name);

    List<StockBean> findByName(String name, Sort stockday);
    List<StockBean> findByNameAndStockdayBetween(String s, String start, String end, Sort stockday);


    @Query(value = "select name  from stock s where name BETWEEN '1000' and '9999'    group by name  having count(name) > 500;", nativeQuery=true)
    List<String> getStorkName();
}
