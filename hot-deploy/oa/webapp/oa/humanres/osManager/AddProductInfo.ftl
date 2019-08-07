<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>

<script>
    function removeTr(id){
        $("#tr_"+id).remove();
    }
</script>
<form method="post" action="" id="addOutInventoryBarList" class="basic-form" name="addOutInventoryBarList">
<table cellspacing="0" id="addOutInventoryTable" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>货品编码</label>
        </td>
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
</form>

