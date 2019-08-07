<script type="text/javascript">
    $(function(){
        var $pieChart = $("#pieChart");
        $pieChart.css("width", "100%");
        $pieChart.highcharts({
            credits:{
                enabled: true,
                href: "http://www.rextec.com.cn",
                text: "RexTec.com.cn"
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height:300
            },
            title: {
                text:'${year}' + '年度薪资发放统计'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
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
                        name: '未发(${data.notSend})',
                        y: ${data.notSend},
                        color: 'red'},
                    {
                        name: '未通过(${data.disapprove})',
                        y: ${data.disapprove},
                        color: 'purple'},
                    {
                        name: '未审(${data.notExamine})',
                        y: ${data.notExamine},
                        color: 'blue'
                    },
                    {
                        name: '已审(${data.approve})',
                        y: ${data.approve},
                        sliced: true,
                        selected: true,
                        color: 'green'
                    },
                    {
                        name: '已发(${data.sent})',
                        y:${data.sent},
                        color: 'gray'
                    }
                ]
            }]
        });
    })
</script>



