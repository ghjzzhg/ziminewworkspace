<#include "component://widget/templates/dropDownList.ftl"/>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="note"]', {
            allowFileManager: true
        });
    });
</script>
<form method="post" action="" id="productType" class="basic-form" name="productType">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <#if data.list?has_content>
            <#list data.list as line>
        <tr>
            <td class="label">
                <label>货品类别编号</label>  </td>
            <td class="jqv">
                <label>${line.productTypeCode?default('')}</label>
                <input type="hidden" name="productTypeCode" value="${line.productTypeCode?default('')}">
                <input type="hidden" name="productTypeId" value="${line.productTypeId?default('')}">
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>货品类别名称</label>
            </td>
            <td class="jqv">
                <input type="text" name="typeName" class="validate[required,custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" maxlength="20" value="${line.typeName?default('')}">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>最大上限</label>
            </td>
            <td class="jqv">
                <input type="text" name="MaximumSafety" class="validate[required,custom[onlyNumberSp]]" maxlength="10" value="${line.MaximumSafety?default('')}">
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>最小下限</label>
            </td>
            <td class="jqv">
                <input type="text" name="MinimumSafety" class="validate[required,custom[onlyNumberSp]]" maxlength="10" value="${line.MinimumSafety?default('')}">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>所属仓库</label>
            </td>
            <td class="jqv">
                <select id="BumphNotice_useTemplate" name="warehouseId" >
                    <option value="">--请选择--</option>
                    <#if data.warehouseList?has_content>
                        <#list data.warehouseList as list>
                            <#if list.warehouseId?has_content && line.warehouseId?has_content  && line.warehouseId == list.warehouseId>
                                <option value="${list.warehouseId}" selected >${list.warehouseName}</option>
                            <#else>
                                <option value="${list.warehouseId}">${list.warehouseName}</option>
                            </#if>
                        </#list>
                    </#if>
                </select>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>单位</label>
            </td>
            <td class="jqv">
            <@dropDownList dropDownValueList=data.unitList enumTypeId="PRODUCT_UNIT" name="unit"  selectedValue="${line.unit?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label" >　
                <label><b class="requiredAsterisk">*</b>备注：</label>
            </td>
            <td colspan="2"></td>
        </tr>
        <tr>
            <td colspan="4" class="jqv">
                <textarea name="note" class="validate[required]">${line.note?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="$.inventory.saveProductType()" title="提交">提交</a>
            </td>
        </tr>
        </#list>
        </#if>
        </tbody>
    </table>
</form>