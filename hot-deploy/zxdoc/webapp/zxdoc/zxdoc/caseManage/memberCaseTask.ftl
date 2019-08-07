<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
    function editCaseTask(id){
        $.ajax({
            type: 'post',
            url: "editCaseTask",
            async: true,
            dataType: 'json',
            data:{caseTaskId:id,caseTaskStatus:$("#caseStatusType_"+id).val()},
            success: function (data) {
                showInfo(data.msg);
                initCaseTask();
            }
        });
    }

    function openFileManager(filePathList, fileSharePartyId){
        var shareFolders = encodeURIComponent(filePathList);
        displayInLayer("上传文档", {requestUrl: "/ckfinder/control/OpenFileinputSelection?allowLocalUpload=false&fileSharePartyId=" + fileSharePartyId + "&filePathList=" + shareFolders + "&externalLoginKey=" + getExternalLoginKey(),
            width:'950px',
            height:'600px',
        });
    }
</script>

<table class="table table-striped table-bordered table-advance table-hover dataTable no-footer" id="caseTaskListTable">
    <thead>
    <tr>
        <th width="30px">序号</th>
        <th>任务描述</th>
        <th width="140px">期限</th>
        <th width="70px">任务状态</th>
        <th width="200px">模版文件</th>
        <th width="130px">操作</th>
    </tr>
    </thead>
    <tbody>
    <#if data.memberList?has_content>
        <#assign count = 1>
        <#list data.memberList as memberInfo>
            <#if memberInfo.caseTasUnfinishedList?has_content>
            <#list memberInfo.caseTasUnfinishedList as unLine>
                <#assign count = unLine_index + 1>
                <tr class="odd" role="row">
                    <td>${count}</td>
                    <td>
                        <div style="word-wrap: break-word">${unLine.title?default("")}</div>
                    </td>
                    <td>${unLine.dueTime?string("yyyy-MM-dd HH:mm:ss")}</td>
                    <td>
                        <select id="caseStatusType_${unLine.id?default("")}">
                            <#list data.caseStatusTypeList as line>
                                <option value="${line.enumId}"<#if unLine.status == line.enumId> selected </#if>>${line.description}</option>
                            </#list>
                        </select>
                    </td>
                    <td>
                        <#list unLine.dataReourceList as file>
                            <a href="/content/control/downloadUploadFile?dataResourceId=${file.dataResourceId}&externalLoginKey=${externalLoginKey}" onclick="">${file.dataResourceName}</a></br>
                        </#list>
                    </td>
                    <td><a class="btn btn-md green btn-outline" href="javascript:editCaseTask('${unLine.id?default("")}');" title="修改"> <i class="fa fa-check"></i> </a>
                        <a class="btn btn-md green btn-outline" href="javascript:openFileManager('${fileList}', '${companyPartyId}');">发布文档 </a>
                    </td>
                </tr>
            </#list>
            </#if>
            <#if memberInfo.caseTaskFinishList?has_content>
                <#list memberInfo.caseTaskFinishList as line>
                <tr class="odd" role="row">
                    <td>${count + line_index + 1}</td>
                    <td>${line.title?default("")}</td>
                    <td>${line.dueTime?string("yyyy-MM-dd HH:mm:ss")}</td>
                    <td>${line.description?default("")}</td>
                    <td>
                        <#list line.dataReourceList as file>
                            <a href="/content/control/downloadUploadFile?dataResourceId=${file.dataResourceId}&externalLoginKey=${externalLoginKey}" onclick="">${file.dataResourceName}</a>
                        </#list>
                    </td>
                    <td></td>
                </tr>
                </#list>
            </#if>
        </#list>
    </#if>
    </tbody>
</table>