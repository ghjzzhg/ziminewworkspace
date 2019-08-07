<#if valueMap?has_content>
    <#assign totolPerson = valueMap.totolPerson?default(0)>
    <#assign abnormal = valueMap.abnormal.abnormal?default(0)>
    <#assign latePerson = valueMap.latePerson.latePerson?default(0)>
    <#assign earlyGetOff = valueMap.earlyGetOff.earlyGetOff?default(0)>
    <#assign absenteeism = valueMap.absenteeism.absenteeism?default(0)>
    <#assign rest = valueMap.rest?default(0)>
    <#assign leave = valueMap.leave?default(0)>
    <#assign normal = valueMap.normal?default(0)>
    <#assign date = valueMap.date>

    <#assign abnormalArr = valueMap.abnormal.abnormalArr?default('')>
    <#assign abnormalCause = valueMap.abnormal.abnormalCause?default('')>
    <#assign latePersonArr = valueMap.latePerson.latePersonArr?default('')>
    <#assign earlyGetOffArr = valueMap.earlyGetOff.earlyGetOffArr?default('')>
    <#assign absenteeismArr = valueMap.absenteeism.absenteeismArr?default('')>
<#else >
    <#assign totolPerson = 0>
    <#assign abnormal = 0>
    <#assign latePerson = 0>
    <#assign earlyGetOff = 0>
    <#assign absenteeism = 0>
    <#assign rest = 0>
    <#assign leave = 0>
    <#assign normal = 0>
    <#assign date = date>
    <#assign abnormalArr = ''>
    <#assign abnormalCause = ''>
    <#assign latePersonArr = ''>
    <#assign earlyGetOffArr = ''>
    <#assign absenteeismArr = ''>
</#if>
<div class="col-xs-12" id="checkingInStatisticsChart"></div>
<script type="text/javascript">
    $(function(){
        var $checkingInStatisticsChart = $("#checkingInStatisticsChart");
        $checkingInStatisticsChart.highcharts({
            legend: {
                align: 'bottom',
                verticalAlign: 'bottom',
                floating:false,
                maxHeight:200,
                margin:0,
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
                text: '${date?string('yyyy-MM-dd')}考勤统计'
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
                                    $.checkingIn.showStaffCheckingIn('${abnormalArr}','${date}',"WorkerCheckingB","考勤异常",'${abnormalCause}');
                                }else if(event.point.name == "迟到人数"){
                                    $.checkingIn.showStaffCheckingIn('${latePersonArr}','${date}',"WorkerCheckingC","迟到",'','1');
                                }else if(event.point.name == "早退人数"){
                                    $.checkingIn.showStaffCheckingIn('${earlyGetOffArr}','${date}',"WorkerCheckingC","早退",'','2');
                                }else if(event.point.name == "旷工人数"){
                                    $.checkingIn.showStaffCheckingIn('${absenteeismArr}','${date}',"WorkerCheckingA","旷工");
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
                        y: ${abnormal},
                        color: 'red'
                    },
                    {
                        name: '迟到人数',
                        sliced: true,
                        selected: true,
                        y: ${latePerson},
                        color: 'blue',
                    },
                    {
                        name: '早退人数',
                        y: ${earlyGetOff},
                        color: 'brown'
                    },
                    {
                        name: '旷工人数',
                        y:${absenteeism},
                        color: 'green'

                    },
                    {
                        name: '休息人数',
                        y:${rest},
                        color: 'gray'

                    },
                    {
                        name: '正常上班人数',
                        y:${normal},
                        color: 'yellow'
                    },
                    {
                        name: '请假人数',
                        y:${leave},
                        color: 'khaki'
                    }
                ]
            }]
        });
    });
</script>