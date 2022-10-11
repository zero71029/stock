package com.jetec.zero;

import com.jetec.zero.model.BiasBean;
import com.jetec.zero.model.SarBean;
import com.jetec.zero.model.StockBean;
import com.jetec.zero.repository.StockNameRepository;
import com.jetec.zero.repository.StockRepository;
import com.jetec.zero.service.AlgorithmService;
import com.jetec.zero.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ZeroApplicationTests {
    @Autowired
    StockRepository sr;
    @Autowired
    AlgorithmService as;
    @Autowired
    StockService ss;
    @Autowired
    StockNameRepository snr;

    @Test
    void contextLoads() {

    }

    @Test
    void obv() {
        List<StockBean> stock = ss.findByName("2330");
        List<SarBean> sar = as.sar(stock);
        List<Integer> obv = as.OBV(stock);
        boolean isBuy = false;
        Double cas = 0.0;
        double yes = 0;
        double err = 0;
        double total = 0.0;
        for (int i = 0; i < sar.size(); i++) {
            if (!isBuy) {
                if (sar.get(i).isUP()) {
                    boolean isup = true;
                    for (int j = 0; j < 5; j++) {
                        if (i - j < 0) {
                            continue;
                        }
                        if (obv.get(i) < obv.get(i - j)) {
                            isup = false;
                        }
                    }
                    if (isup) {
                        isBuy = true;
                        cas = sar.get(i).getEndprice();
                    }
                }
            }
            if (isBuy) {
                if ((!sar.get(i).isUP()) && sar.get(i - 1).isUP()) {
                    if ((sar.get(i + 1).getEndprice() - cas) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    System.out.println("買入  :" + cas + " 賣出:" + sar.get(i).getEndprice() + " 結果:" + Math.round(sar.get(i).getEndprice() - cas));
//                    mes.add("買入  :" + buyday + " 賣出:" + sarList.get(i).getStockday() + " 結果:" + Math.round(sarList.get(i).getEndprice() - cas));
                    isBuy = false;
                    total = total + sar.get(i).getEndprice() - cas;
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功率 " + Math.round(yes / (yes + err) * 100));
        System.out.println("total = " + total);
        System.out.println("=======================");
        as.sarResult(sar);
    }


    @Test
    void sar() {
        Map<String, Long> ratelist = new HashMap<>();

        String s = "2330";

        System.out.println(s);

        List<SarBean> sarList = as.sar(ss.findByName(s));


        ratelist.put(s, (Long) as.sarResult(sarList).get("total"));

        ratelist.forEach((a, o) -> {

                System.out.println(a + " :" + o);
        });
        System.out.println(sarList.get(sarList.size()-2));
        System.out.println(sarList.get(sarList.size()-1));
        System.out.println("=======================");
    }

    @Test
    void avg() {
        List<String> list = sr.getStorkName();
        for (String s : list) {
            System.out.println(s);

            as.avgResult(ss.findByName(s), 20, 40, 60);
        }
    }

    @Test
    void oneCrossThree() {
        List<String> list = sr.getStorkName();
        for (String s : list) {
            System.out.println(s);
            as.oneCrossThree(ss.findByName(s), 20, 40, 60);
        }
    }

    /**
     * sar 加 20平均
     *
     * @param sarList 特别行政区列表
     */
    public void sarAdd20agv(List<SarBean> sarList) {
        double total = 0;
        //進價
        double cas = 0;
        double Lprice = 0;
        double yes = 0;
        double err = 0;
        boolean buy = false;
        for (int i = 30; i < sarList.size() - 1; i++) {
            //
            if (sarList.get(i).isUP() && (!sarList.get(i - 1).isUP())) {
                cas = sarList.get(i + 1).getEndprice();
                buy = true;
            }
            if (buy) {
                Lprice = sarList.get(i - 1).getEndprice();
                for (int x = 1; x < 20; x++) {
                    if (Lprice > sarList.get(i - x).getEndprice()) {
                        Lprice = sarList.get(i - x).getEndprice();
                    }
                }
                if (Lprice > sarList.get(i).getEndprice()) {
                    if ((sarList.get(i).getEndprice() - cas) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    buy = false;
                    total = total + sarList.get(i).getEndprice() - cas;
                }
            }
        }
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功比 " + (yes / (yes + err)));
        System.out.println("total = " + total);
        //sar = sar + 0.2 * (nav - sar)
    }


    @Test
    void MACD() {
        String s = "2330";

//        List<String> list = sr.getStorkName();
//        for (String s : list) {

        System.out.println(s);
        List<StockBean> stock = ss.findByName(s);
        Map<String, List> macd = as.MACD(stock);
        List<SarBean> sar = as.sar(stock);

        List<Double> DIF = macd.get("DIF");
        boolean isBuy = false;
        double total = 0.0;
        double price = 0.0;
        double yes = 0.0;
        double err = 0.0;
        for (int i = 0; i < DIF.size(); i++) {
            if (!isBuy) {
                if (DIF.get(i) > 0 && sar.get(i).isUP()) {
                    price = stock.get(i).getEndprice();
                    isBuy = true;
                }
            }
            if (isBuy) {
                if (!sar.get(i).isUP()) {
                    if ((stock.get(i).getEndprice() - price) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    isBuy = false;
                    total = total + stock.get(i).getEndprice() - price;
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("active = " + (yes + err));
        System.out.println("成功率 " + as.double2(yes / (yes + err) * 100) + "%");
        System.out.println(as.double2(total));
        System.out.println("=======================");
        yes = 0.0;
        err = 0.0;
        price = 0.0;
        total = 0;
        isBuy = false;
        for (int i = 0; i < DIF.size(); i++) {
            if (!isBuy) {
                if (DIF.get(i) > 1 && sar.get(i).isUP()) {
                    price = stock.get(i).getEndprice();
                    isBuy = true;
                }
            }
            if (isBuy) {
                if (!sar.get(i).isUP()) {
                    if ((stock.get(i).getEndprice() - price) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    isBuy = false;
                    total = total + stock.get(i).getEndprice() - price;
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("active = " + (yes + err));
        System.out.println("成功率 " + as.double2(yes / (yes + err) * 100) + "%");
        System.out.println(as.double2(total));
        System.out.println("=======================");
//            for (int i = 0; i < DIF.size(); i++) {
//                if (!isBuy) {
//                    if (DIF.get(i) < 0 && !sar.get(i).isUP()) {
//                        price = stock.get(i).getEndprice();
//                        isBuy = true;
//                    }
//                }
//                if (isBuy) {
//                    if (sar.get(i).isUP()) {
//                        if ((stock.get(i).getEndprice() - price) > 0) {
//                            yes++;
//                        } else {
//                            err++;
//                        }
//                        isBuy = false;
//                        total = total + price - stock.get(i).getEndprice()  ;
//                    }
//                }
//            }
//            System.out.println("=======================");
//            System.out.println("yes = " + yes);
//            System.out.println("err = " + err);
//            System.out.println("成功率 " + as.double2(yes / (yes + err) * 100) + "%");
//            System.out.println(as.double2(total));
        System.out.println("=======================");
    }
//    }


    @Test
    void kd() {
        String s = "2330";
        List<StockBean> stock = ss.findByName(s);
        Map kd = as.KD(stock);
        List<SarBean> sar = as.sar(stock);
        List<Double> k = (List<Double>) kd.get("k");
        List<Double> d = (List<Double>) kd.get("d");

        boolean isBuy = false;
        double total = 0.0;
        double price = 0.0;
        double yes = 0.0;
        double err = 0.0;
        for (int i = 0; i < sar.size(); i++) {
            if (!isBuy) {

                if ((d.get(i) > 60 || d.get(i) < 40) && sar.get(i).isUP()) {
                    price = stock.get(i).getEndprice();
                    isBuy = true;
                }
            }
            if (isBuy) {
                if (!sar.get(i).isUP()) {
                    if ((stock.get(i).getEndprice() - price) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    isBuy = false;
                    total = total + stock.get(i).getEndprice() - price;
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("active = " + (yes + err));
        System.out.println("成功率 " + as.double2(yes / (yes + err) * 100) + "%");
        System.out.println(as.double2(total));
        System.out.println("=======================");

        yes = 0.0;
        err = 0.0;
        price = 0.0;
        total = 0;
        isBuy = false;
        for (int i = 0; i < sar.size(); i++) {
            if (!isBuy) {
                if ((d.get(i) > 60 || d.get(i) < 40) && !sar.get(i).isUP()) {
                    price = stock.get(i).getEndprice();
                    isBuy = true;
                }
            }
            if (isBuy) {
                if (sar.get(i).isUP()) {
                    if ((price - stock.get(i).getEndprice()) > 0) {
                        yes++;
                    } else {
                        err++;
                    }
                    isBuy = false;
                    total = total + price - stock.get(i).getEndprice();
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功率 " + as.double2(yes / (yes + err) * 100) + "%");
        System.out.println(as.double2(total));
        as.sarResult(sar);
    }
    @Test
    void bias() {
        String s = "2330";
        List<StockBean> stock = ss.findByName(s);
        List<BiasBean> bias = as.bias(stock);
        bias.forEach(System.out::println);

    }

}
