<#include "component://widget/templates/dropDownList.ftl"/>

<script type="text/javascript">
    var resourceExplain;
    $(function () {
        resourceExplain = KindEditor.create('textarea[name="resourceExplain"]', {
            allowFileManager: true
        });
        $("input[name='resourceName']").data("promptPosition", "bottomLeft");
    });
</script>
<form method="post" action="" id="createResourceForm" class="basic-form" name="createResourceForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>资源名称</label>
            </td>
            <td class="jqv">
                <#if resourceMap?has_content>
                    <input type="text" name="resourceName" class="validate[required,maxSize[20],custom[onlyLetterNumberChinese]]" value="${resourceMap.resourceName?default('')}">
                    <input type="hidden" name="resourceId" value="${resourceMap.resourceId?default('')}">
                <#else>
                        <input type="text" name="resourceName" class="validate[required,maxSize[20],custom[onlyLetterNumberChinese]]">
                        <input type="hidden" name="resourceId">
                </#if>

            </td>
        </tr>
        <tr>
            <td class="label">
                <label>资源说明</label>
            </td>
            <td colspan="3">
                <#if resourceMap?has_content>
                    <textarea name="resourceExplain">${resourceMap.resourceExplain?default("")}</textarea>
                <#else>
                    <textarea name="resourceExplain"></textarea>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>资产使用状态</label>
            </td>
            <td>
                <select name="resourceUseState" >
                    <option value="">--请选择--</option>
                <#if userSate?has_content>
                    <#list userSate as list>
                        <#if resourceMap?has_content>
                            <#if (list.enumId?default('')) == (resourceMap.resourceUseState?default(''))>
                                <option value="${list.enumId?default("")}" selected>${list.description?default("")}</option>
                            <#else>
                                <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                            </#if>
                        <#else>
                            <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                        </#if>
                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr align="center">
            <td colspan="4">
                <a href="#" class="smallSubmit" onclick="$.resourceManagement.saveResource()">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
