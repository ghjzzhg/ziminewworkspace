<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>

<form method="post" action="" id="SearchInventoryInfo" class="basic-form" name="SearchInventoryInfo">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>仓库名称</label>
            </td>
            <td>
                <input type="text" name="warehouseName">
            </td>
            <td class="label">
                <label>联系人</label>
            </td>
            <td>
            <@chooser name="man" chooserType="LookupEmployee" required=false/>
            </td>
        </tr>
        <tr>
            <td >
                <a href="#" class="smallSubmit" onclick="$.inventory.searchWarehouseInfo()">查询</a>
            </td>
            <td >
                <a href="#" class="smallSubmit" onclick="$.inventory.createWarehouseInfo()">添加</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
