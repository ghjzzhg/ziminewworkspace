<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171031" type="text/javascript"></script>

<script type="text/javascript">
    $(function(){
        initToggleDueTask();
    });

    function initToggleDueTask() {
        $("select[name^='dueBase_']").each(function () {
            $(this).find("option").show();
            var thisGroupIndex = $(this).closest(".groupIndex").attr("groupIndex");
            var nodeIndex = $(this).closest(".nodeIndex").attr("nodeIndex");
            var i = thisGroupIndex;
            if(nodeIndex <= 1){
                i = i - 1;
            }
            var groupList = new Array();
            for(i; i > 0; i--){
                groupList.push($("div[groupIndex=" + i + "]").find("table").attr("groupid"));
            }
            if(groupList.length <= 0){
                $(this).find("option[value^='NG-']").hide();
            }else{
                $(this).find("option").each(function () {
                    var value = $(this).attr("value");
                    if(groupList.indexOf(value) < 0 && value.indexOf("NG-") >= 0){
                        $(this).hide();
                    }
                })
            }
        })
    }
    
    function toggleDueTask(index) {
        var value = $("select[name='dueBase_" + index + "']").val()
        if(value.indexOf("BT") >= 0){
            $("#showwords_" + index).css("display", "none");
            $("#addAnSub_" + index).css("display", "inline-block");
        }else{
            $("#showwords_" + index).css("display", "inline-block");
            $("#addAnSub_" + index).css("display", "none");
        }
    }
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
</style>
<#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), null, false)>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
        <#if isFromCase == "false">
            <span class="caption-subject bold uppercase">期限设置</span>
        <#else>
            <span class="caption-subject bold uppercase">${caseName?default('')} - 期限设置</span>
        </#if>
        </div>
    </div>
    <div class="portlet-body">
    <form id="templateNodesForm">
        <input type="hidden" id="templateId" name="templateId" value="${parameters.templateId?default('')}">
        <input type="hidden" id="isEmptyFlag" name="isEmptyFlag" value="${isEmptyFlag?default('')}">
    <#assign lineIndex = -1>
    <#list groups as group>
        <div class="panel panel-primary" name="panelPanelPrimary">
            <div class="panel-heading">
                <h3 class="panel-title">${group?index +1}、${group.name}</h3>
            </div>
            <div class="panel-body groupIndex" groupIndex="${group?index +1}">
                <table class="table table-striped table-bordered table-hover order-column" groupId="${group.id}"
                       id="nodesTable_${group.id}">
                    <thead>
                    <tr>
                        <th style="width:50px">序号</th>
                        <th style="width:30%">任务名称</th>
                        <th>期限</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#if isFromCase == "true" && isEmptyFlag != "true">
                            <#if nodesMap?keys?seq_contains(group.templateGroupId)>
                                <#assign nodes=nodesMap[group.templateGroupId]>
                            <#else>
                                <#assign nodes=[]>
                            </#if>
                        <#else>
                            <#if nodesMap?keys?seq_contains(group.id)>
                                <#assign nodes=nodesMap[group.id]>
                            <#else>
                                <#assign nodes=[]>
                            </#if>
                        </#if>
                        <#if nodes?has_content>
                            <#list nodes as node>
                                <#assign lineIndex = lineIndex + 1>
                            <tr class="nodeIndex" nodeIndex="${node?index + 1}">
                                <td>
                                    <div>
                                        ${node?index + 1}
                                        <input type="hidden" name="id_${lineIndex}" value="${node.id}"/>
                                    </div>
                                </td>
                                <td>
                                    <div style="white-space: normal">
                                        ${node.name?default('')}
                                    </div>
                                </td>
                                <td>
                                    <div style="white-space: normal;">
                                        在
                                        <select class="form-control select validate[required]" style="display: inline-block;width:200px"
                                                name="dueBase_${lineIndex}" onchange="toggleDueTask('${lineIndex}')">
                                            <option value="">-请选择-</option>
                                            <option value="BT-start"
                                                    <#if node.dueBase?has_content && node.dueBase == "BT-start">selected</#if>>
                                                CASE启动
                                            </option>
                                            <#list node.nodeMapList as group>
                                                <#if isFromCase == "true"  && isEmptyFlag != "true">
                                                    <option value="${group.id}"  <#if group.flag>disabled style="font-weight: bold"</#if>
                                                            <#if node.dueBase?has_content && node.dueBase?index_of("NG-") < 0 && node.dueBase == group.templateGroupId>selected</#if>><#if !group.flag>&nbsp;</#if>${group.name}</option>
                                                <#else>
                                                    <option value="${group.id}"  <#if group.flag>disabled style="font-weight: bold"</#if>
                                                            <#if node.dueBase?has_content && node.dueBase?index_of("NG-") < 0 && node.dueBase == group.id>selected</#if>><#if !group.flag>&nbsp;&nbsp;</#if>${group.name}</option>
                                                </#if>
                                            </#list>
                                            <#if baseTimes?has_content>
                                                <#list baseTimes as baseTime>
                                                    <option value="${baseTime.id}"
                                                            <#if node.dueBase?has_content && node.dueBase == baseTime.id>selected</#if>>${baseTime.name}</option>
                                                </#list>
                                            </#if>
                                        </select>
                                        <span id="showwords_${lineIndex}" style="display:<#if node.dueBase?has_content && node.dueBase?index_of("BT") != -1>none<#else> inline-block</#if>">
                                            结束后
                                        </span>
                                        <select class="form-control select" id="addAnSub_${lineIndex}" style="display:<#if node.dueBase?has_content && node.dueBase?index_of("BT") != -1>inline-block<#else> none</#if>;width:auto">
                                            <option value="1">之后</option>
                                            <option value="2" <#if node.dueDay?has_content && node.dueDay?index_of("-") &gt;=0>selected</#if>>之前</option>
                                        </select>
                                        <input type="text" class="form-control validate[custom[integer]]" maxlength="9"
                                               name="dueDay_${lineIndex}" id="dueDay_${lineIndex}" value="<#if node.dueDay?has_content && node.dueDay?index_of("-") &gt;=0>${node.dueDay?substring(1,node.dueDay?length)}<#else>${node.dueDay?default('')}</#if>"
                                               style="width: 50px;display:inline;margin-top:5px;margin-left:10px">天内完成
                                    </div>
                                </td>
                            </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </#list>
    <#if isFromCase == "false">
    </form>
    </#if>
        <div class="form-group" align="center">
            <div class="margiv-top-10">
            <#if parameters.blankCaseSessionKey?has_content>
                <input type="hidden" id="blankCaseSessionKey" name="blankCaseSessionKey" value="${parameters.blankCaseSessionKey}"/>
                <a href="javascript:$.caseManage.caseBack(6);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveBlankCaseProgress();" class="btn green"><#if msg??>下一步<#else>完成</#if></a>
            <#elseif isFromCase == "false"  && isEmptyFlag != "false">
                <a href="javascript:$.caseManage.caseTemplateBack(1);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveCaseTemplateNodes();" class="btn green"> 下一步 </a>
            <#else>
                <input type="hidden" id="caseId" value="${caseId}">
                <a href="javascript:$.caseManage.caseBack(1);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveCaseProgress('${caseId}');" class="btn green">
                    <#if msg??>下一步<#else>完成</#if></a>
                <input type="hidden" name="isFromCase" value="true"/>
            </#if>
            </div>
        </div>
    <#if isFromCase != "false">
        </form>
    </#if>
    </div>
</div>