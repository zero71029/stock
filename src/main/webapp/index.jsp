<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Document</title>
            <script src="${pageContext.request.contextPath}/js/jquery-3.4.1.js"></script>
            <!-- 引入样式 vue-->
            <script src="${pageContext.request.contextPath}/js/vue.min.js"></script>
            <script src="${pageContext.request.contextPath}/js/axios.min.js"></script>
            <!-- 引入element-ui样式 -->
            <link rel="stylesheet" href="${pageContext.request.contextPath}/js/element-ui.css">
            <!-- 引入element-ui组件库 -->
            <script src="${pageContext.request.contextPath}/js/element-ui.js"></script>
            <script src="${pageContext.request.contextPath}/js/zh-TW.js"></script>
            <script src="${pageContext.request.contextPath}/js/echarts.min.js"></script>
        </head>

        <body>           

            <div class="app">
                <el-button type="primary" icon="el-icon-arrow-left" size="small" @click="next('right')"></el-button>

                <el-input v-model="stockNum" placeholder="请输入内容" list="browsers" style="width: 170px;"></el-input>
                <datalist id="browsers">
                    <c:forEach varStatus="loop" begin="0" end="${stockName.size()-1}" items="${stockName}" var="s">
                        <option value="${s}"></option>
                    </c:forEach>
                </datalist>
                <el-button type="primary" icon="el-icon-arrow-right" size="small" @click="next('left')"></el-button>
                <br><br>
                <el-select v-model="norm" placeholder="指標">
                    <el-option label="三平均線" value="avg"></el-option>
                    <el-option label="SAR" value="sar"></el-option>
                    <!-- <el-option  label="RSI" value="rsi"></el-option> -->
                </el-select>
                <el-button type="primary" @click="clickStock">送出</el-button>
                <br>
                <div id="main" style="width: 100%;height:900px;">圖所在</div>
                <div v-show="yes != ''">
                    成功 = {{yes}} 次<br>
                    失敗 = {{err}} 次<br>
                    成功率 {{rate}}%<br>
                    結算 = {{total}}<br>
                </div>
                <div v-for="(s, index) in process" :key="index">{{s}}</div>


                <!-- <el-date-picker v-model="start" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
        :picker-options="pickerOptions">
    </el-date-picker>

    <el-date-picker v-model="end" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
        :picker-options="pickerOptions">...
    </el-date-picker> -->


            </div>
            <script>let stockName = [];</script>


            <c:forEach varStatus="loop" begin="0" end="${stockName.size()-1}" items="${stockName}" var="s">
                <script>stockName.push('${s}');</script>
            </c:forEach>
            <script>
                const upColor = '#ec0000';
                const downColor = '#00da3c';
                // 2207 :473
                // 2327 :483
                // 1476 :334
                // 2454 :303
                // 2492 :307
                const vm = new Vue({
                    el: ".app",
                    data() {
                        return {
                            stockNum: "2330",
                            norm: "avg",
                            yes: "",
                            err: "",
                            total: "",
                            rate: "",
                            process: [],
                            input: "",
                            list: [],
                            start: "",
                            end: "",
                            currentPage: 1,
                            pageSize: 20,
                            day20: [],
                            day5: [],
                            day80: [],
                            buyday: [],
                            xAxis: [],
                            option:{},
                        }
                    },
                    created() {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/stock-DJI.json?name=' + this.stockNum,
                            type: 'POST',
                            async: false,//同步請求
                            cache: false,//不快取頁面
                            success: rawData => {                              
                                var data = splitData(rawData);
                                console.log(data);
                                this.option = this.setOption(data);                                                                                     
                            }
                        })
                    },
                    mounted() {
                        // this.char(this.option);
                    },
                    methods: {
                        clickStock() {
                            if (this.norm == "" && this.stockNum == "") {
                                this.$message.error("輸入錯誤");
                                return;
                            }
                            $.ajax({
                                url: '${pageContext.request.contextPath}/calculationResults',
                                type: 'POST',
                                data: "stockNum=" + this.stockNum + "&norm=" + this.norm,
                                async: false,//同步請求
                                cache: false,//不快取頁面
                                success: response => {
                                    console.log(response);
                                    if (response.code == 200) {
                                        this.$message.success(response.message);
                                        this.yes = response.data.yes;
                                        this.err = response.data.err;
                                        this.total = response.data.total;
                                        this.rate = response.data.rate;//
                                        this.process = response.data.buyDay;
                                    }

                                    if (response.code == 500) this.$message.error(response.message);
                                },
                                error: function (returndata) {
                                    console.log(returndata);
                                }
                            });
                            $.ajax({
                            url: '${pageContext.request.contextPath}/stock-DJI.json?name=' + this.stockNum,
                            type: 'POST',
                            async: false,//同步請求
                            cache: false,//不快取頁面
                            success: rawData => {                              
                                var data = splitData(rawData);
                                console.log(data);
                                this.option = this.setOption(data);
                                this.char(this.option);                                                         
                            }
                        })
                        },
                        char(option) {
                            let myChart = echarts.init(document.getElementById('main'), 'dark');
                            myChart.setOption(option, true);
                            // myChart.dispose();
                        },
                        setOption(data) {
                            console.log(data);
                            return {
                                animation: false,
                                legend: {
                                    bottom: 10,
                                    left: 'center',
                                    data: ['Dow-Jones index', 'MA20', 'SAR']
                                },
                                tooltip: {
                                    trigger: 'axis',
                                    axisPointer: {
                                        type: 'cross'
                                    },
                                    borderWidth: 1,
                                    borderColor: '#ccc',
                                    padding: 10,
                                    textStyle: {
                                        color: '#000'
                                    },
                                    position: function (pos, params, el, elRect, size) {
                                        const obj = {
                                            top: 10
                                        };
                                        obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
                                        return obj;
                                    }
                                    // extraCssText: 'width: 170px'
                                },
                                axisPointer: {
                                    link: [
                                        {
                                            xAxisIndex: 'all'
                                        }
                                    ],
                                    label: {
                                        backgroundColor: '#777'
                                    }
                                },
                                toolbox: {
                                    feature: {
                                        dataZoom: {
                                            yAxisIndex: false
                                        },
                                        brush: {
                                            type: ['lineX', 'clear']
                                        }
                                    }
                                },
                                brush: {
                                    xAxisIndex: 'all',
                                    brushLink: 'all',
                                    outOfBrush: {
                                        colorAlpha: 0.1
                                    }
                                },

                                grid: [
                                    {
                                        left: '10%',
                                        right: '8%',
                                        height: '50%'
                                    },
                                    {
                                        left: '10%',
                                        right: '8%',
                                        top: '63%',
                                        height: '16%'
                                    }
                                ],
                                xAxis: [
                                    {
                                        type: 'category',
                                        data: data.categoryData,
                                        boundaryGap: false,
                                        axisLine: { onZero: false },
                                        splitLine: { show: false },
                                        min: 'dataMin',
                                        max: 'dataMax',
                                        axisPointer: {
                                            z: 100
                                        }
                                    },
                                    {
                                        type: 'category',
                                        gridIndex: 1,
                                        data: data.categoryData,
                                        boundaryGap: false,
                                        axisLine: { onZero: false },
                                        axisTick: { show: false },
                                        splitLine: { show: false },
                                        axisLabel: { show: false },
                                        min: 'dataMin',
                                        max: 'dataMax'
                                    }
                                ],
                                yAxis: [
                                    {
                                        scale: true,
                                        splitArea: {
                                            show: true
                                        }
                                    },
                                    {
                                        scale: true,
                                        gridIndex: 1,
                                        splitNumber: 2,
                                        axisLabel: { show: false },
                                        axisLine: { show: false },
                                        axisTick: { show: false },
                                        splitLine: { show: false }
                                    },

                                ],
                                dataZoom: [
                                    {
                                        type: 'inside',
                                        xAxisIndex: [0, 1],
                                        start: 98,
                                        end: 100
                                    },
                                    {
                                        show: true,
                                        xAxisIndex: [0, 1],
                                        type: 'slider',
                                        top: '85%',
                                        start: 98,
                                        end: 100
                                    }
                                ],
                                // visualMap: [
                                //     {
                                //         type: "piecewise",
                                //         show: true,
                                //         seriesIndex: 2,
                                //         dimension: 2,
                                //         pieces: [
                                //             {
                                //                 value: 1,
                                //                 color: '#ec0000',
                                //                 backgroundColor: "#ec0000",
                                //                 opacity: 1

                                //             },
                                //             {
                                //                 value: -1,
                                //                 color: "#00da3c",
                                //                 backgroundColor: "#00da3c",
                                //                 opacity: 1
                                //             }
                                //         ]
                                //     },
                                //     {
                                //         show: true,
                                //         seriesIndex: 3,
                                //         dimension: 2,
                                //         pieces: [
                                //             {
                                //                 value: 1,
                                //                 color: '#ec0000'

                                //             },
                                //             {
                                //                 value: -1,
                                //                 color: "#00da3c"
                                //             }
                                //         ]
                                //     }],
                                series: [
                                    {
                                        name: 'Dow-Jones index',
                                        type: 'line',
                                        // type: 'candlestick',
                                        data: data.values,
                                        itemStyle: {
                                            color: upColor,
                                            color0: downColor,
                                            borderColor: undefined,
                                            borderColor0: undefined
                                        },
                                        tooltip: {
                                            formatter: function (param) {
                                                param = param[0];
                                                return [
                                                    'Date: ' + param.name + '<hr size=1 style="margin: 3px 0">',
                                                    'Open: ' + param.data[0] + '<br/>',
                                                    'Close: ' + param.data[1] + '<br/>',
                                                    'Lowest: ' + param.data[2] + '<br/>',
                                                    'Highest: ' + param.data[3] + '<br/>'
                                                ].join('');
                                            }
                                        }
                                    },
                                    // {
                                    //     name: 'MA20',
                                    //     type: 'line',
                                    //     data: calculateMA(20, data),
                                    //     smooth: true,
                                    //     lineStyle: {
                                    //         opacity: 0.5
                                    //     }
                                    // },
                                    {
                                        name: 'SAR',
                                        type: 'scatter',
                                        data: sar(data),
                                    },
                                    // {
                                    //     name: '12EMA',
                                    //     type: 'line',
                                    //     xAxisIndex: 1,
                                    //     yAxisIndex: 1,
                                    //     data: xEMA(data.values, 12)
                                    // },
                                    // {
                                    //     name: '26EMA',
                                    //     type: 'line',
                                    //     xAxisIndex: 1,
                                    //     yAxisIndex: 1,
                                    //     data: xEMA(data.values, 26)
                                    // },
                                    {
                                        name: 'DIF',
                                        type: 'bar',
                                        xAxisIndex: 1,
                                        yAxisIndex: 1,
                                        data: MACD(data.values).DIF
                                    },
                                    {
                                        name: 'xMACD',
                                        type: 'line',
                                        xAxisIndex: 1,
                                        yAxisIndex: 1,
                                        data: MACD(data.values).xMACD
                                    },
                                ]
                            };
                        },
                        changeDate() {
                            if (this.input == "") this.input = "5871";
                            let data = "start=" + this.start + "&end=" + this.end;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/selectStock/' + this.input,
                                type: 'POST',
                                data: data,
                                async: false,
                                cache: false,
                                success: (response => {
                                    this.list = response;
                                    this.day5 = response.day5;
                                    this.day20 = response.day20;
                                    this.day80 = response.day80;
                                    this.buyday = response.buyday;
                                    this.xAxis = response.xAxis;
                                    this.price = response.price;
                                    console.log(response);
                                    this.char();
                                }),
                                error: function (returndata) {
                                    console.log(returndata);
                                }
                            })
                        },
                        next(n) {
                            if (n == 'left') {
                                if (stockName.indexOf(this.stockNum) + 1 >= stockName.length) {
                                    this.stockNum = stockName[stockName.indexOf(this.stockNum)];
                                } else {
                                    this.stockNum = stockName[stockName.indexOf(this.stockNum) + 1];
                                }
                            } else {
                                if (stockName.indexOf(this.stockNum) - 1 < 0) {
                                    this.stockNum = stockName[0];
                                }
                                else {
                                    this.stockNum = stockName[stockName.indexOf(this.stockNum) - 1];
                                }
                            }

                            $.ajax({
                                url: '${pageContext.request.contextPath}/calculationResults',
                                type: 'POST',
                                data: "stockNum=" + this.stockNum + "&norm=" + this.norm,
                                async: false,//同步請求
                                cache: false,//不快取頁面
                                success: response => {
                                    console.log(response);
                                    if (response.code == 200) {
                                        this.$message.success(response.message);
                                        this.yes = response.data.yes;
                                        this.err = response.data.err;
                                        this.total = response.data.total;
                                        this.rate = response.data.rate;//
                                        this.process = response.data.buyDay;
                                    }

                                    if (response.code == 500) this.$message.error(response.message);
                                },
                                error: function (returndata) {
                                    console.log(returndata);
                                }
                            });


                        }
                    },
                })
 
                function splitData(rawData) {
                    let categoryData = [];
                    let values = [];
                    let volumes = [];
                    let endprice = [];
                    for (let i = 0; i < rawData.length; i++) {
                        categoryData.push(rawData[i].splice(0, 1)[0]);
                        values.push(rawData[i]);
                        endprice.push(rawData[i][1]);
                        volumes.push([i, rawData[i][4], rawData[i][0] > rawData[i][1] ? 1 : -1]);
                    }
                    return {
                        categoryData: categoryData,
                        values: values,
                        volumes: volumes,
                        endprice: endprice
                    };
                }
                function calculateMA(dayCount, data) {
                    var result = [];
                    for (var i = 0, len = data.values.length; i < len; i++) {
                        if (i < dayCount) {
                            result.push('-');
                            continue;
                        }
                        var sum = 0;
                        for (var j = 0; j < dayCount; j++) {
                            sum += data.values[i - j][1];
                        }
                        result.push(+(sum / dayCount).toFixed(3));
                    }
                    return result;
                }
                function sar(data) {
                    //open ,close ,l,h
                    //sar = sar + 0.2 * (nav - sar)      
                    let isUP = true;
                    let nav = data.values[0][3];//最高價
                    let sar = data.values[0][1];
                    let AF = 0.02;
                    let color = 1;
                    let list = [];
                    let val = data.values;
                    //oclh
                    for (let i = 0; i < data.values.length; i++) {
                        if (isUP) {
                            color = 1;
                            if (val[i][3] > nav) {
                                nav = val[i][3];
                                if (AF < 0.2) {
                                    AF = AF + 0.01
                                }
                            }
                            sar = sar + AF * (nav - sar);
                            //收盤 跌破sar
                            if (sar > val[i][2]) {
                                color = -1;
                                isUP = false;
                                AF = 0.02;
                                for (let j = 0; j < 20; j++) {
                                    let ins = (i - j);
                                    if (ins < 0) ins = 0;
                                    if (sar < val[ins][3]) {
                                        sar = val[ins][3];
                                    }
                                }
                                nav = val[i][2];
                            }
                        } else {
                            color = -1;
                            if (val[i][2] < nav) {
                                nav = val[i][2];
                                if (AF < 0.2) {
                                    AF = AF + 0.01
                                }
                            }
                            sar = sar - AF * (sar - nav);
                            //收盤 長破sar
                            if (sar < val[i][3]) {
                                color = 1;
                                isUP = true;
                                AF = 0.02;
                                for (let j = 0; j < 20; j++) {
                                    let ins = (i - j);
                                    if (ins < 0) ins = 0;
                                    if (sar > val[ins][2]) {
                                        sar = val[ins][2];
                                    }
                                }
                                nav = val[i][3];
                            }
                        }
                        sar = Math.round(sar * 100) / 100;
                        list.push([data.categoryData[i], sar, color]);
                    }
                    console.log("==Sar==")           
                    return list;

                }
                function xEMA(data, n) {
                    let nEMA = [];
                    nEMA.push(0.0);
                    for (let i = 1; i < data.length; i++) {
                        nEMA.push(double2((nEMA[i - 1] * (n - 1) + data[i][1] * 2) / (n + 1)));
                    }
                    return nEMA;
                }

                function MACD(data) {
                    console.log("macd")
                    //open ,close ,l,h
                    //nEMA=(前一日nEMA*(n-1)＋今日收盤價×2)/(n+1)  
                    let DIF = [];             
                    let nEMA = xEMA(data, 12);                    
                    let mEMA = xEMA(data, 26);
                    // DIF=nEMA－mEMA
                    console.log(nEMA)
                    console.log(mEMA)
                    for (let i = 0; i < data.length; i++) {
                        DIF.push(double2(nEMA[i] - mEMA[i]));
                    }
                    console.log("========")
                    // xMACD=(前一日xMACD*(x-1)＋DIF×2)/(x+1)
                    let xMACD = [];
                    xMACD.push(0.0);
                    for (let i = 1; i < data.length; i++) {
                        xMACD.push(double2((xMACD[i - 1] * (9 - 1) + DIF[i] * 2) / (9 + 1)));
                    }
                    console.log("========")
                    for (let i = 0; i < data.length; i++) {
                        DIF[i] = double2(DIF[i] - xMACD[i]);
                    }
                    console.log("macd888")
                    return { 'nEMA': nEMA, 'mEMA': mEMA, 'DIF': DIF, "xMACD": xMACD };
                }
                function double2(n) {               
                    return Math.round(n * 100) / 100;
                }

            </script>

        </body>

        </html>