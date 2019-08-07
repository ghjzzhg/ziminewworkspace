<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.js" type="text/javascript"></script>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>

<script type="text/javascript">
    var formData;
    $(function(){
//        var oTable = initDatatables("#caseNodes", {paging: false,"info": false, "ordering": false});
        initNode()
        $("#templateNodesForm").validationEngine("attach", {promptPosition: "topLeft"});
        formData = $("#templateNodesForm").serialize();
        var panelWidth = "";
        /*$("table[id^='nodesTable_']").each(function(){
            if(panelWidth != null && panelWidth != ""){
                var thisWidth = $(this).width();
                if(thisWidth > panelWidth){
                    panelWidth = thisWidth;
                }
            }else{
                panelWidth = $(this).width();
            }
        })
        if(panelWidth == null || panelWidth == ""){
            panelWidth = "800"
        }
        $("div[name='panelPanelPrimary']").each(function(){
            $(this).width(panelWidth + 50)
        })*/
//        $(".nodeFile").attachment();
    });

    //给每个大步骤添加一个空白的小步骤节点。
    function initNode(){
        var maxSeq = -1;
        $("#templateNodesForm").find("input[name^=seq_]").each(function(){
            var seq = parseInt($(this).attr("name").substring(4));
            if(seq > maxSeq){
                maxSeq = seq;
            }
        });
        var allTables = $("#templateNodesForm table");
        allTables.each(function(){
            var table = $(this), groupId = table.attr("groupId"), tbody = table.find("tbody");
            if(table.find("input[name^=seq_]").length <= 0){
                tbody.append(Mustache.render($("#nodeHtml").html(), {index: tbody.find("tr").length + 1, seq:  ++ maxSeq, groupId: groupId}));
            }
        });
        $(".nodeFile").attachment();
        //重置排序
        resortSeq();
    }

    function appendNode(obj, groupId){
        var btn = $(obj), tr = btn.closest("tr"), form = $("#templateNodesForm"), table = btn.closest("table"), tbody = table.find("tbody"), maxSeq = -1;
        form.find("input[name^=seq_]").each(function(){
            var seq = parseInt($(this).attr("name").substring(4));
            if(seq > maxSeq){
                maxSeq = seq;
            }
        })
        tr.after(Mustache.render($("#nodeHtml").html(), {index: tbody.find("tr").length + 1, seq: maxSeq + 1, groupId: groupId}));
        $("#atta_" + (maxSeq + 1)).attachment();
        var openLayer = getLayer();
        openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
        //重置排序
        resortSeq();
    }
    function removeNodeNodes(obj){

        var confirmIndex = getLayer().confirm("确定删除该节点吗?", {
            btn: ['确定', '取消']
        }, function () {

            var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table");
            tr.remove();
            var openLayer = getLayer();
            openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
            //重置排序
            resortSeq();
            getLayer().close(confirmIndex);
        })
    }
    function moveNode(obj, dir){
        var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table");
        if(dir == "up"){
            var upside = tr.prev();
            if(upside.length){
                upside.before(tr);
            }
        }else if(dir == "down"){
            var downside = tr.next();
            if(downside.length){
                downside.after(tr);
            }
        }
        //重置排序
        resortSeq();
    }

    function resortSeq(){
        var allTables = $("#templateNodesForm table");
        allTables.each(function(){
            var table = $(this);
            table.find("input[name^=seq_]").each(function(index){
                $(this).val(index);
                $(this).siblings("span").html(index + 1);
            });
        });
    }

    function removeNodeFile(obj){
        var btn = $(obj)
    }
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
</style>
<#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
<#assign enumerations = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "CASE_ROLE"), null, false)>
<script id="nodeHtml" type="text/html">
    <tr class="nodeIndex" nodeIndex="{{index}}">
        <td><input type="hidden" name="id_{{seq}}"/><input type="hidden" name="seq_{{seq}}"/><input type="hidden" name="group_{{seq}}" value="{{groupId}}"/><span>{{index}}</span></td>
        <td>
            <input type="text" class="form-control validate[required,condRequired[executor_{{seq}}],custom[noSpecial]]" maxlength="50" name="name_{{seq}}" id="name_{{seq}}"/>
        </td>
        <td>
            <select class="form-control select validate[required]" style="width: 110px" name="executor_{{seq}}" id="executor_{{seq}}">
                <option value="">-请选择-</option>
            <#list roleTypeList as category>
                <option value="${category.roleTypeId}">${category.description}</option>
            </#list>
            </select></td>
        <td>
            <input type="text" name="atta_{{seq}}" id="atta_{{seq}}" class="nodeFile" data-size-limit="${fileSize}">
        </td>
        <td>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');" title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this, '{{groupId}}');" title="添加"> <i class="fa fa-plus"></i> </a>
            <br/>
            <br/>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');" title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
            <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNodeNodes(this);" title="删除"> <i class="fa fa-remove"></i> </a>
        </td>
    </tr>
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <#if isFromCase == "false">
            <span class="caption-subject bold uppercase">任务</span>
            <#else>
                <span class="caption-subject bold uppercase">${caseName?default('')} - 任务</span>
                <input id="caseName" type="hidden" value="${caseName?default('')}">
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
                    <table class="table table-striped table-bordered table-hover order-column" groupId="${group.id}" id="nodesTable_${group.id}">
                        <thead>
                        <tr>
                            <th style="width:50px">序号</th>
                            <th style="width: 300px">任务名称</th>
                            <th style="width:150px">执行人</th>
                            <th>模板文件</th>
                            <th style="width: 80px">操作</th>
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
                                    <td><span>${node?index + 1}</span>
                                        <input type="hidden" name="id_${lineIndex}" value="${node.id}"/>
                                        <input type="hidden" name="seq_${lineIndex}" value="${node.seq}"/>
                                        <#if isFromCase == "true">
                                            <input type="hidden" name="group_${lineIndex}" value="${group.id}"/>
                                        <#else>
                                            <input type="hidden" name="group_${lineIndex}" value="${node.groupId}"/>
                                        </#if>
                                    </td>
                                    <td>
                                        <input type="text" class="form-control validate[required,required,custom[noSpecial]]" maxlength="50" id="name_${lineIndex}" name="name_${lineIndex}" value="${node.name?default('')}"/></td>
                                    <td>
                                        <select class="form-control select validate[required]" style="width: 100%" name="executor_${lineIndex}" id="executor_${lineIndex}">
                                            <option value="">-请选择-</option>
                                            <#list roleTypeList as category>
                                                <option value="${category.roleTypeId}" <#if node.executor?has_content && node.executor == category.roleTypeId>selected</#if>>${category.description}</option>
                                            </#list>
                                        </select>
                                    </td>
                                    <td>
                                        <#assign nodeFiles=node.nodeFiles>
                                        <#assign fileInfo = "">
                                        <#assign fileValue = "">
                                        <#if nodeFiles?has_content>
                                            <#list nodeFiles as nodeFile>
                                                <#assign fileInfo = fileInfo + ",{\"dataResourceId\": \"" +  nodeFile.dataResourceId + "\", \"dataResourceName\": \"" + nodeFile.dataResourceName + "\"}">
                                                <#assign fileValue = fileValue + "," +  nodeFile.dataResourceId>
                                            </#list>
                                            <#assign fileInfo = fileInfo?substring(1)>
                                            <#assign fileValue = fileValue?substring(1)>
                                        </#if>
                                        <input type="text" name="atta_${lineIndex}" id="atta_${lineIndex}" class="nodeFile" data-size-limit="${fileSize}" value="${fileValue}" data-value='[${fileInfo}]'>
                                    </td>
                                    <td>
                                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');" title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
                                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this, '${group.id}');" title="添加"> <i class="fa fa-plus"></i> </a>
                                        <br/>
                                        <br/>
                                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');" title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
                                        <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNodeNodes(this);" title="删除"> <i class="fa fa-remove"></i> </a>
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
                <a href="javascript:$.caseManage.caseBack(5);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveBlankCaseProgress(true);" class="btn green">下一步</a>
            <#elseif isFromCase == "false"  && isEmptyFlag != "false">
                <a href="javascript:$.caseManage.caseTemplateBack(2);" class="btn green"> 上一步 </a>
                <a href="javascript:$.caseManage.saveCaseTemplateNodes(true);" class="btn green"> 下一步 </a>
            <#else>
                <input type="hidden" id="caseId" value="${caseId}">
                <a href="javascript:$.caseManage.caseBack(2);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveCaseProgress('${caseId}',true);" class="btn green">下一步</a>
                <input type="hidden" name="isFromCase" value="true"/>
            </#if>
            </div>
        </div>
<#if isFromCase != "false">
    </form>
</#if>
    </div>
</div>
<div class="note note-info">
            <pre>
                温馨提示
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                1.可在流程中创建多个任务。
                2.如果按钮显示为完成，没有显示下一步，说明您不是当前case的企业参与方登录。
                3.在上传文件时请检查。当前所有的文件的总大小，请确保该页面中所有的<span style="color: red">文件的总大小不超过${fileSize?default("50")}兆</span>
                4.<span style="color: red">文件名称不要超过50个字符。</span>
            </pre>
</div>