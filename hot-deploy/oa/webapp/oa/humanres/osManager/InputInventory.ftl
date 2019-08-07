<#include "component://widget/templates/dropDownList.ftl"/>


<form method="post" action="" id="EditInputInventory" class="basic-form" name="EditInputInventory">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>所属仓库</label>
            </td>
            <td class="jqv">
               <label>${data.list.warehouseName?default("")}</label>
                <input type="hidden" name="osManagementId" value="${data.list.osManagementId?default('')}">
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
                <label><b class="requiredAsterisk">*</b>存储货位</label>
            </td>
            <td class="jqv">
                <select class="validate[required]" name="storageLocation" id="storageLocation">
                    <option value="">--请选择--</option>
                <#if data.locationList?has_content && data.list?has_content>
                    <#list data.locationList as list>
                        <#if list.locationId?has_content && data.list.storageLocation?has_content  && (data.list.storageLocation?default("")) == (list.locationId?default(""))>
                            <option value="${list.locationId?default('')}" selected >${list.locationName?default("")}</option>
                        <#else>
                            <option value="${list.locationId?default('')}">${list.locationName?default("")}</option>
                        </#if>
                    </#list>
                <#elseif data.locationList?has_content>
                    <#list data.locationList as list>
                        <option value="${list.locationId?default('')}">${list.locationName?default("")}</option>
                    </#list>
                </#if>
                </select>
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
                <label>上次入库单价</label>
            </td>
            <td class="jqv">
                <label>${data.list.inputInventoryPrice?default("")}</label>&nbsp;&nbsp;&nbsp;元
                <input type="hidden" name="inputInventoryId" value="${data.list.inputInventoryId?default('')}">
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
                <label><b class="requiredAsterisk">*</b>本次入库类型</label>
            </td>
            <td class="jqv">
                <select name="inputInventoryType" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if data.inputInventoryTypeList?has_content>
                    <#list data.inputInventoryTypeList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label" >　
                <label><b class="requiredAsterisk">*</b>本次入库数量</label>
            </td>
            <td class="jqv">
                <input type="text" name="inputInventoryAmount" class="validate[required,custom[onlyNumberSp]]" value="">${data.list.unitName?default("")}
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>本次入库单价</label>
            </td>
            <td class="jqv">
                <input type="text" name="inputInventoryPrice" value="">&nbsp;&nbsp;&nbsp;元
            </td>
            <td class="label" >　
                <label>入库说明</label>
            </td>
            <td class="jqv">
                <textarea name="inputInventoryNote" maxlength="50"></textarea>
            </td>
        </tr>
        <tr align="center">
            <td colspan="4">
                <a name="submit" href="#" class="smallSubmit" onclick="$.InventoryManagement.saveInputInventory()">
                    <label>提交</label>
                </a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
