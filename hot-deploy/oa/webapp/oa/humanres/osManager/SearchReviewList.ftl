<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>

<form method="post" action="" id="SearchReviewList" class="basic-form" name="SearchReviewList">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>仓库名称</label>
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
            <td class="label">
                <label>领用类型</label>
            </td>
            <td>
                <select name="outInventoryType" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if data.outInventoryTypeList?has_content>
                    <#list data.outInventoryTypeList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label>领用部门</label>
            </td>
            <td>
            <@chooser name="receiveDepartment" chooserType="LookupDepartment" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>状态</label>
            </td>
            <td>
                <select name="checkResult" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if data.receiveResultList?has_content>
                    <#list data.receiveResultList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label>领用人</label>
            </td>
            <td>
            <@chooser name="receivePerson" chooserType="LookupEmployee" required=false/>
            </td>
        </tr>
        <tr>
            <td >
                <a href="#" class="smallSubmit" onclick="$.InventoryManagement.searchUncheckReveice()">查询</a>
            </td>
            <td >
                <a href="#" class="smallSubmit" onclick="$.InventoryManagement.addReceiveForm()">添加新的领用单</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
