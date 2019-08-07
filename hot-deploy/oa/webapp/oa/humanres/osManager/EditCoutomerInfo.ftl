<#include "component://widget/templates/dropDownList.ftl"/>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="note"]', {
            allowFileManager: true
        });
    });
</script>

<form method="post" action="" id="EditCustomer" class="basic-form" name="EditCustomer">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>客户名称</label>
            </td>
            <td class="jqv">
                <#if data.list?has_content>
                    <input type="text" name="customerName" class="validate[required,custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" maxlength="20" value="${data.list.customerName?default('')}">
                <#else>
                    <input type="text" name="customerName" class="validate[required,custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" maxlength="20" value="">
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>客户类型</label>
            </td>
            <td class="jqv">
                <select class="validate[required]" name="customerType" data-prompt-position="centerRight">
                    <option value="">--请选择--</option>
                <#if data.typeList?has_content && data.list?has_content>
                    <#list data.typeList as list>
                        <#if list.enumId?has_content && data.list.customerType?has_content  && (data.list.customerType?default("")) == (list.enumId?default(""))>
                            <option value="${list.enumId?default("")}" selected >${list.description?default("")}</option>
                        <#else>
                            <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                        </#if>
                    </#list>
                <#elseif data.typeList?has_content>
                    <#list data.typeList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>
                </select>
                <#--<input type="text" name="customerType" value="">-->
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>联系人</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <input type="text" name="linkman" class="validate[custom[onlyLetterNumberChinese]]" maxlength="20" value="${data.list.linkman?default('')}">
            <#else>
                <input type="text" name="linkman" class="validate[custom[onlyLetterNumberChinese]]" maxlength="20" value="">
            </#if>
            </td>
            <td class="label">
                <label>所在地址</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <input type="text" name="address" class="validate[custom[onlyLetterNumberChinese]]" maxlength="50" value="${data.list.address?default('')}">
            <#else>
                <input type="text" name="address" class="validate[custom[onlyLetterNumberChinese]]" maxlength="50" value="">
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>联系电话</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <input type="text" name="telephone" class="validate[custom[isPhone]]" maxlength="20" value="${data.list.telephone?default('')}">
            <#else>
                <input type="text" name="telephone" class="validate[custom[isPhone]]" maxlength="20" value="">
            </#if>
            </td>
            <td class="label">
                <label>联系传真</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <input type="text" name="linkFax" value="${data.list.linkFax?default('')}">
            <#else>
                <input type="text" name="linkFax" value="">
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>联系手机</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <input type="text" name="phone" class="validate[required,custom[isMobile]]" maxlength="20" value="${data.list.phone?default('')}">
            <#else>
                <input type="text" name="phone" class="validate[required,custom[isMobile]]" maxlength="20" value="">
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label" >　
                <label>备注：</label>
            </td>
            <td colspan="2"></td>
        </tr>
        <tr>
            <td colspan="4" class="jqv">
                <#if data.list?has_content>
                    <textarea name="note">${data.list.note?default('')}</textarea>
                <#else>
                    <textarea name="note"></textarea>
                </#if>
            </td>
        </tr>

        <tr align="center">
            <td colspan="4">
                <#if data.list?has_content>
                    <input type="hidden" name="customerInfoId" value="${data.list.customerInfoId?default('')}">
                </#if>
                <a href="#" class="smallSubmit" onclick="$.customer.saveCustomer()">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
