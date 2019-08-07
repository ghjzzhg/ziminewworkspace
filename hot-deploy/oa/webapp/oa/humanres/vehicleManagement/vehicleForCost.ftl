<#assign extInfo = parameters.extInfo?default("N")>
<form name="vehicleForCostList">
    <div style="height:200px; overflow:scroll;">
        <table class="basic-table hover-bar" cellspacing="1" cellpadding="3" width="900px" style="margin-top: 5px">
            <tr class="header-row-2" style="height: 30px; width: 30px">
                <td style="font-size: 10px;height: 30px; width:100px">车辆名称</td>
            <#assign number=1>
            <#assign zeor=0>
            <#list dateList as dateList>

                <td style="font-size: 10px;text-align:center;"><#if number<=9>${zeor}</#if>
                    ${number}</br>${dateList}
                </td>

                <#assign number=number+1>
            </#list>
            </tr>
        <#assign alt_row = false>
        <#list vehicle as vehicleList>
            <#assign number=1>
            <tr <#if alt_row> class="alternate-row"</#if> style="height: 30px" >
                <td >
                    <a href="#nowhere" onclick="$.vehicleManagement.showVehicle(${vehicleList.vehicleId},${year},${month})"> ${vehicleList.vehicleName?default("") }</a>
                </td>
                <#list dateList as list>
                    <td style="font-size: 10px;">
                        <#list sumCostList as sumCostList>
                            <#if sumCostList.vehicleId == vehicleList.vehicleId&&sumCostList.happenDateNumber == number >
                        ${sumCostList.cost}
                        <#else>
                        </#if>
                        </#list>
                    </td>
                    <#assign number=number+1>
                </#list>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
        </table>
    </div>
    <div style="height:200px; overflow:scroll;">
    <table class="basic-table hover-bar" cellspacing="1" cellpadding="3" width="900px" style="margin-top: 15px;">
        <tr class="header-row-2" style="height: 30px; width: 30px">
            <td style="font-size: 10px;height: 30px; width: 100px">${year}年</td>
        <#assign number=1>
        <#list 1..13 as dateList>
        <#if  dateList!=13>
            <td style="font-size: 10px;text-align:center">${dateList}月</td>
        </#if>
        <#if dateList==13>
            <td style="font-size: 10px;height: 30px; width: 60px;text-align:center">小计</td>
        </#if>
            <#assign number=number+1>
        </#list>
        </tr>
        <#assign alt_row = false>
        <#list vehicle as list>
            <tr <#if alt_row> class="alternate-row"</#if> style="height: 30px" >
                <td>
               ${list.vehicleName?default("")}
                </td>
                <#assign numberFor=1>
                <#list 1..13 as listForNumber>
                    <td style="font-size: 10px;">
                        <#list sumCostListForYear as sumCostListFor>
                        <#if sumCostListFor.vehicleId == list.vehicleId&&sumCostListFor.happenMonthNumber == listForNumber >
                        ${sumCostListFor.cost}
                        <#else>
                        </#if>
                        </#list>
                        <#list sumCostListForYearByVehicle as sumCostListForYearByVehicle>
                        <#if sumCostListForYearByVehicle.vehicleId == list.vehicleId&&listForNumber==13>
                        ${sumCostListForYearByVehicle.cost}
                        <#else>
                        </#if>
                    </#list>
                    </td>
                    <#assign numberFor=numberFor+1>
                </#list>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
        <tr class="alternate-row"  style="height: 30px">
            <td>
            费用合计
            </td>
        <#list 1..13 as listSum>
            <td style="font-size: 10px;">
                <#list sumCostListForYearByMonth as sumCostListForYearByMonth>
                    <#if sumCostListForYearByMonth.happenMonthNumber == listSum>
                ${sumCostListForYearByMonth.cost}
                <#else>
                </#if>
                </#list>
            </td>
        </#list>
        </tr>
    </table>
    </div>
</form>
<div style="font-size: 10px;margin-top: 20px"></div>
