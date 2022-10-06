package com.jetec.zero;


import com.alibaba.fastjson.JSONObject;
import com.jetec.zero.Tool.TrustAll;
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
import java.util.ArrayList;
import java.util.Arrays;


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
}
