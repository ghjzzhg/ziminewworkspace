<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script>
    function changedCheck(){
        if($("input[id='isTrue']").attr("checked")=="checked"){
            $("input[name='isCheck']").val("Y")
        } else if($("input[id='isFalse']").attr("checked")=="checked"){
            $("input[name='isCheck']").val("N")
        }
    }
    function addOutInventoryBar(){
        var rows = $("#addOutInventoryTable tr").length;
        var htmlStr = "<tr id='tr_"+rows+"'>" +
                "<td><input type='text' id='warehouseName_"+rows+"' readonly onclick='" + 'findOutInventory("货品选择器","InventoryInfo","chooser","'+rows+'")' + "'></td>"+
                "<td><label id='standardName_"+rows+"'></label></td>"+
                "<td><label id='typeName_"+rows+"'></label></td>"+
                "<td><label id='unitName_"+rows+"'></label></td>"+
                "<td><label id='canUseAmount_"+rows+"'></label></td>"+
                "<td><label id='repertoryAmount_"+rows+"'></label></td>"+
                "<td><input type='text' name='number' id='number_"+rows+"' class='validate[required,custom[numberWithoutDecimal]]' onblur='onMove("+rows+")' maxlength='9'></td>"+
                "<td> <input type='hidden' id='osManagement_"+rows+"' name='osManagementId'>"+
                "<a name='removeTr' href='#' onclick='removeTr("+rows+")' title='移除' class='icon-trash'></td>"+
                "</tr>";
        var $tr = $("#addOutInventoryTable tr:last");
        $tr.after(htmlStr);
    }

    function onMove(id){
        var canUseAmount = $("#canUseAmount_"+id).html();
        var number = $("#number_"+id).val();
        if(canUseAmount-number<0){
            showInfo("本次领用数量不可以大于可用数量");
        }
    }

    function findOutInventory(strTab,chooserType,name,id){
        var warehouseId = $("#warehouseId").val();
        if(warehouseId){
            displayInTab3("secondaryTbl", strTab, {requestUrl: chooserType,data:{name:name,id:id,warehouseId:warehouseId}, width: "800px"});
        } else {
            showInfo("请先选择仓库");
        }
    }

</script>

<form method="post" action="" id="EditReviewList" class="basic-form" name="EditReviewList">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>仓库名称</label>
            </td>
            <td class="jqv">
                <select name="warehouseId" id="warehouseId" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if data.warehouseList?has_content>
                    <#list data.warehouseList as list>
                        <option value="${list.warehouseId?default("")}">${list.warehouseName?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>出库类型</label>
            </td>
            <td class="jqv">
                <select name="outInventoryType" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if data.outInventoryTypeList?has_content>
                    <#list data.outInventoryTypeList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>预计领用日期</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="receiveDate" event="" action="" className="validate[required,past[endTime:yyyy-MM-dd]]" alert="" title="Format: yyyy-MM-dd"
            value="" size="25" maxlength="30" id="WorkReport_c" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                <script language="JavaScript"
                        type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>领用部门</label>
            </td>
            <td>
                <@chooser name="receiveDepartmentId" chooserType="LookupDepartment"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>领用人</label>
            </td>
            <td>
                <@chooser name="receivePersonId" chooserType="LookupEmployee"/>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>制单人</label>
            </td>
            <td>
                <@chooser name="makeInfoPersonId" chooserType="LookupEmployee"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>是否需要审核</label>
            </td>
            <td>
                <input type="radio" name="radio" id="isTrue" checked onclick="changedCheck()">是
                <input type="radio" name="radio" id="isFalse" onclick="changedCheck()">否
                <#--<input type="checkbox" name="isChecked" onclick="changedCheck()">-->
                <input type="hidden" name="isCheck" value="Y">
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>审核岗位</label>
            </td>
            <td>
                 <@chooser name="checkJob" chooserType="LookupOccupation"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>领用说明</label>
            </td>
            <td colspan="2">
                <textarea name="receiveNote"></textarea>
            </td>
            <td>
                <a class="smallSubmit" onclick="addOutInventoryBar()">新增货品明细</a>
            </td>
        </tr>
        <div class="screenlet-body">
            <div id="ListSubOrgs">
                <script>
                    function removeTr(id){
                        $("#tr_"+id).remove();
                    }
                </script>
                    <table cellspacing="0" id="addOutInventoryTable" class="basic-table hover-bar">
                        <tbody>
                        <tr class="header-row-2">
                            <td>
                                <label>货品名称</label>
                            </td>
                            <td>
                                <label>规格</label>
                            </td>
                            <td>
                                <label>类别</label>
                            </td>
                            <td>
                                <label>单位</label>
                            </td>
                            <td>
                                <label>可用数量</label>
                            </td>
                            <td>
                                <label>库存数量</label>
                            </td>
                            <td>
                                <label>本次领用数量</label>
                            </td>
                            <td>
                            </td>
                        </tr>
                        </tbody>
                    </table>
            </div>
        </div>
        <tr>
            <td >
                <a name="add" class="smallSubmit" href="#" onclick="$.InventoryManagement.saveReceive()">提交</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
