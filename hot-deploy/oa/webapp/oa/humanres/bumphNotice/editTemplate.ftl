<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="noticeTemplate"]', {
            allowFileManager: true
        });
    });
</script>
<#assign noticeTemplateId = parameters.noticeTemplateId>
<#assign noticeId = parameters.noticeId>
<#if noticeTemplateId?has_content>
    <#assign noticeTemplateInfo = delegator.findByPrimaryKey("TblNoticeTemplate", {"noticeTemplateId": noticeTemplateId})>
    <#assign noticeTemplateName = noticeTemplateInfo.noticeTemplateName?default('')>
    <#assign noticeTemplate = noticeTemplateInfo.noticeTemplate?default('')>
<#--<#else>-->
    <#--<#assign -->
</#if>
<div>
    <form name="templateForm" id="templateForm" class="basic-form" action="#">
        <input type="hidden" name="noticeTemplateId" value="${noticeTemplateId?default('')}">
        <input type="hidden" value="${noticeId?default('')}" name="noticeId">
            <table>
                <tbody>
                <tr>
                    <td class="label">
                        <label for="noticeTemplateName" id="BumphNotice_templateName_title"><b class="requiredAsterisk">*</b>模板名称：</label>
                    </td>
                    <td colspan="2" class="jqv">
                        <input type="text" id="noticeTemplateName" name="noticeTemplateName" value="${noticeTemplateName?default('')}" class="validate[required]" >
                    </td>
                </tr>
                <tr>
                    <td class="label">　
                        <label for="BumphNotice_template" id="BumphNotice_template_title"><b class="requiredAsterisk">*</b>模板内容：</label>
                    </td>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="4" class="jqv">
                        <textarea name="noticeTemplate" id="noticeTemplate" class="validate[required]">${noticeTemplate?default('')}</textarea>
                    </td>

                </tr>
                </tbody>
                <tr align="left">
                    <td>
                        <a href="#" class="smallSubmit" onclick="$.bumphNotice.saveNoticeTemplate(template,'${noticeId}')">保存模板</a>
                    </td>
                </tr>

            </table>
    </form>
</div>