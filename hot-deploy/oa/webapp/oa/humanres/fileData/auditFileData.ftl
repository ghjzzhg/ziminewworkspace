<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>

<#if fileDataMap?has_content>
    <#assign fileDataId = fileDataMap.fileDataId?default('')>
    <#assign parentTypeName = fileDataMap.parentTypeName?default('')>
    <#assign sonTypeName = fileDataMap.sonTypeName?default('')>
    <#assign parentName = fileDataMap.parentName?default('')>
    <#assign sonName = fileDataMap.sonName?default('')>
    <#assign documentTitle = fileDataMap.documentTitle?default('')>
    <#assign documentNumber = fileDataMap.documentNumber?default('')>
    <#assign feedback = fileDataMap.feedback1?default('')>
    <#assign documentStatus = fileDataMap.documentStatus1?default('')>
    <#assign releaseDepartment = fileDataMap.releaseDepartment?default('')>
    <#assign releaseDate = fileDataMap.releaseDates?default('')>
    <#assign auditor = fileDataMap.auditor?default('')>
    <#assign auditorName = fileDataMap.auditorName?default('')>
    <#assign remarks = fileDataMap.remarks?default('')>
    <#assign browseStaff = fileDataMap.browseStaff?default('')>
    <#assign fullName = fileDataMap.fullName?default('')>
    <#assign documentPublishingRange = fileDataMap.documentPublishingRange?default('')>
    <#assign groupName = fileDataMap.groupName?default('')>
    <#assign groupNames = fileDataMap.groupNames?default('')>
    <#assign documentContent = fileDataMap.documentContent?default('')>
    <#assign auditContent = fileDataMap.auditContent?default('')>
    <#assign fileList = fileDataMap.fileList?default("")>
    <#assign fileId = fileDataMap.fileId?default("")>

</#if>
<script language="javascript">
    var auditContent;
    $(function () {
        auditContent = KindEditor.create('textarea[name="auditContent"]', {
            allowFileManager: true
        });
    })

</script>
<div class="yui3-skin-sam">
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="fileDataAuditForm" id="fileDataAuditForm">
        <input type="hidden" value="${fileDataId?default('')}" name="fileDataId">
        <div style="width:100%;">
            <div>
                <div style="border:1px solid;">
                    <table style="width:100%" class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tr>
                            <td class="label">
                                <label for="parentTypeName">文档类别：</label>
                            </td>
                            <td colspan="2">
                            ${parentName?default('')}
                            </td>
                            <td class="label">
                                <label for="sonTypeName">文档子类别：</label>
                            </td>
                            <td colspan="2">
                            ${sonName?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentTitle">文档标题：</label>
                            </td>
                            <td colspan="2" class="jqv">
                            ${documentTitle?default('')}
                            </td>
                            <td class="label">
                                <label for="documentNumber">文档编号：</label>
                            </td>
                            <td colspan="2" class="jqv">
                            ${documentNumber?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="feedback">可否反馈：</label>
                            </td>
                            <td colspan="2">
                            ${feedback?default('')}
                            </td>
                            <td class="label">
                                <label for="documentStatus">文档状态：</label>
                            </td>
                            <td colspan="3">
                            ${documentStatus?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="releaseDepartment"><b class="requiredAsterisk">*</b>发布部门：</label>
                            </td>
                            <td colspan="2" class="jqv">
                            ${groupNames?default('')}
                            </td>
                            <td class="label">
                                <label for="releaseDate">发布日期：</label>
                            </td>
                            <td colspan="2" style="margin-right: 28px">
                            ${releaseDate?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="fileId" id="fileId_title">附件：</label>
                            </td>
                            <td colspan="2">
                                <div>
                                <@showFileList id="accessory_div" hiddenId="fileId" fileList=fileList?default('') uploadFlag=false ></@showFileList>
                                </div>
                            </td>
                            <td class="label">
                                <label for="auditor" >审核人：</label>
                            </td>
                            <td colspan="2" class="jqv">
                            ${auditorName?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="browseStaff" >需确认浏览人员：</label>
                            </td>
                            <td colspan="10" class="jqv">
                            ${fullName?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentPublishingRange">文档发布范围：</label>
                            </td>
                            <td colspan="10" class="jqv">
                            ${groupName?default('')}
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="remarks">备注说明：</label>
                            </td>
                            <td colspan="10">
                                <div id="remarks" >
                                </div>
                                <script>
                                    $("#remarks").append("<label>"+ unescapeHtmlText('${remarks?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="documentContent">文档内容：</label>
                            </td>
                            <td colspan="10">
                                <div id="documentContent" >
                                </div>
                                <script>
                                    $("#documentContent").append("<label>"+ unescapeHtmlText('${documentContent?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="auditContent"><b class="requiredAsterisk">*</b>审核内容：</label>
                            </td>
                            <td  colspan="7">
                                <textarea style="height: 80px;width:100%" name="auditContent"  id="auditContent_title" class="validate[required]">${auditContent?default('')}</textarea>
                            </td>
                        </tr>
                        <tr>
                            <input type="hidden" value="" name="status">
                            <td colspan="3" align="center">
                            <a href="#" class="smallSubmit" onclick="$.FileData.auditSuccess()">审核通过</a>
                            </td>
                            <td colspan="3" align="center">
                            <a href="#" class="smallSubmit" onclick="$.FileData.auditFail()">驳回文档</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>