<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>


<form method="post" action="" id="EditOutInventory" class="basic-form" name="EditOutInventory">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>所属仓库</label>
            </td>
            <td class="jqv">
                <label>${data.list.warehouseName?default("")}</label>
                <input type="hidden" name="osManagementId" value="${data.list.osManagementId?default('')}">
                <input type="hidden" name="outInventoryId" value="${data.list.outInventoryId?default('')}">
            </td>
            <td class="label">
                <label>所属类别</label>
            </td>
            <td class="jqv">
                <label>${data.list.typeName?default("")}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>存储地点</label>
            </td>
            <td class="jqv">
                <label>${data.list.storageLocationName?default('')}</label>
            </td>
            <td class="label">
                <label>货品编码</label>
            </td>
            <td class="jqv">
                <label>${data.list.warehouseName?default("")}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>货品名称</label>
            </td>
            <td class="jqv">
                <label>${data.list.suppliesName?default("")}</label>
            </td>
            <td class="label">
                <label>规格</label>
            </td>
            <td class="jqv">
                <label>${data.list.standardName?default("")}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>最大库存</label>
            </td>
            <td class="jqv">
                <label>${data.list.MaximumSafety?default("")}</label>
            </td>
            <td class="label" >　
                <label>最小库存</label>
            </td>
            <td class="jqv">
                <label>${data.list.MinimumSafety?default("")}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>单位</label>
            </td>
            <td class="jqv">
                <label>${data.list.unitName?default("")}</label>
            </td>
            <td class="label" >　
                <label>基本单价</label>
            </td>
            <td class="jqv">
                <label>${data.list.price?default("")}</label>&nbsp;&nbsp;&nbsp;元
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>上次出库库单价</label>
            </td>
            <td class="jqv">
                <label>${data.list.outInventoryPrice?default('')}</label>&nbsp;&nbsp;&nbsp;元
            </td>
            <td class="label" >　
                <label>库存数量</label>
            </td>
            <td class="jqv">
                <label>${data.list.repertoryAmount?default("")}</label>&nbsp;&nbsp;&nbsp;${data.list.unitName?default("")}
                <input type="hidden" name="repertoryAmount" value="${data.list.repertoryAmount?default('')}">
            </td>
        </tr>
        <tr>
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
            <td class="label" >　
                <label><b class="requiredAsterisk">*</b>收货部门</label>
            </td>
            <td class="jqv">
                 <@chooser name="receiveDepartment" chooserType="LookupDepartment"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>收货人</label>
            </td>
            <td class="jqv">
                <@chooser name="receivePerson" chooserType="LookupEmployee"/>
            </td>
            <td class="label" >　
                <label><b class="requiredAsterisk">*</b>出库数量</label>
            </td>
            <td class="jqv">
                <input type="text" name="outInventoryAmount" value="">${data.list.unitName?default("")}
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>出库货品单价</label>
            </td>
            <td class="jqv">
                <input type="text" name="outInventoryPrice" value="">&nbsp;&nbsp;&nbsp;元
            </td>
            <td class="label" >　
                <label><b class="requiredAsterisk">*</b>出库日期</label>
            </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="outInventoryData" event="" action="" className="validate[required,past[completeTime]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
            value="${startTime?default('')}" size="25" maxlength="30" id="WorkPlan_startTime" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>出库说明</label>
            </td>
            <td>
                <textarea name="outInventoryNote" maxlength="50"></textarea>
            </td>
        </tr>
        <tr align="center">
            <td colspan="4">
                <a name="submit" href="#" class="smallSubmit" onclick="$.InventoryManagement.saveOutInventory()">
                    <label>提交</label>
                </a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
