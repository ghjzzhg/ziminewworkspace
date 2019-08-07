<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/awesomefont/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="/metronic-web/simple-line-icons.min.css" rel="stylesheet" type="text/css">
<link href="/images/lib/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="/images/lib/uniform/themes/default/css/uniform.default.css" rel="stylesheet" type="text/css">
<link href="/images/lib/awesomefont/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css">
<link href="/metronic-web/component/components.min.css" rel="stylesheet" id="style_components" type="text/css">
<link href="/images/lib/awesomefont/css/plugins.min.css" rel="stylesheet" type="text/css">
<link href="/metronic-web/layout/layout.min.css" rel="stylesheet" type="text/css">
<link href="/metronic-web/layout/css/themes/default.min.css" rel="stylesheet" type="text/css" id="style_color">
<link href="/metronic-web/layout/css/custom.min.css" rel="stylesheet" type="text/css">
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#if data?has_content>
<style type="text/css">
    .demo_line_01{
        color: black;
        padding: 0 20px 0;
        margin: 20px 0;
        line-height: 1px;
        text-align: center;
    }
</style>
<script type="application/javascript" language="JavaScript">
    $(function () {
        <#if data.memberList?has_content>
            <#list data.memberList as member>
                var unfinishedHtml = "";
                var finishHtml = "";
                var untitleHtml = "";
                var titleHtml = "";
                var unfinishNum = 0;
                var finishNum = 0;
                <#if member.caseTasUnfinishedList?has_content>
                    <#list member.caseTasUnfinishedList as unfinishedLine>
                        unfinishNum += 1;
                        untitleHtml += '<ul><li class="mt-list-item"><div class="list-icon-container"><a title="修改" href="javascript:openAddCaseTask(' + "'" + ${unfinishedLine.id} +"'" + ')"><i class="fa fa-pencil-square-o"/></a><a title="删除"  href="javascript:delCaseTask(' + "'" + ${unfinishedLine.id} +"'" + ')"><i class="icon-close"/></a></div><div style="width: 160px;text-align: center;" class="list-datetime"> ${unfinishedLine.dueTime?string("yyyy-MM-dd HH:mm:ss")}</div><div class="list-item-content" style="word-wrap: break-word">${unfinishedLine.title}</div></li></ul>'
                    </#list>
                    unfinishedHtml = '<div class="mt-list-container list-simple ext-1 group"><a class="list-toggle-container" data-toggle="collapse" href="#completed-simple${member.personPartyId}" aria-expanded="false">' +
                            ' <div class="list-toggle active uppercase sbg-red">未完成<span class="badge badge-default pull-right bg-white font-green bold">' + unfinishNum + '</span></div></a>' +
                            '<div class="panel-collapse collapse in" id="completed-simple${member.personPartyId}">' + untitleHtml + "</div></div>";
                </#if>
                <#if member.caseTaskFinishList?has_content>
                    <#list member.caseTaskFinishList as finishLine>
                        finishNum += 1;
                        titleHtml += '<ul><li class="mt-list-item"><div class="list-icon-container"><a title="归档"  href="javascript:editCaseTask(' + "'" + ${finishLine.id} +"','CASE_STATUS_FILED'" + ')"><i class="fa fa-check-circle-o"/></a></div><div  style="width: 160px;text-align: center;" class="list-datetime"> ${finishLine.dueTime?string("yyyy-MM-dd HH:mm:ss")} </div><div class="list-item-content" style="word-wrap: break-word"> ${finishLine.title}</div></li></ul>'
                    </#list>
                    finishHtml = '<div class="mt-list-container list-simple ext-1 group"><a class="list-toggle-container collapsed" data-toggle="collapse" href="#completed-unsimple${member.personPartyId}" aria-expanded="false">' +
                            ' <div class="list-toggle done uppercase">已完成<span class="badge badge-default pull-right bg-white font-green bold">' + finishNum + '</span></div></a>' +
                            '<div class="panel-collapse collapse" id="completed-unsimple${member.personPartyId}">' + titleHtml + "</div></div>";
                </#if>
                var html = splitHeadTemplateHtml('${member.personPartyId}', '${member.fullName}', unfinishedHtml, finishHtml);
                if (finishNum > 0 || unfinishNum > 0) {
                    <#if member_index%2 != 0>
                        $("#rightDiv").append("<div style='float:left;width: 100%'>" + html + "</div>");
                    <#else>
                        $("#leftDev").append("<div style='float:left;width: 100%'>" + html + "</div>");
                    </#if>
                }
            </#list>
        </#if>

        $("#caseTaskListTable").hide();
    })

    function splitHeadTemplateHtml(partyId, fullName, unfinishedHtml, finishHtml) {
        var headTmplateHtml = '<div class="mt-element-list" style="margin-bottom: 10px"><div class="mt-list-head list-simple ext-1 font-white bg-blue-chambray"><div class="list-head-title-container">' +
                '<div class="badge badge-default pull-right bg-white font-dark bold"></div><span class="list-title">' + fullName + '</span></div></div>' +
                unfinishedHtml + finishHtml + '</div>'
        return headTmplateHtml;
    }

    function openAddCaseTask(id) {
        $.ajax({
            url: "openCaseTaskEdit",
            type: "POST",
            data: {"caseId": "${caseId}", partyGroupId: '${partyGroupId}', caseTaskId: id},
            dataType: "html",
            async: true,
            success: function (content) {
                $("#caseTaskInfo").html(content);
                parent.adjustContentFrame();
            }
        });
    }

    function delCaseTask(id) {

        var confirmIndex = getLayer().confirm("确认是否删除?", {
            btn: ['确定', '取消']
        }, function () {
            $.ajax({
                type: 'post',
                url: "delCaseTask",
                async: true,
                dataType: 'json',
                data: {caseTaskId: id},
                success: function (content) {
                    initCaseTask();
                }
            });
            getLayer().close(confirmIndex);
        })
    }

    function editCaseTask(id, status) {
        $.ajax({
            type: 'post',
            url: "editCaseTask",
            async: true,
            dataType: 'json',
            data: {caseTaskId: id, caseTaskStatus: status},
            success: function (data) {
                showInfo("已归档");
                initCaseTask();
            }
        });
    }

    function editCaseTasks(id) {
        $.ajax({
            type: 'post',
            url: "editCaseTask",
            async: true,
            dataType: 'json',
            data: {caseTaskId: id, caseTaskStatus: $("#caseStatusType_" + id).val()},
            success: function (data) {
                showInfo(data.msg);
                initCaseTask();
                showTable();
            }
        });
    }

    function openFileManager(filePathList, fileSharePartyId) {
        var shareFolders = encodeURIComponent(filePathList);
        displayInLayer("上传文档", {
            requestUrl: "/ckfinder/control/OpenFileinputSelection?allowLocalUpload=false&fileSharePartyId=" + fileSharePartyId + "&filePathList=" + shareFolders + "&externalLoginKey=" + getExternalLoginKey(),
            width: '950px',
            height: '600px'
        });
    }

    function showDistribution() {
        $("#distribution").show();
        $("#caseTaskListTable").hide();
        parent.adjustContentFrame();
    }

    function showTable() {
        $("#distribution").hide();
        $("#caseTaskListTable").show();
        parent.adjustContentFrame();
    }
</script>
<div style="color:#FFF;width: 100%;text-align: -webkit-right;" id="newFileCreate">
    <span class="btn red btn-outline btn-file" id="createFile" onclick="showDistribution()" style="margin: 5px">
        <span class="fileinput-new"> 任务状态 </span>
    </span>
    <span class="btn red btn-outline btn-file" id="createFile" onclick="showTable()" style="margin: 5px">
        <span class="fileinput-new"> 我的任务 </span>
    </span>
    <span class="btn red btn-outline btn-file" id="createFile" onclick="openAddCaseTask('')" style="margin: 5px">
        <span class="fileinput-new"> 新增任务 </span>
    </span>
</div>
<hr class="demo_line_01"></hr>
<div style="width: 100%;" id="distribution">
    <div class="portlet-body" id="leftDev" style="width: 510px;float: left; margin-right: 20px; margin-left: 20px">
    </div>
    <div class="portlet-body" id="rightDiv" style="width: 510px;float: left;margin-left: 20px;">
    </div>
</div>
<table style="width: 100%" class="table table-striped table-bordered table-advance table-hover dataTable no-footer"
       id="caseTaskListTable">
    <thead>
    <tr>
        <th width="30px">序号</th>
        <th>任务描述</th>
        <th width="140px">期限</th>
        <th width="70px">任务状态</th>
        <th width="200px">模版文件</th>
        <th width="150px">操作</th>
    </tr>
    </thead>
    <tbody>
        <#if data.manageList?has_content>
            <#assign count = 1>
            <#list data.manageList as memberInfo>
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
                                    <option value="${line.enumId}"<#if unLine.status == line.enumId>
                                            selected </#if>>${line.description}</option>
                                </#list>
                            </select>
                        </td>
                        <td>
                            <#list unLine.dataReourceList as file>
                                <a href="/content/control/downloadUploadFile?dataResourceId=${file.dataResourceId?default("")}&externalLoginKey=${externalLoginKey}"
                                   onclick="">${file.dataResourceName?default("")}</a></br>
                            </#list>
                        </td>
                        <td><a class="btn btn-md green btn-outline"
                               href="javascript:editCaseTasks('${unLine.id?default("")}');" title="更新状态"> <i
                                class="fa fa-check"></i> </a>
                            <a class="btn btn-md green btn-outline"
                               href="javascript:openFileManager('${fileList?default("")}', '${companyPartyId?default("")}');">上传文档 </a>
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
                                <a href="/content/control/downloadUploadFile?dataResourceId=${file.dataResourceId?default("")}&externalLoginKey=${externalLoginKey}"
                                   onclick="">${file.dataResourceName?default("")}</a>
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
</#if>