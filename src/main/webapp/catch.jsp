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
        <br>
        <div class="app">




            <el-input v-model="input" placeholder="请输入内容"></el-input>

            <br>

            <!-- <el-date-picker v-model="start" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
                :picker-options="pickerOptions">
            </el-date-picker>

            <el-date-picker v-model="end" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
                :picker-options="pickerOptions">
            </el-date-picker> -->
            <br>
            <p style="color: red;">警告 每11秒抓取1個月資料,從100年開始須,費時20幾分鐘</p>

            <el-button type="primary" @click="catchMessage">抓取按钮</el-button>


        </div>
        <script>
            const vm = new Vue({
                el: ".app",
                data() {
                    return {
                        loading: true,
                        input: "",
                    }
                },
                created() {
                    $.ajax({
                        url: '${pageContext.request.contextPath}/checkCatch',
                        type: 'get',
                        async: false,//同步請求
                        cache: false,//不快取頁面
                        success: response => {
                            if (response) {
                                this.loading = this.$loading({
                                    fullscreen: true,
                                    text: "資料抓取中",
                                    spinner: "el-icon-loading",
                                    background: "rgba(0, 0, 0, 0.8)",
                                })
                            }
                            console.log(response)
                        },
                        error: function (returndata) {
                            console.log(returndata);
                        }
                    });
                },
                mounted() {

                },
                methods: {
                    catchMessage() {
                        this.loading = this.$loading({
                            fullscreen: true,
                            text: "資料抓取中",
                            spinner: "el-icon-loading",
                            background: "rgba(0, 0, 0, 0.8)",
                        })
                        $.ajax({
                            url: '${pageContext.request.contextPath}/CatchStock',
                            type: 'POST',
                            data:"name="+this.input,
                            async: false,//同步請求
                            cache: false,//不快取頁面
                            success: response => {
                                
                                console.log(response)
                            },
                            error: function (returndata) {
                                console.log(returndata);
                            }
                        });




                    }
                },
            })
        </script>
        <style>
            body {
                margin: 0;
            }
        </style>

    </body>

    </html>