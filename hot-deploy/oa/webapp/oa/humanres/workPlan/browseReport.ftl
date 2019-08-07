<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });
        $("#descriptionLab").html(unescapeHtmlText('${workReportMap.description?default('')}'));
    });

    function individualFeedback(id,workreportId){
        displayInTab3("individualFeedbackTbl", "个人反馈信息", {
            requestUrl: "individualFeedback",
            data: {workReportId:workreportId,personId:id},
            width: "600px",
            position: "center"
        });
    }
</script>
<form name="workReportForm" id="workReportForm" class="basic-form">
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">报告基本信息</li>
            </ul>
            <br class="clear">
        </div>
        <table class="basic-table">
            <tbody>
            <tr>
                <td class="label">
                    <label>报告编号:</label>
                </td>
                <td>
                    ${workReportMap.reportNumber?default('')}
                    <input type="hidden" id="workReportId" name="workReportId" value="${workReportMap.workReportId?default('')}">
                </td>
                <td class="label">
                    <label>报告类别:</label>
                </td>
                <td>
                    ${workReportMap.reportTypeName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>报告编号:</label></td>
                <td>
                    ${workReportMap.statusName?default('')}
                </td>
                <td class="label">
                    <label>录 入 人:</label></td>
                <td>
                    ${workReportMap.fullName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>报告名称:</label></td>
                <td>
                    ${workReportMap.reportTitle?default('')}
                </td>
                <td class="label">
                    <label>报告进度:</label></td>
                <td>
                    ${workReportMap.planName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>简要要求:</label></td>
                <td>
                    ${workReportMap.request?default('')}
                </td>
                <td class="label">
                    <label>项目主管:</label></td>
                <td>
                    ${workReportMap.leaderNames?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>生效时间:</label></td>
                <td>
                ${workReportMap.startDate?default('')}~${workReportMap.endDate?default('')}
                </td>
                <td class="label">
                    <label>上传文件</label>
                </td>
                <td>
                <@showFileList id="workReportFile" hiddenId="fileId" fileList=workReportMap.fileList?default('') uploadFlag=false></@showFileList>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>执 行 人:</label></td>
                <td colspan="3">
                    ${workReportMap.allName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>内容说明:</label></td>
                <td colspan="3">
                    <label id="descriptionLab"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</form>