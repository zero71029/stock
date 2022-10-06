package com.jetec.zero.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jetec.zero.Tool.TrustAll;
import com.jetec.zero.Tool.ZeroTools;
import com.jetec.zero.model.StockBean;
import com.jetec.zero.repository.StockRepository;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class time {

    Logger logger = LoggerFactory.getLogger("time");
    @Autowired
    StockRepository sr;

    @Scheduled(cron = "0 0 20 * * *")
    public void layoutSQL() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .sslSocketFactory(TrustAll.socketFactory(), new TrustAll.trustManager())
                .hostnameVerifier(new TrustAll.hostnameVerifier())
                .build();
        Request request = new Request.Builder()
                .url("https://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=json")
                .build();
        JSONObject object = null;
        try (Response response = client.newCall(request).execute()) {
            object = JSONObject.parseObject(response.body().string());
        } catch (IOException e) {
            logger.info("連接錯誤");
            logger.info(e.toString());

        }
        JSONArray arr = object.getJSONArray("data");
        for (int i = 0; i < arr.size(); i++) {
            JSONArray e = arr.getJSONArray(i);
            System.out.println(e);
            StockBean stockBean = new StockBean();
            stockBean.setStockday(object.getString("date"));
            stockBean.setTransactiondate(LocalDate.parse(object.getString("date"), DateTimeFormatter.ofPattern("yyyyMMdd")));
            stockBean.setOpenprice(Double.valueOf(e.getString(4).replaceAll(",", "")));
            stockBean.setHightprice(Double.valueOf(e.getString(5).replaceAll(",", "")));
            stockBean.setLowestprice(Double.valueOf(e.getString(6).replaceAll(",", "")));
            stockBean.setEndprice(Double.valueOf(e.getString(7).replaceAll(",", "")));
            stockBean.setName(e.getString(0).replaceAll("\"", ""));
            stockBean.setTransactionsnumber(Integer.valueOf(e.getString(2).replaceAll(",", "")));
            if (!sr.existsByStockdayAndName(stockBean.getStockday(), stockBean.getName())) {
                stockBean.setStockid(ZeroTools.getUUID());
                sr.save(stockBean);
            }
        }
    }
}
