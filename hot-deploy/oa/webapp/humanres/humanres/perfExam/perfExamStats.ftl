<#if data?has_content>
    <#assign evaluateYear = data.evaluateYear?default('')>
    <#assign evaluateMonth = data.evaluateMonth?default('')>
    <#assign statisticsMap = data.statisticsMap?default('')>
</#if>

<div class="col-xs-5" id="approveChart"></div>
<div class="col-xs-5" id="resultChart"></div>
<script type="text/javascript">
    $(function(){
        var $approveChart = $("#approveChart");
        $approveChart.highcharts({
            credits:{
                enabled: true,
                href: "http://www.rextec.com.cn",
                text: "RexTec.com.cn"
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 240
            },
            title: {
//                text: $("select[name=evaluateYear]").val()+ '年' + $("select[name=evaluateMonth]").val()+ '月'+ ' 考评审核统计'
                text: '${evaluateYear}年${evaluateMonth}月考评审核统计'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b> : {point.y}人'
            },
            exporting:{
                enabled: false
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true
                }
            },
            series: [{
                type: 'pie',
                name: '占比',
                data: [
                    {
                        name: '未评',
                        y: ${statisticsMap.notEvaluateCount},
                        color: 'red'},
                    {
                        name: '已评',
                        y: ${statisticsMap.evaluateCount},
                        color: 'blue'
                    },
                    {
                        name: '已初审',
                        y: ${statisticsMap.revieweCount},
                        color: 'brown'
                    },
                    {
                        name: '已终审',
                        y: ${statisticsMap.approveCount},
                        sliced: true,
                        selected: true,
                        color: 'green'
                    },
                    {
                        name: '已归档',
                        y:${statisticsMap.finalizeCount},
                        color: 'gray'

                    }
                ]
            }]
        });
        var resultChart = $("#resultChart");
        resultChart.highcharts({
            credits:{
                enabled: true,
                href: "http://www.rextec.com.cn",
                text: "RexTec.com.cn"
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 240
            },
            title: {
                /*text: $("select[name=evaluateYear]").val()+ '年' + $("select[name=evaluateMonth]").val()+ '月' + ' 考评结果统计'*/
                text: '${evaluateYear}年${evaluateMonth}月考评结果统计'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b> : {point.y}人'
            },
            exporting:{
                enabled: false
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true
                }
            },
            series: [{
                type: 'pie',
                name: '占比',
                data: [
                    {
                        name: 'A+',
                        y: ${statisticsMap.ACount1},
                        sliced: true,
                        selected: true,
                        color: 'red'
                    },
                    {
                        name: 'A',
                        y: ${statisticsMap.ACount},
                        color: 'blue'
                    },
                    {
                        name: 'B+',
                        y: ${statisticsMap.BCount1},
                        color: 'brown'
                    },
                    {
                        name: 'B',
                        y:${statisticsMap.BCount},
                        color: 'green'

                    },
                    {
                        name: 'C',
                        y:${statisticsMap.CCount},
                        color: 'gray'

                    },
                    {
                        name: 'C-',
                        y:${statisticsMap.CCount1},
                        color: 'yellow'

                    }
                ]
            }]
        });
    })
</script>