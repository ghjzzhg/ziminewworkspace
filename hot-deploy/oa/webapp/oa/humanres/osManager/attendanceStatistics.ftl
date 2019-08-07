<div class="col-xs-12" id="approveChart"></div>
<script type="text/javascript">
    $(function(){
        var $approveChart = $("#approveChart");
        $approveChart.highcharts({
            legend: {
                align: 'right',
                verticalAlign: 'middle',
                width: 100
            },
            credits:{
                enabled: true,
                href: "http://www.rextec.com.cn",
                text: "RexTec.com.cn"
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 200
            },
            title: {
                text: /*$("input[name=period]").val() + */' 考勤查询统计'
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
                    showInLegend: true,
                    events:{
                        click:function(event){

                            if(event.point.name != "正常上班人数"){
                                if(event.point.name == "考勤异常人数"){
                                    showWorkerCheckingIn(event.point.y,'b')
                                }else if(event.point.name == "迟到人数"){
                                    showWorkerCheckingIn(event.point.y,'c')
                                }else if(event.point.name == "早退人数"){
                                    showWorkerCheckingIn(event.point.y,'d')
                                }else if(event.point.name == "旷工人数"){
                                    showWorkerCheckingIn(event.point.y,'a')
                                }

                            }

                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: '占比',
                data: [
                    {
                        name: '考勤异常人数',
                        y: 2,
                        color: 'red'
                    },
                    {
                        name: '迟到人数',
                        sliced: true,
                        selected: true,
                        y: 3,
                        color: 'blue',
                    },
                    {
                        name: '早退人数',
                        y: 1,
                        color: 'brown'
                    },
                    {
                        name: '旷工人数',
                        y:2,
                        color: 'green'

                    },
                    {
                        name: '休息人数',
                        y:3,
                        color: 'gray'

                    },
                    {
                        name: '正常上班人数',
                        y:12,
                        color: 'yellow'
                    }
                ]
            }]
        });
    })
</script>