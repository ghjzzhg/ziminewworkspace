<div id="pieChart" class="col-xs-12"></div>
<script type="text/javascript">
    $(function(){
        var $pieChart = $("#pieChart");
        $pieChart.highcharts({
            credits:{
                enabled: true,
                href: "http://www.rextec.com.cn",
                text: "RexTec.com.cn"
            },
            chart: {
                type:'column',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height:300
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -45,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '人数'
                },
                tickInterval: 1
            },
            title: {
                text: '岗位统计'
            },
            tooltip: {
                pointFormat: '<b>{point.y:1f}人</b>'
            },
            exporting:{
                enabled: false
            },
            plotOptions: {
                column: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true,
                    minPointLength: 3
                }
            },
            series: [{
                name: '人数统计',
                data: [
                        <#if occupations?has_content>
                            <#list occupations as occupation>
                                {
                                    name: unescapeHtmlText('${occupation.name}'),
                                    y: ${occupation.y},
                                    positionId: '${occupation.positionId}'
                                }
                                <#if occupation_has_next>
                                    ,
                                </#if>
                            </#list>
                        </#if>
                ],
                dataLabels: {
                    enabled: true,
                    rotation: -90,
                    color: '#FFFFFF',
                    align: 'right',
                    format: '{point.y:1f}', // one decimal
                    y: -20, // 10 pixels down from the top
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            }]
        });
    })
</script>