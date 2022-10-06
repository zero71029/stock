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

            <!-- hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.
<div id="main" style="width: 100%;height:900px;">圖所在</div>
hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh -->
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

                        }
                    },
                    created() {
                        // $.ajax({
                        //     url: '${pageContext.request.contextPath}/zero',
                        //     type: 'POST',
                        //     async: false,//同步請求
                        //     cache: false,//不快取頁面
                        //     success: response => {
                        //         this.price = response.price;
                        //         this.list = response;
                        //         this.day5 = response.day5;
                        //         this.day20 = response.day20;
                        //         this.day80 = response.day80;
                        //         this.buyday = response.buyday;
                        //         this.xAxis = response.xAxis...;
                        //         console.log(response);
                        //         this.char();
                        //     },.
                        //     error: function (returndata) {
                        //         console.log(returndata);
                        //     }
                        // });vdsfffffsdjjjee
                    },
                    mounted() {
                        // this.char();
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
                        },
                        char() {
                            var chartDom = document.getElementById('main');
                            var myChart = echarts.init(chartDom);
                            var option;
                            option = {
                                title: {
                                    text: 'Stacked Line'
                                },
                                tooltip: {
                                    trigger: 'axis'
                                },

                                grid: {
                                    left: '3%',
                                    right: '4%',
                                    bottom: '3%',
                                    containLabel: true
                                },
                                toolbox: {
                                    feature: {
                                        saveAsImage: {}
                                    }
                                },
                                xAxis: {
                                    type: 'category',
                                    boundaryGap: false,
                                    data: this.xAxis
                                },
                                yAxis: {
                                    type: 'value',

                                },
                                series: [
                                    {
                                        name: 'price',
                                        data: this.price,
                                        type: 'line'
                                    },
                                    {
                                        name: 'day5',

                                        data: this.day5,
                                        type: 'line'
                                    },
                                    {
                                        name: 'day20',

                                        data: this.day20,
                                        type: 'line'
                                    },
                                    {
                                        name: 'day80',
                                        data: this.day80,
                                        type: 'line'
                                    }
                                    ,
                                    {
                                        name: 'buyday',
                                        data: this.buyday,
                                        type: 'line'
                                    }
                                ]
                            };
                            option && myChart.setOption(option);
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
            </script>

        </body>

        </html>