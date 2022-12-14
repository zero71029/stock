package com.jetec.zero;


import com.alibaba.fastjson.JSONObject;
import com.jetec.zero.Tool.TrustAll;
import com.jetec.zero.model.StockBean;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Test {

    @org.junit.jupiter.api.Test
    void contextLoads() throws IOException {

        System.out.println(LocalDate.parse("20220826", DateTimeFormatter.ofPattern("yyyyMMdd")));

        System.out.println(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));


        ArrayList<String> cn = new ArrayList();
        URL u = new URL("https://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=json");
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
        conn.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

    }

    @org.junit.jupiter.api.Test
    void OkHttpClient() throws IOException {

        System.out.println("====================================================");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .sslSocketFactory(TrustAll.socketFactory(), new TrustAll.trustManager())
                .hostnameVerifier(new TrustAll.hostnameVerifier())
                .build();

        Request request = new Request.Builder()
                .url("https://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=json")
                .build();
        JSONObject obj;
        try (Response response = client.newCall(request).execute()) {
            obj = JSONObject.parseObject(response.body().string());
        }
        System.out.println(obj);
    }

    @org.junit.jupiter.api.Test
    void aaaa() {
        int[] nums = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        System.out.println(removeDuplicates(nums));
    }


    public int removeDuplicates(int[] nums) {
        int n = nums.length;
        if (n <= 0) {
            return 0;
        }
        int i = 0;
        int j = 1;
        while (j < n) {
            if (nums[i] != nums[j]) {
                i++;
                nums[i] = nums[j];
            }
            j++;
        }
        return i;
    }
}
