<#if parameters.data.hasContent>
<script src="/images/lib/highcharts/js/highcharts.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function () {
        $('#containerLevel').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: '客户等级维度'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                name: '占比',
                colorByPoint: true,
                data: [
                    <#if parameters.data.levelMap?has_content>
                        <#outputformat "UNXML">
                            <#list parameters.data.levelMap.keySet() as myKey>
                                {
                                    name: '${myKey?default('未指定客户等级')}',
                                    y:${parameters.data.levelMap.get(myKey)}
                                },
                            </#list>
                        </#outputformat>
                    <#else>
                        {
                            name: '客户等级没有数据',
                            y: 0
                        },
                    </#if>]
            }]
        })
        ;
    })
    $(function () {
        $('#containerStatus').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: '客户状态维度'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                name: '占比',
                colorByPoint: true,
                data: [
                    <#if parameters.data.statusMap?has_content>
                        <#outputformat "UNXML">
                            <#list parameters.data.statusMap.keySet() as myKey>
                                {
                                    name: '${myKey?default('未指定客户状态')}',
                                    y:${parameters.data.statusMap.get(myKey)}
                                },
                            </#list>
                        </#outputformat>
                    <#else>
                        {
                            name: '客户状态没有数据',
                            y: 0
                        },
                    </#if>]
            }]
        });
        $("text[text-anchor=end]").hide();
    })
</script>

<div id="containerLevel" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<div id="containerStatus" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
<#else>
<div class="portlet light ">
    <div class="portlet-title" align="center">
        <div class="font-red sbold" style="font-size: larger">暂无客户信息</div>
    </div>
</div>
</#if>

