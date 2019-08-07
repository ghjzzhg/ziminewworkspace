<#include "component://widget/templates/dropDownList.ftl"/>

<form method="post" action="" id="SearchInventory" class="basic-form" name="SearchInventory">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>易耗品名称</label>
            </td>
            <td>
                <input type="text" name="suppliesName">
                <input type="hidden" name="warehouseId" value="${data.warehouseId?default('')}">
            </td>
            <td class="label">
                <label>货品类别</label>
            </td>
            <td>
                <select name="productTypeId">
                <option value="">--请选择--</option>
            <#if data.productTypeList?has_content>
                <#list data.productTypeList as list>
                    <option value="${list.productTypeId?default("")}">${list.typeName?default("")}</option>
                </#list>
            </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td >
                <a href="#" class="smallSubmit" onclick="$.InventoryManagement.searchReveiveInventoryList()">查询</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
