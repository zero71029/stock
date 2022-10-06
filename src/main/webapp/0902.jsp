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
        <div class="app" style="font-size: 20px;">
            <div v-for="(s, index) in sar" :key="index">{{s}}</div>
        </div>

    </body>

    </html>


    <script>

        const vm = new Vue({
            el: ".app",
            data() {
                return {
                    stockName: "0050",
                    sar:[],

                }
            },
            created() {
                $.ajax({
                        url: '${pageContext.request.contextPath}/stock-DJI.json?name=' + this.stockName,
                        type: 'POST',
                        async: false,//同步請求
                        cache: false,//不快取頁面
                        success: rawData => {
                            var data = splitData(rawData);
                            console.log(data);
                             this.sar = sar(data);
                        },
                        error: function (returndata) {
                            console.log(returndata);
                        }
                    });
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
                    color = "R";
                    if (val[3] > nav) {
                        nav = val[3];
                        if (AF < 0.2) {
                            AF = AF + 0.02
                        }
                    }

                    sar = sar + AF * (nav - sar);
                    //最低 跌破sar
                    if (sar > val[2]) {
                        isUP = false;
                        AF = 0.02;
                        sar = val[3];
                        nav = val[2];
                    }
                } else {
                    color = "b";
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
                list.push([data.categoryData[i], sar, color,"o "+val[0],"c "+val[1],"l "+val[2],"h "+val[3]]);
                i++;
            }
            console.log("==Sar==")
            console.log(list)
            return list;

        }




    </script>