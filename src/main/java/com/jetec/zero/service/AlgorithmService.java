package com.jetec.zero.service;

import com.jetec.zero.model.BiasBean;
import com.jetec.zero.model.SarBean;
import com.jetec.zero.model.StockBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jetec
 */
@Service
@Transactional
public class AlgorithmService {


    /**
     * SAR特别行政区
     *
     * @param list 列表
     * @return {@link List}<{@link SarBean}>
     */
    public List<SarBean> sar(List<StockBean> list) {
        //最高
        double nav = list.get(0).getHightprice();
        double sar = list.get(0).getLowestprice();
        double aF = 0.02;
        //幾天最高
        int daycount = 10;
        boolean up = true;
        List<SarBean> sarList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            SarBean bean = new SarBean();
            if (up) {
                if (list.get(i).getHightprice() > nav) {
                    nav = list.get(i).getHightprice();
                    if (aF <= 0.2) {
                        aF = aF + 0.01;
                    }
                }
                sar =double2(sar + aF * (nav - sar))    ;

                //變盤
                if (sar > list.get(i).getEndprice()) {
                    up = false;
                    aF = 0.02;
//                    sar = list.get(i).getHightprice();
                    for (int j = 0; j < daycount; j++) {
                        int in = i - j;
                        if (in < 0) {
                            in = 0;
                        }
                        if (sar < list.get(in).getHightprice()) {
                            sar = list.get(in).getHightprice();
                        }
                    }
                    nav = list.get(i).getLowestprice();
                }



                bean.setSar(sar);
                bean.setName(list.get(i).getName());
                bean.setUP(up);
                bean.setEndprice(list.get(i).getEndprice());
                bean.setStockday(list.get(i).getStockday());
                bean.setTransactiondate(list.get(i).getTransactiondate());

            } else {
                if (list.get(i).getLowestprice() < nav) {
                    nav = list.get(i).getLowestprice();
                    if (aF < 0.2) {
                        aF = aF + 0.01;
                    }
                }
                sar =double2(sar - aF * (sar - nav))    ;
                //收盤 長破sar 變盤
                if (sar < list.get(i).getEndprice()) {
                    up = true;
                    aF = 0.02;
//                    sar = list.get(i).getLowestprice();
                    for (int j = 0; j < daycount; j++) {
                        int in = i - j;
                        if (in < 0) {
                            in = 0;
                        }
                        if (sar > list.get(in).getLowestprice()) {
                            sar = list.get(in).getLowestprice();
                        }
                    }
                    nav = list.get(i).getHightprice();
                }
                bean.setUP(up);
                bean.setSar(sar);
                bean.setName(list.get(i).getName());
                bean.setEndprice(list.get(i).getEndprice());
                bean.setStockday(list.get(i).getStockday());
                bean.setTransactiondate(list.get(i).getTransactiondate());
            }
            sarList.add(bean);

            for (SarBean sarBean : sarList) {
                if ( sarBean.getStockday().compareTo("20220701") > 0    ){
                    System.out.println(sarBean);
                }

            }


        }
        return sarList;
    }

    public Map<String, Object> sarResult(List<SarBean> sarList) {
        //計算結果
        double total = 0;
        //進價
        double cas = 0;
        double yes = 0;
        double err = 0;
        boolean buy = false;
        double los = 0;
        List<String> mes = new ArrayList<>();
        String buyday = null;
        for (int i = 30; i < sarList.size(); i++) {
            //
            if (!buy) {
                if (sarList.get(i).isUP() && (!sarList.get(i - 1).isUP())) {
                    cas = sarList.get(i).getEndprice();
                    buy = true;
                    buyday = sarList.get(i).getStockday();
                    los = sarList.get(i).getEndprice() - 10;
                }
            }
            if (buy) {
                if (((!sarList.get(i).isUP()) && sarList.get(i - 1).isUP())) {
                    if ((sarList.get(i).getEndprice() - cas) > 0) {
                        yes++;

//                        System.out.println(buyday + "買 " + cas + "     " + sarList.get(i).getStockday() + "賣 " + sarList.get(i).getEndprice() + "  (成功)賺 :" + double2(sarList.get(i).getEndprice() - cas));

                    } else {
                        err++;
//                        System.out.println(buyday + "買 " + cas + "     " + sarList.get(i).getStockday() + "賣 " + sarList.get(i).getEndprice() + "  (失敗) :" + double2(sarList.get(i).getEndprice() - cas));

                    }
                    mes.add("買入  :" + buyday + " 賣出:" + sarList.get(i).getStockday() + " 結果:" + Math.round(sarList.get(i).getEndprice() - cas));
                    buy = false;
                    total = total + sarList.get(i).getEndprice() - cas;
                }
            }
        }
        System.out.println("========只用sar===============");
        printResult(yes, err, total);

        Map<String, Object> result = new HashMap<>(10);
        result.put("yes", yes);
        result.put("err", err);
        result.put("rate", Math.round(yes / (yes + err) * 100));
        result.put("total", Math.round(total));
        result.put("buyDay", mes);

        total = 0;
        cas = 0;
        yes = 0;
        err = 0;
        buy = false;
        for (int i = 30; i < sarList.size(); i++) {
            //

            if (!buy) {
                if ((!sarList.get(i).isUP()) && sarList.get(i - 1).isUP()) {
                    cas = sarList.get(i).getEndprice();
                    buy = true;
                    buyday = sarList.get(i).getStockday();
                    los = sarList.get(i).getEndprice() + 10;
                }
            }
            if (buy) {
                if ((sarList.get(i).isUP() && (!sarList.get(i - 1).isUP()))) {
                    if ((cas - sarList.get(i).getEndprice()) > 0) {
//                        System.out.println(buyday + "買 " + cas + "     " + sarList.get(i).getStockday() + "賣 " + sarList.get(i).getEndprice() + "   (成功)賺 :" + double2(cas - sarList.get(i).getEndprice()));

                        yes++;
                    } else {
//                        System.out.println(buyday + "買 " + cas + "     " + sarList.get(i).getStockday() + "賣 " + sarList.get(i).getEndprice() + "   (失敗) :" + double2(cas - sarList.get(i).getEndprice()));

                        err++;
                    }
                    buy = false;
                    total = total + cas - sarList.get(i).getEndprice();
                }
            }
        }
        System.out.println("========放空sar===============");
        printResult(yes, err, total);

        return result;

    }


    public Map<Object, Object> bolli(List<StockBean> list) {
        int dayCount = 20;
        List<Double> uplist = new ArrayList();
        List<Double> avglist = new ArrayList();
        List<Double> downlist = new ArrayList();
        List<Double> d = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            if (i < dayCount) {
                uplist.add(0.0);
                avglist.add(0.0);
                downlist.add(0.0);
                d.add(0.0);
                continue;
            }
            double sum = 0;
            for (int j = 0; j < dayCount; j++) {
                sum += list.get(i - j).getEndprice();
            }
            double avg = (sum / dayCount);
            double bo = 0;
            for (int j = 0; j < dayCount; j++) {
                bo += (list.get(i - j).getEndprice() - avg) * (list.get(i - j).getEndprice() - avg);
            }
            double z = Math.sqrt((bo / dayCount));
            uplist.add((double) (Math.round((avg + z) * 100) / 100));
            avglist.add((double) (Math.round((avg) * 100) / 100));
            downlist.add((double) (Math.round((avg - z) * 100) / 100));
            d.add((double) (Math.round((z + z) * 100) / 100));
        }
        Map<Object, Object> result = new HashMap<>(4);
        result.put("up", uplist);
        result.put("avg", avglist);
        result.put("down", downlist);
        result.put("d", d);
        return result;
    }

    /**
     * sar 加 20平均
     *
     * @param sarList 特别行政区列表
     */
    public void sarAdd20agv(List<SarBean> sarList) {
        int daycount = 20;
        double total = 0;
        //進價
        double cas = 0;
        double yes = 0;
        double err = 0;
        boolean buy = false;
        for (int i = 30; i < sarList.size() - 1; i++) {
            //
            if (sarList.get(i).isUP() && !buy) {
                double avg = 0;
                for (int x = 0; x < daycount; x++) {
                    avg = avg + sarList.get(i - x).getEndprice();
                }
                double avg2 = 0;
                for (int x = 1; x < daycount + 1; x++) {
                    avg2 = avg2 + sarList.get(i - x).getEndprice();
                }
                if (avg > avg2) {
                    cas = sarList.get(i + 1).getEndprice();
                    buy = true;
                }
            }


            if ((!sarList.get(i).isUP()) && sarList.get(i - 1).isUP() && buy) {
                if ((sarList.get(i + 1).getEndprice() - cas) > 0) {
                    yes++;
                } else {
                    err++;
                }
                buy = false;
                total = total + sarList.get(i).getEndprice() - cas;
            }
        }
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功比 " + (yes / (yes + err)));
        System.out.println("total = " + total);
        //sar = sar + 0.2 * (nav - sar)
    }


    private void printResult(double yes, double err, double total) {
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功率 " + Math.round(yes / (yes + err) * 100));
        System.out.println("total = " + total);
        System.out.println("=======================");
    }

    /**
     * 平均线
     *
     * @param list 列表
     * @param day  一天
     * @return {@link List}<{@link Double}>
     */
    public List<Double> avgLine(List<StockBean> list, Integer day) {
        List<Double> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            if ((i - day) < 0) {
                result.add(0.0);
                continue;
            }
            double sum = 0;
            for (int j = 0; j < day; j++) {
                sum = sum + list.get(i - j).getEndprice();
            }
            result.add(Math.round(sum / day * 100) / 100.0);
        }
        return result;
    }


    /**
     * avg结果
     *
     * @param list 列表
     * @param day1 俊
     * @param day2 day2
     * @param day3 把
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> avgResult(List<StockBean> list, int day1, int day2, int day3) {
        List<Double> a1 = avgLine(list, day1);
        List<Double> a2 = avgLine(list, day2);
        List<Double> a3 = avgLine(list, day3);
        List<String> mes = new ArrayList<>();
        String buyDay = null;
        Double cas = 0.0;
        Double total = 0.0;
        Double yes = 0.0;
        Double err = 0.0;
        boolean buy = false;
        for (int i = day3; i < a1.size(); i++) {
            if (!buy) {
                if (a1.get(i) > a2.get(i) && a2.get(i) > a3.get(i)) {
                    cas = list.get(i).getEndprice();
                    buy = true;
                    buyDay = list.get(i).getStockday();
                }
            }
            if (buy) {
                if (a1.get(i) < a2.get(i)) {
                    total = total + (a1.get(i) - cas);
                    buy = false;
                    if (list.get(i).getEndprice() > cas) {
                        yes++;
                    } else {
                        err++;
                    }
                    mes.add("買入  :" + buyDay + " 賣出:" + list.get(i).getStockday() + " 結果:" + Math.round(list.get(i).getEndprice() - cas));
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功率 " + Math.round(yes / (yes + err) * 100));
        System.out.println("total = " + Math.round(total));
        System.out.println("=======================");
        Map<String, Object> result = new HashMap<>(5);
        result.put("yes", yes);
        result.put("err", err);
        result.put("rate", Math.round(yes / (yes + err) * 100));
        result.put("total", Math.round(total));
        result.put("buyDay", mes);
        return result;
    }

    public void oneCrossThree(List<StockBean> list, int day1, int day2, int day3) {
        List<Double> a1 = avgLine(list, day1);
        List<Double> a2 = avgLine(list, day2);
        List<Double> a3 = avgLine(list, day3);
        boolean buy = false;
        Double cas = 0.0;
        double total = 0.0;
        double yes = 0.0;
        double err = 0.0;
        for (int i = day3 + 10; i < list.size(); i++) {
            if (!buy) {
                if (list.get(i).getHightprice() > a1.get(i) && list.get(i).getHightprice() > a2.get(i) && list.get(i).getHightprice() > a3.get(i)) {
                    if (list.get(i).getLowestprice() < a1.get(i) && list.get(i).getLowestprice() < a1.get(i) && list.get(i).getLowestprice() < a1.get(i)) {
                        buy = true;
                        cas = list.get(i).getEndprice();
                    }
                }
            }
            if (buy) {
                if (cas < a3.get(i)) {
                    buy = false;
                    total += list.get(i).getEndprice() - cas;
                    if (list.get(i).getEndprice() > cas) {
                        yes++;
                    } else {
                        err++;
                    }
                }
            }
        }
        System.out.println("=======================");
        System.out.println("yes = " + yes);
        System.out.println("err = " + err);
        System.out.println("成功率 " + Math.round(yes / (yes + err) * 100));
        System.out.println("total = " + Math.round(total));
        System.out.println("=======================");

    }

    public List<Integer> OBV(List<StockBean> list) {
        List<Integer> OBV = new ArrayList<>();
        OBV.add(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getEndprice() > list.get(i).getEndprice()) {
                OBV.add(OBV.get(i - 1) + list.get(i).getTransactionsnumber());

            } else {
                OBV.add(OBV.get(i - 1) - list.get(i).getTransactionsnumber());
            }
        }
        List<Integer> OBVavg = new ArrayList<>();
        for (int i = 0; i < OBV.size(); i++) {
            int sum = 0;
            for (int j = 0; j < 5; j++) {
                if (i - j < 0) {
                    continue;
                }
                sum += OBV.get(i - j);
            }
            OBVavg.add(sum / 10);
        }
        return OBVavg;
    }


    public Double double2(double n) {
        return Math.round(n * 100) / 100.0;

    }

    public List<Double> xEMA(List<StockBean> data, int n) {
        List<Double> nEMA = new ArrayList<>();
        nEMA.add(0.0);
        for (int i = 1; i < data.size(); i++) {
            nEMA.add(double2((nEMA.get(i - 1) * (n - 1) + data.get(i).getEndprice() * 2) / (n + 1)));
        }
        return nEMA;
    }


    public Map<String, List> MACD(List<StockBean> data) {
        //open ,close ,l,h
        //nEMA=(前一日nEMA*(n-1)＋今日收盤價×2)/(n+1)
        int n = 12;
        int m = 26;
        int x = 9;
        List<Double> nEMA = xEMA(data, n);
        List<Double> mEMA = xEMA(data, m);

        // DIF=nEMA－mEMA
        List<Double> DIF = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DIF.add(double2(nEMA.get(i) - mEMA.get(i)));
        }
        // xMACD=(前一日xMACD*(x-1)＋DIF×2)/(x+1)
        List<Double> xMACD = new ArrayList<>();
        xMACD.add(0.0);
        for (int i = 1; i < data.size(); i++) {
            xMACD.add(double2((xMACD.get(i - 1) * (x - 1) + DIF.get(i) * 2) / (x + 1)));
        }

        for (int i = 0; i < DIF.size(); i++) {
            DIF.set(i, double2(DIF.get(i) - xMACD.get(i)));
        }


        Map<String, List> result = new HashMap<>(4);
        result.put("DIF", DIF);
        result.put("xMACD", xMACD);
        result.put("nEMA", nEMA);
        result.put("mEMA", mEMA);
        return result;
    }

    public Map<String, List> KD(List<StockBean> data) {
        int n = 22;
        List<Double> RSV = new ArrayList<>();
        List<Double> k = new ArrayList<>();
        List<Double> d = new ArrayList<>();
        //最近n天內最低價
        Double nLow = data.get(0).getLowestprice();
        //最近n天內最高價
        Double nHeight = data.get(0).getHightprice();

        for (int i = 0; i < n; i++) {
            RSV.add(0.0);
            k.add(50.0);
            d.add(50.0);
        }
        for (int i = n; i < data.size(); i++) {
            nLow = data.get(i).getLowestprice();
            nHeight = data.get(i).getHightprice();
            for (int j = 0; j < n; j++) {
                if (data.get(i - j).getLowestprice() < nLow) {
                    nLow = data.get(i - j).getLowestprice();
                }
                if (data.get(i - j).getHightprice() > nHeight) {
                    nHeight = data.get(i - j).getHightprice();
                }
            }
            //RSV = 第n天收盤價-最近n天內最低價/最近n天內最高價-最近n天內最低價*100
            RSV.add(double2((data.get(i).getEndprice() - nLow) / (nHeight - nLow) * 100));
            // 當日K值(%K)= 2/3 前一日 K值 + 1/3 RSV
            k.add(double2((2 * k.get(i - 1) / 3) + (RSV.get(i) / 3)));
            // 當日D值(%D)= 2/3 前一日 D值＋ 1/3 當日K值
            d.add(double2((2 * d.get(i - 1) / 3) + (k.get(i) / 3)));
        }
        Map<String, List> result = new HashMap<>();
        result.put("k", k);
        result.put("d", d);
        return result;
    }

    public List<BiasBean> bias(List<StockBean> list) {
        int day1 = 6;
        int precision = 100;
        List<Double> a1 = avgLine(list, day1);
        List<BiasBean> result = new ArrayList<>(list.size());
        for (int i = 0; i < day1; i++) {
            a1.set(i, list.get(i).getEndprice());
        }
        // 當日股價-最近n日平均股價// 最近n日平均股價
        for (int i = 0; i < list.size(); i++) {
            result.add(    new BiasBean(list.get(i).getStockday(),double2((list.get(i).getEndprice() - a1.get(i)) / a1.get(i) * precision))                      );
        }
        return result;
    }


}
