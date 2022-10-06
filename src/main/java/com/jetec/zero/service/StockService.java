package com.jetec.zero.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jetec.zero.Tool.TrustAll;
import com.jetec.zero.Tool.ZeroTools;
import com.jetec.zero.model.StockBean;
import com.jetec.zero.model.StockNameBean;
import com.jetec.zero.repository.StockNameRepository;
import com.jetec.zero.repository.StockRepository;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author jetec
 */
@Service
@Transactional
public class StockService {
    @Autowired
    StockRepository sr;
    @Autowired
    StockNameRepository snr;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(TrustAll.socketFactory(), new TrustAll.trustManager())
            .hostnameVerifier(new TrustAll.hostnameVerifier())
            .build();

    Logger logger = LoggerFactory.getLogger("StockService");

    /**
     * 讀取股票
     *
     * @param name 名字
     * @return {@link List}<{@link StockBean}>
     */
    public List<StockBean> findByName(String name) {
        return sr.findByName(name, Sort.by(Sort.Direction.ASC, "stockday"));
    }

    /**
     * 機算平均線
     *
     * @param list 列表
     * @param x    幾天
     * @return {@link List}<{@link Double}>
     */
    public List<Double> getAge(List<StockBean> list, int x) {

        List<Double> ag = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            double sum = 0;
            for (int j = i; j > i - x; j--) {
                if (j <= 0) {
                    sum = sum + 0;
                } else {
                    sum = sum + list.get(j).getEndprice();
                }
            }

            ag.add(sum / x);
        }

        return ag;
    }


    public List<StockBean> findByNameAndStockdayBetween(String name, String start, String end) {
        return sr.findByNameAndStockdayBetween(name, start, end, Sort.by(Sort.Direction.ASC, "stockday"));
    }


    public Map<String, Object> result(List<Double> p, List<Integer> b, List<Double> age5, List<Double> age20, List<Double> age80, List<String> x) {
        Map<String, Object> map = new HashMap<>();
        List<Double> age5day = new ArrayList<>();
        List<Double> age20day = new ArrayList<>();
        List<Double> age80day = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> buyday = new ArrayList<>();
        List<Double> price = new ArrayList<>();


        double zero = 0;
        for (int i = 80; i < p.size(); i++) {
            age5day.add(age5.get(i));
            age20day.add(age20.get(i));
            age80day.add(age80.get(i));
            xAxis.add(x.get(i));
            buyday.add(b.get(i));
            price.add(p.get(i));
            if (b.get(i).equals(500)) {
                zero = zero + (p.get(i) - p.get(i - 1));
            }
            if (b.get(i - 1).equals(500) && b.get(i).equals(0)) {
                zero = zero + (p.get(i) - p.get(i - 1));
            }

        }
        System.out.println(zero);
        map.put("price", price);
        map.put("day5", age5day);
        map.put("day20", age20day);
        map.put("day80", age80day);
        map.put("buyday", buyday);
        map.put("xAxis", xAxis);
        return map;
    }

    /**
     * 捕捉股票
     *
     * @param name 名字
     */
    public void CatchStock(String name) {
        LocalDate now = LocalDate.now();
        for (int y = 2011; y < (now.getYear() + 1); y++) {
            for (int i = 1; i < 13; i++) {
                Response response = null;
                try {
                    logger.info("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" + y + String.format("%02d", i) + "01&stockNo=" + name);
                    Request request = new Request.Builder()
                            .url("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" + y + String.format("%02d", i) + "01&stockNo=" + name)
                            .build();
                    response = client.newCall(request).execute();
                    JSONObject object = JSONObject.parseObject(response.body().string());
                    JSONArray array = object.getJSONArray("data");
                    StockBean stockBean = new StockBean();
                    for (int j = 0; j < array.size(); j++) {
                        JSONArray e = array.getJSONArray(j);
                        System.out.println(e);
                        LocalDate d = LocalDate.parse(e.getString(0).replaceAll("\"", "").replaceAll("/", ""), DateTimeFormatter.ofPattern("yyyMMdd")).plusYears(1911);
                        stockBean.setStockday(d.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        stockBean.setTransactiondate(d);
                        stockBean.setOpenprice(e.getDouble(3));
                        stockBean.setHightprice(e.getDouble(4));
                        stockBean.setLowestprice(e.getDouble(5));
                        stockBean.setEndprice(e.getDouble(6));
                        stockBean.setName(name);
                        stockBean.setTransactionsnumber(Integer.valueOf(e.getString(1).replaceAll(",", "")));
                        if (!sr.existsByStockdayAndName(stockBean.getStockday(), stockBean.getName())) {
                            stockBean.setStockid(ZeroTools.getUUID());
                            sr.save(stockBean);
                        }
                    }
                } catch (Exception e) {
                    logger.info("抓取不到資料");
                    try {
                        logger.info(response.body().string());
                    } catch (IOException ex) {
                        logger.info("沒有返回");
                    }

                    e.printStackTrace();
                }

                try {
                    Thread.sleep(11000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public boolean existsByStockdayAndName(String stockday, String name) {
        return sr.existsByStockdayAndName(stockday, name);
    }

    public void save(StockBean stockBean) {
        sr.save(stockBean);
    }

    public List<StockBean> findAll() {
        return sr.findAll(Sort.by(Sort.Direction.DESC));
    }

    public List<StockNameBean> findStockNameByAll() {
        return snr.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }


    public void deleteStrckName(StockNameBean bean) {
        snr.delete(bean);
    }
}
