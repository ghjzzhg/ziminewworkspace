<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    function changeDivBackgroundColorForTime(id) {
        var d = document.getElementById(id);
        d.style.backgroundColor = "#FF7F24";
    }
    function changeDivBackgroundColorForThrough(orderId,id, start, end) {
        var j = parseInt(end);
        var i = parseInt(start);
        for (i; i <= j; i++) {
            var idVar = id + i;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#CDCD00";
            d.onclick = function(){
                javascript:$.vehicleManagement.vehicleOrderView(orderId);
            }
        }
    }
    function changeDivBackgroundColorForArrange(orderId,id, start, end) {
        var j = parseInt(end);
        var i = parseInt(start);
        for (i; i <= j; i++) {
            var idVar = id + i;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#66CD00";
            d.onclick = function(){
                javascript:$.vehicleManagement.vehicleOrderView(orderId);
            }
        }
    }
    function changeDivBackgroundColor(orderId,id, start, end) {
        var j = parseInt(end);
        var i = parseInt(start);
        for (i; i <= j; i++) {
            var idVar = id + i;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#FF0000";
            d.onclick = function(){
                javascript:$.vehicleManagement.vehicleOrderView(orderId);
            }
        }
    }
    $(function () {
    <#list occupyTime as timeList>
        changeDivBackgroundColor('${timeList.orderId}','vehicle1_${timeList.vehicleId}_', '${timeList.startDate}', '${timeList.endDate}');
    </#list>
    <#list occupyTimeForThrough as timeListForThrough>
        changeDivBackgroundColorForThrough('${timeListForThrough.orderId}','vehicle1_${timeListForThrough.vehicleId}_', '${timeListForThrough.startDate}', '${timeListForThrough.endDate}');
    </#list>
    <#list occupyTimeForArrange as timeListForArrange>
        changeDivBackgroundColorForArrange('${timeListForArrange.orderId}','vehicle1_${timeListForArrange.vehicleId}_', '${timeListForArrange.startDate}', '${timeListForArrange.endDate}');
    </#list>
    })
</script>
<#assign extInfo = parameters.extInfo?default("N")>
<form name="vehicleList">
    <table border="5" width="100%" style="background-color: #FFFFFF;border-color: #FF0000 ">
        <tr style="height: 45px">
            <td style=" font-size: 15px;width: 100px;white-space: nowrap;font-weight:bold">车辆名称</td>
            <td style="font-size: 15px;white-space: nowrap;font-weight:bold">车辆占用图例</td>
        </tr>
    <#assign time=0>
    <#list vehicleShow as list>
        <tr style="height: 40px">
            <td>${list.vehicleName?default("")}</td>
            <td>
                <div>
                    <div>
                        <#list 0..48 as divList>
                            <div id="vehicle1_${list.vehicleId}_${divList}"
                                 style="background: rgba(0, 0, 0, 0.22);float:left;margin-right: 0px;padding: 0px;border: 0px;width: 15px;height: 15px;vertical-align: top">
                                <#if divList==15>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">08:00</div>
                                </#if>
                                <#if divList==0>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">00:00</div>
                                </#if>
                                <#if divList==48>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">24:00</div>
                                </#if>
                                <#if divList==24>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">12:00</div>
                                </#if>
                                <#if divList==33>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">17:00</div>
                                </#if>
                            </div>
                        </#list>
                    </div>
                    </br>
                    <div style="width:800px;">
                        <#list 0..48 as divList>
                            <div id="vehicle1_time_${list.vehicleId}_${divList}"
                                 style="background: rgba(0, 0, 0, 0.51);float:left;margin-right: 0px;padding: 0px;border: 0px;width: 15px;height: 2px;vertical-align: top">

                            </div>
                        </#list>
                    </div>
                </div>
            </td>
        </tr>
        <#list 16..23 as changeDivList>
            <script>
                changeDivBackgroundColorForTime('vehicle1_time_${list.vehicleId}_${changeDivList}');
            </script>
        </#list>
        <#list 26..33 as changeDivList>
            <script>
                changeDivBackgroundColorForTime('vehicle1_time_${list.vehicleId}_${changeDivList}');
            </script>
        </#list>
    </#list>
    </table>
</form>
<!-- end findEmployees.ftl -->
