<#include "component://widget/templates/chooser.ftl"/>

<form method="post" action="" id="warehouseInfo" class="basic-form" name="warehouseInfo">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label>仓库编号</label>  </td>
            <td class="jqv">
                <label>${parameters.data}</label>
                <input type="hidden" name="warehouseCode" value="${parameters.data}">
            </td>
            <td class="label">
                <label><b style="color: red">*</b>仓库名称</label>
            </td>
            <td class="jqv">
               <input type="text" name="warehouseName" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" value="" data-prompt-position="centerRight">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b style="color: red">*</b>仓库地址</label>
            </td>
            <td class="jqv">
                <input type="text" name="warehouseAddress" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" value="">
            </td>
            <td class="label">
                <label><b style="color: red">*</b>联系人</label>
            </td>
            <td class="jqv">
            <@chooser name="linkman" chooserType="LookupEmployee"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b style="color: red">*</b>联系电话</label>
            </td>
            <td class="jqv">
                <input type="text" name="phone" class="validate[required,custom[isMobile]]" value="">
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
                    function removeTr(id){
                        $("#tr_"+id).remove();
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
                    </tbody>
                </table>
            </div>
        </div>
        <tr>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="$.inventory.saveWarehouseInfo('')" title="保存">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>