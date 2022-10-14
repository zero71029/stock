package com.jetec.zero.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.jetec.zero.Tool.ResultBean;
import com.jetec.zero.Tool.TrustAll;
import com.jetec.zero.Tool.ZeroFactory;
import com.jetec.zero.Tool.ZeroTools;
import com.jetec.zero.model.StockBean;
import com.jetec.zero.model.StockNameBean;
import com.jetec.zero.service.AlgorithmService;
import com.jetec.zero.service.StockService;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author jetec
 */
@Controller
public class ZeroController {
    @Autowired
    StockService ss;
    @Autowired
    AlgorithmService as;
    @Autowired
    Cache<String, Object> caffeineCache;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(TrustAll.socketFactory(), new TrustAll.trustManager())
            .hostnameVerifier(new TrustAll.hostnameVerifier())
            .build();

    Logger logger = LoggerFactory.getLogger("ZeroController");

    /**
     * 首頁初始化
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @RequestMapping("/zero")
    @ResponseBody
    public Map<String, Object> zero() {
        logger.info("首頁初始化");
        List<StockBean> list = ss.findByName("5871");
        List<Double> age5day = ss.getAge(list, 5);
        List<Double> age20day = ss.getAge(list, 20);
        List<Double> age80day = ss.getAge(list, 80);
        List<String> xAxis = new ArrayList<>();
        List<Integer> buyday = new ArrayList<>();
        List<Double> price = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getEndprice() > age5day.get(i) && age5day.get(i) > age20day.get(i) && age20day.get(i) > age80day.get(i)) {
//                        buyday.add(LocalDate.parse(list.get(i).getStockday(),DateTimeFormatter.ofPattern("yyy/MM/dd")) );
                buyday.add(300);
            } else {
                buyday.add(0);
            }
            xAxis.add(list.get(i).getStockday());
            price.add(list.get(i).getEndprice());
        }

        return ss.result(price, buyday, age5day, age20day, age80day, xAxis);
    }

    /**
     * 搜索股票
     *
     * @param num   號碼
     * @param start 开始
     * @param end   结束
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @RequestMapping("/selectStock/{num}")
    @ResponseBody
    public Map<String, Object> selectStock(@PathVariable("num") String num, @RequestParam("start") String start, @RequestParam("end") String end) {
        logger.info("搜索資料 {} ;起{};結束{}", num, start, end);
        System.out.println(num);
        System.out.println(start);
        System.out.println(end);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String s = LocalDate.parse(start, format).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String e = LocalDate.parse(end, format).minusDays(80).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        System.out.println(s);
        System.out.println(e);


        List<StockBean> list = ss.findByNameAndStockdayBetween(num, s, e);

        System.out.println("輸出筆數" + list.size());
        List<Double> age5day = ss.getAge(list, 5);
        List<Double> age20day = ss.getAge(list, 20);
        List<Double> age80day = ss.getAge(list, 80);

        List<String> xAxis = new ArrayList<>();
        List<Integer> buyday = new ArrayList<>();
        List<Double> price = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getEndprice() > age5day.get(i) && age5day.get(i) > age20day.get(i) && age20day.get(i) > age80day.get(i)) {
//                        buyday.add(LocalDate.parse(list.get(i).getStockday(),DateTimeFormatter.ofPattern("yyy/MM/dd")) );
                buyday.add(500);
            } else {
                buyday.add(0);
            }
            xAxis.add(list.get(i).getStockday());
            price.add(list.get(i).getEndprice());
        }


        return ss.result(price, buyday, age5day, age20day, age80day, xAxis);
    }

    @RequestMapping("/testStock")
    @ResponseBody
    public List<StockBean> testStock() {
        String name = "5871";
        return ss.findByName(name);
    }

    @RequestMapping("/stock-DJI.json")
    @ResponseBody
    public JSONArray stockjson(@RequestParam("name") String name) {
        logger.info("讀取 {}", name);
        List<StockBean> list = ss.findByName(name);
        JSONArray result = new JSONArray();
//        Map<String, List> macd   =     as.MACD(list );
//        List<Double> DIF =macd.get("DIF");



        for (int i = 0; i < list.size(); i++) {
            JSONArray arr = new JSONArray();
            arr.add(list.get(i).getStockday());
            arr.add(list.get(i).getOpenprice());
            arr.add(list.get(i).getEndprice());
            arr.add(list.get(i).getLowestprice());
            arr.add(list.get(i).getHightprice());
            arr.add(list.get(i).getTransactionsnumber());
            result.add(arr);
        }


        return result;
    }

    /**
     * 检查是反抓取中
     *
     * @return {@link Boolean}
     */
    @RequestMapping("/checkCatch")
    @ResponseBody
    public Boolean checkCatch() {
        return (Boolean) caffeineCache.getIfPresent("isCatch");
    }

    /**
     * 捕捉股票
     *
     * @param nam 名字
     * @return {@link Boolean}
     */
    @RequestMapping("/CatchStock")
    @ResponseBody
    public Boolean catchStock(@RequestParam("name") String nam) {
        caffeineCache.asMap().put("isCatch", true);
        new Thread(() -> {
            LocalDate now = LocalDate.now();
            List<StockNameBean> list = ss.findStockNameByAll();
            for (StockNameBean bean : list) {
                String name = bean.getNum();
                for (int y = 2011; y < (now.getYear() + 1); y++) {
                    for (int i = 1; i < 13; i++) {
                        Response response;
                        try {
                            logger.info("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" + y + String.format("%02d", i) + "01&stockNo=" + name);
                            Request request = new Request.Builder()
                                    .url("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" + y + String.format("%02d", i) + "01&stockNo=" + name)
                                    .build();
                            response = client.newCall(request).execute();
                            JSONObject object = JSONObject.parseObject(response.body().string());
                            logger.info(String.valueOf(object));
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
                                if (!ss.existsByStockdayAndName(stockBean.getStockday(), stockBean.getName())) {
                                    stockBean.setStockid(ZeroTools.getUUID());
                                    ss.save(stockBean);
                                }
                            }
                        } catch (Exception e) {
                            logger.info("抓取不到資料");
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(11000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            ss.deleteStrckName(bean);
            }
            caffeineCache.asMap().put("isCatch", false);
        }).start();
        return true;
    }

    @RequestMapping("/calculationResults")
    @ResponseBody
    public ResultBean calculationResults(@RequestParam("stockNum") String stockNum, @RequestParam("norm") String norm) {
        System.out.println(stockNum);
        System.out.println(norm);
        Map<String,Object> result = new HashMap<>(5);
        if(Objects.equals(norm,"avg")){
            result =   as.avgResult(ss.findByName(stockNum),20,40,60);
            return ZeroFactory.success("三平均線計算",result);
        }
        if(Objects.equals(norm,"sar")){
            result =  as.sarResult(as.sar(ss.findByName(stockNum)));
            return ZeroFactory.success("SAR計算",result);
        }
        return ZeroFactory.fail("指標錯誤");



    }


}
