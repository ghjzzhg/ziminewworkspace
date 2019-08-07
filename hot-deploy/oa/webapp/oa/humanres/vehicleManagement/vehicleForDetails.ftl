<#macro vehicleForDetailsList vehicleForDetailsList>
<div>
    <div>
        <table class="basic-table">
            <tr class="header-row-2">
                <td>费用类型</td>
                <td>金额</td>
                <td>备注</td>
                <td>发生日期</td>
                <td width="10%"><label for="editLink" class="hide">修改</label></td>
                <#--<td width="5%"><label for="trashLink" class="hide">删除</label></td>-->
            </tr>
            <#if vehicleForDetailsList?has_content>
                <#list vehicleForDetailsList as list>
                    <tr>
                        <#assign reviewTheStatus = delegator.findOne("Enumeration",{"enumId":list.costType},false)>
                        <td>${reviewTheStatus.description}</td>
                        <td id="vehicle_cost_${list.costId}">${list.cost?default('')}</td>
                        <td id="vehicle_remarks_${list.costId}">${list.remarks?default('')}</td>
                        <td>${list.happenDate?default('')}</td>
                        <td>
                            <a class="icon-edit" href="#nowhere" onclick="toEdit(${list.costId},$(this))" title="修改"></a>&nbsp;&nbsp;&nbsp;
                            <a class="icon-trash" href="#nowhere" onclick="$.vehicleManagement.deleteVehicleByCostId(${list.costId})" title="删除"></a>
                        </td>
                    </tr>
                </#list>
            </#if>
            <script type="text/javascript">
                function toEdit(id,obj){
                    var costObj = $("#vehicle_cost_"+id);
                    var remarksObj = $("#vehicle_remarks_"+id);
                    var text1 = $(costObj).text();
                    $(costObj).html('<input id="cost_'+id+'" value="'+ text1 +'"/>');
                    var text2 = $(remarksObj).text();
                    $(remarksObj).html('<input id="remarks_'+id+'" value="'+ text2 +'"/>');
                    $(obj).parent().html('<a href="#" onclick="$.vehicleManagement.saveVehicleByCostId(\''+ id +'\')" class="smallSubmit">保存</a>');
                }
            </script>
        </table>
    </div>
</div>
</#macro>


