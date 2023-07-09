<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>交易统计图表</title>
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-1.11.1-min.js"></script>

    <script>
        $(function () {
            //页面加载完毕后，绘制统计图表
            getCharts();
        })
        
        function getCharts() {

            $.ajax({
                url:"workbench/transaction/getCharts.do",
                dataType:"JSON",
                type:"get",
                success:function (data) {
                    /*
                        data
                            {"total":100,"dataList":[{value:60,name:'01资质审查'},{value:114,name:'02需求分析'},{....}}]}
                     */
                    //基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById("main"))
                    // 指定图表的配置项和数据
                    var option1 = {
                        title: {
                            text: '交易阶段漏斗图',
                            subtext: '统计交易阶段数量的漏斗图'
                        },
                        series: [
                            {
                                name: 'Funnel',
                                type: 'funnel',
                                left: '10%',
                                top: 60,
                                bottom: 60,
                                width: '80%',
                                min: 0,
                                max: data.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.dataMap
                            }
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option1);

                    var arr1 = []
                    $.each(data.dataMap,function (i,n) {
                        var name = n.name;
                        arr1.push(name);
                    })

                    var arr2 = []
                    $.each(data.dataMap,function (i,n) {
                        var value =n.value;
                        arr2.push(value)
                    })

                    var myChart2 = echarts.init(document.getElementById("main2"))
                    var option2 = {
                        title: {
                            text: '交易阶段柱状图',
                            subtext: '统计交易阶段数量的柱状图'
                        },
                        xAxis: {
                            type: 'category',
                            data:arr1
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                data: arr2,
                                type: 'bar',
                                showBackground: true,
                                backgroundStyle: {
                                    color: 'rgba(180, 180, 180, 0.2)'
                                }
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart2.setOption(option2);

                    var myChart3 = echarts.init(document.getElementById("main3"))
                    var option3 = {
                        title: {
                            text: '交易阶段饼图',
                            subtext: '统计交易阶段数量的饼图',
                        },
                        tooltip: {
                            trigger: 'item'
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left'
                        },
                        series: [
                            {
                                name: '交易阶段',
                                type: 'pie',
                                radius: '50%',
                                data:data.dataMap,
                                emphasis: {
                                    itemStyle: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                    }
                                }
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart3.setOption(option3);

                    var myChart4 = echarts.init(document.getElementById("main4"))
                    var option4 = {
                        title: {
                            text: '交易阶段折线图',
                            subtext: '统计交易阶段数量的折线图',
                        },
                        xAxis: {
                            type: 'category',
                            data: arr1
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                data: arr2,
                                type: 'line'
                            }
                        ]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart4.setOption(option4);
                }
            })
        }
    </script>
</head>
<body>
    <div style="display: flex">
        <!--为ECharts准备一个具备大小(宽高)的 DOM -->
        <div id="main" style="width:600px;height:400px;"></div>
        <div id="main2" style="width:600px;height:400px;"></div>
    </div>
    <div style="display: flex">
        <div id="main3" style="width:600px;height:400px;"></div>
        <div id="main4" style="width:600px;height:400px;"></div>
    </div>

</body>
</html>