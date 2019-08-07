<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
    $(function () {
        $("input[name='warehouseName']").data("promptPosition", "bottomLeft");
    })
</script>
<form method="post" action="" id="warehouseInfo" class="basic-form" name="warehouseInfo">
    <table cellspacing="0" class="basic-table">
        <tbody>
    <#if data.list?has_content>
    <#list data.list as line>
        <tr>
            <td class="label">
                <label>仓库编号</label>  </td>
            <td class="jqv">
                <label>${line.warehouseCode?default('')}</label>
                <input type="hidden" name="warehouseCode" value="${line.warehouseCode?default('')}">
                <input type="hidden" name="warehouseId" value="${line.warehouseId?default('')}">
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>仓库名称</label>
            </td>
            <td class="jqv">
               <input type="text" name="warehouseName" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" value="${line.warehouseName?default('')}">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>仓库地址</label>
            </td>
            <td class="jqv">
                <input type="text" name="warehouseAddress" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" value="${line.warehouseAddress?default('')}">
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>联系人</label>
            </td>
            <td class="jqv">
                <@chooser name="linkman" chooserType="LookupEmployee" chooserValue="${line.linkman?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>联系电话</label>
            </td>
            <td class="jqv">
                <input type="text" name="phone" class="validate[required,custom[isMobile]]" value="${line.phone?default('')}">
            </td>
            <td class="label">
            </td>
            <td>
                <a class="smallSubmit" onclick="$.inventory.addLocation()">添加货位</a>
            </td>
        </tr>
    <div class="screenlet-body">
        <div id="ListSubOrgs">
            <script>
                function removeTr(rows,id){
                    $("#tr_"+rows).remove();
                    $.ajax({
                        type: 'post',
                        url: "deleteLocation",
                        async: true,
                        data:{locationId: id},
                        dataType: 'json',
                        success:function (data) {
                            showInfo(data.msg);
                        },
                    });
                }
                function removeTr(rows){
                    $("#tr_"+rows).remove();
                }
            </script>
            <table cellspacing="0" id="addLocationTable" class="basic-table hover-bar">
                <tbody>
                <tr class="header-row-2">
                    <td>
                        <label>货位名称</label>
                    </td>
                    <td>
                        <label>删除</label>
                    </td>
                </tr>
        <#if data.locations?has_content>
            <#assign rows=1>
            <#list data.locations as line1>
                <tr id="tr_${rows}">
                    <td>
                        <input type="text" name="locationName" value="${line1.locationName?default('')}">
                        <input type="hidden" name="locationId" value="${line1.locationId?default('')}">
                    </td>
                    <td>
                        <a name="removeTr" href="#" onclick="removeTr(${rows},${line1.locationId?default('')})" title="移除" class="icon-trash">
                    </td>
                </tr>
            <#assign rows=rows+1>
            </#list>
        </#if>
                </tbody>
            </table>
        </div>
    </div>
        <tr>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="$.inventory.saveWarehouseInfo(${line.warehouseId?default('')})" title="保存">保存</a>
            </td>
        </tr>
        </#list>
    </#if>
        </tbody>
    </table>
</form>