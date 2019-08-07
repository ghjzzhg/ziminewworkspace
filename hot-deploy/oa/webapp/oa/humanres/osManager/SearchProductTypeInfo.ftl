<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>

<form method="post" action="" id="SearchProductTypeInfo" class="basic-form" name="SearchProductTypeInfo">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>类别名称</label>
            </td>
            <td>
                <input type="text" name="typeName" value="">
            </td>
            <td class="label">
                <label>录入人</label>
            </td>
            <td>
            <@chooser name="inputPerson" chooserType="LookupEmployee" required=false/>
            </td>
            <td class="label">
                <label>所属仓库</label>
            </td>
            <td>
                <select name="warehouseId" >
                    <option value="">--请选择--</option>
                <#if data.warehouseList?has_content>
                    <#list data.warehouseList as list>
                        <option value="${list.warehouseId?default("")}">${list.warehouseName?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td >
                <a href="#" class="smallSubmit" onclick="$.inventory.searchProductTypeInfo()">查询</a>
            </td>
            <td >
                <a href="#" class="smallSubmit" onclick="$.inventory.createProductType()">添加</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
