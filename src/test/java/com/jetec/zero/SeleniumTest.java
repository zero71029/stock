package com.jetec.zero;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jetec.zero.Tool.TrustAll;
import com.jetec.zero.Tool.ZeroTools;
import com.jetec.zero.model.StockBean;
import com.jetec.zero.repository.StockRepository;
import com.jetec.zero.service.AlgorithmService;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SeleniumTest {
    @Autowired
    StockRepository sr;
    @Autowired
    AlgorithmService as;

    @Test
    void everyday() {
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
            System.out.println(object);
        } catch (IOException e) {
            e.printStackTrace();
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

    @Test
    void contextLoads2() throws InterruptedException {
        String name = "5871";
        //配置
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        for (int i = 1; i < 13; i++) {
            driver.get("https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=2022" + String.format("%02d", i) + "01&stockNo=" + name);
            JSONObject object = JSONObject.parseObject(driver.findElement(By.tagName("pre")).getText());
            System.out.println(object);
            System.out.println();
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
            Thread.sleep(10000);
        }
        driver.quit();
//        System.out.println(driver.findElementByTagName("pre").getText());

    }



}
