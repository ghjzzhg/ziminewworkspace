<div style="width: 1100px; overflow: auto">
<#list ['1','2','3','4','5','6','7','8','9','10','11','12'] as index>
    <div class="col-xs-3">
        <div
                <#if month == index && year == data.nowYear>
                        class="btn btn-default"
                </#if>
                <#if month != index || year != data.nowYear>
                        class="btn btn-info"
                </#if>>
            <#if data.salaryPayOffList?has_content>
                <div align="right">
                    <a href="#" onclick="showMonthSalaryInfo('${index}','${year}','${data.partyId?default("")}','${data.department?default("")}','${data.position?default("")}')">更多</a>
                </div>
            </#if>
            <#if data.numMonthMap?has_content>
                <div id="month_${index}" style="width: 170px">
                    <script type="text/javascript">
                        $(function(){
                            var $pieChart = $("#month_${index}");
                            $pieChart.css("width", "200px");
                            $pieChart.highcharts({
                                credits:{
                                    enabled: true,
                                    href: "http://www.rextec.com.cn",
                                    text: "RexTec.com.cn"
                                },
                                chart: {
                                    plotBackgroundColor: null,
                                    plotBorderWidth: null,
                                    plotShadow: true,
                                    height:240
                                },
                                title: {
                                    text:'${index}月薪资发放统计'
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
                                            name: '未发(${data.numMonthMap["notSend" + index]?default(0)})',
                                            y: ${data.numMonthMap["notSend" + index]?default(0)},
                                            color: 'red'},
                                        {
                                            name: '未通过(${(data.numMonthMap["disapprove" + index])?default(0)})',
                                            y: ${data.numMonthMap["disapprove" + index]?default(0)},
                                            color: 'purple'},
                                        {
                                            name: '未审(${(data.numMonthMap["notExamine" + index])?default(0)})',
                                            y: ${data.numMonthMap["notExamine" + index]?default(0)},
                                            color: 'blue'
                                        },
                                        {
                                            name: '已审(${(data.numMonthMap["approve" + index])?default(0)})',
                                            y: ${data.numMonthMap["approve" + index]?default(0)},
                                            sliced: true,
                                            selected: true,
                                            color: 'green'
                                        },
                                        {
                                            name: '已发(${(data.numMonthMap["sent" + index])?default(0)})',
                                            y: ${(data.numMonthMap["sent" + index])?default(0)},
                                            color: 'gray'
                                        }
                                    ]
                                }]
                            });
                        })
                    </script>
                </div>
            </#if>
        </div>
    </div>
</#list>
</div>
<script type="text/javascript">
    function showMonthSalaryInfo(month,year,partyId,department,position){
        displayInTab3("showMonthSalaryInfoTbl", "员工薪资", {requestUrl: "showMonthSalaryInfo",data:{month:month,year:year,partyId:partyId,department:department,position:position} , width: "800px"});
    }
</script>