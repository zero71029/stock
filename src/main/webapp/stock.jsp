<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
        <script src="//unpkg.com/element-ui/lib/umd/locale/zh-TW.js"></script>
        <script src="${pageContext.request.contextPath}/js/echarts.min.js"></script>
    </head>

    <body>
        <div class="app">
            <input type="text" v-model="stockName">
            <button @click="showCandlestick">xxxxx</button>
        </div>
        hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
        <div id="main" style="width: 1900px;height:900px;">圖所在</div>
        hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    </body>

    </html>


    <script>

        const vm = new Vue({
            el: ".app",
            data() {
                return {
                    stockName: ""
                }
            },
            created() {

            },
            methods: {
                showCandlestick() {
                    var chartDom = document.getElementById('main');
                    var myChart = echarts.init(chartDom);
                    var option;


                    $.ajax({
                        url: '${pageContext.request.contextPath}/stock-DJI.json?name=' + this.stockName,
                        type: 'POST',
                        async: false,//同步請求
                        cache: false,//不快取頁面
                        success: rawData => {
                            var data = splitData(rawData);
                            console.log(data);


                            myChart.setOption(
                                (option = {
                                    animation: false,
                                    legend: {
                                        bottom: 10,
                                        left: 'center',
                                        data: ['Dow-Jones index', 'MA5', 'MA10', 'MA20', 'MA30']
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
                                    visualMap: [
                                        {
                                            type:"piecewise",
                                            show: false,
                                            seriesIndex: 5,
                                            dimension: 2,
                                            pieces: [
                                                {
                                                    value: 1,
                                                    color: '#ec0000',
                                                    backgroundColor: "#ec0000",
                                                    opacity:1

                                                },
                                                {
                                                    value: -1,
                                                    color: "#00da3c",
                                                    backgroundColor: "#00da3c",
                                                    opacity:1
                                                }
                                            ]
                                        },
                                        {
                                            show: true,
                                            seriesIndex: 6,
                                            dimension: 2,
                                            pieces: [
                                                {
                                                    value: 1,
                                                    color: '#ec0000'

                                                },
                                                {
                                                    value: -1,
                                                    color: "#00da3c"
                                                }
                                            ]
                                        }],
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
                                        }
                                    ],
                                    dataZoom: [
                                        {
                                            type: 'inside',
                                            xAxisIndex: [0, 1],
                                            start: 93,
                                            end: 100
                                        },
                                        {
                                            show: true,
                                            xAxisIndex: [0, 1],
                                            type: 'slider',
                                            top: '85%',
                                            start: 93,
                                            end: 100
                                        }
                                    ],
                                    series: [
                                        {
                                            name: 'Dow-Jones index',
                                            // type: 'candlestick',
                                            type: 'line',
                                            data: data.endprice,
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
                                        {
                                          name: 'MA5',
                                          type: 'line',
                                          data: calculateMA(5, data),
                                          smooth: true,
                                          lineStyle: {
                                            opacity: 0.5
                                          }
                                        },
                                        {
                                          name: 'MA10',
                                          type: 'line',
                                          data: calculateMA(10, data),
                                          smooth: true,
                                          lineStyle: {
                                            opacity: 0.5
                                          }
                                        },
                                        {
                                          name: 'MA20',
                                          type: 'line',
                                          data: calculateMA(20, data),
                                          smooth: true,
                                          lineStyle: {
                                            opacity: 0.5
                                          }
                                        },
                                        {
                                          name: 'MA80',
                                          type: 'line',
                                          data: calculateMA(80, data),
                                          smooth: true,
                                          lineStyle: {
                                            opacity: 0.5
                                          }
                                        },
                                        {
                                            name: 'SAR',
                                            type: 'scatter',
                                            data: sar(data),
                                        },
                                        {
                                            name: 'Volume',
                                            type: 'bar',
                                            xAxisIndex: 1,
                                            yAxisIndex: 1,
                                            data: data.volumes
                                        }
                                    ]
                                }),
                                true
                            );
                           
                        },
                        error: function (returndata) {
                            console.log(returndata);
                        }
                    });
                    // option && myChart.setOption(option);
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
            console.log(data);
            //open ,close ,l,h
            //sar = sar + 0.2 * (nav - sar)      
            let isUP = true;
            let nav = data.values[0][3];//最高價
            let sar = data.values[0][1];
            let AF = 0.02;
            let color = 1;
            let list = [];
            let i = 0;
            for (const val of data.values) {
                if (isUP) {
                    color = 1;
                    if (val[3] > nav) {
                        nav = val[3];
                        if (AF < 0.2) {
                            AF = AF + 0.02
                        }
                    }

                    sar = sar + AF * (nav - sar);
                    //收盤 跌破sar
                    if (sar > val[2]) {
                        isUP = false;
                        AF = 0.02;
                        sar = val[3];
                        nav = val[2];
                    }
                } else {
                    color = -1;
                    if (val[2] < nav) {
                        nav = val[2];
                        if (AF < 0.2) {
                            AF = AF + 0.02
                        }
                    }
                    sar = sar - AF * (sar - nav);
                    //收盤 長破sar
                    if (sar < val[3]) {
                        isUP = true;
                        AF = 0.02;
                        sar = val[2];
                        nav = val[3];
                    }
                }
                sar = Math.round(sar * 100) / 100;
                list.push([data.categoryData[i], sar, color]);
                i++;
            }
            console.log("==Sar==")
            console.log(list)
            return list;

        }




    </script>