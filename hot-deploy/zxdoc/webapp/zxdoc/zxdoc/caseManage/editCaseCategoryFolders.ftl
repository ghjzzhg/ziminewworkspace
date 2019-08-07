<link href="/images/lib/treetable/css/jquery.treetable.css?t=20170915" rel="stylesheet" type="text/css"/>
<link href="/images/lib/treetable/css/jquery.treetable.theme.default.css?t=20170915" rel="stylesheet" type="text/css"/>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<script src="/images/lib/treetable/jquery.treetable.js?t=20170915" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.js" type="text/javascript"></script>
<script type="text/javascript">
    var formData;
    $(function(){
        $("#templateFoldersForm").validationEngine("attach", {promptPosition: "topLeft"});
        formData = $("#templateFoldersForm").serialize();
    });

    function checkAllCategoryFolder(obj, type){
        var checked = $(obj).is(":checked");
        if(checked){
            $("input[name^=roleFolder_" + type + "]").prop("checked", "checked");
        }else{
            $("input[name^=roleFolder_" + type + "]").prop("checked", null);
        }
    }
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase"><#if template?has_content>${template.templateName}</#if>文件(夹)授权设置</span>
        </div>
    </div>
    <div class="portlet-body">
        <form id="templateFoldersForm" style="overflow-y: auto;overflow-x: hidden;">
            <input type="hidden" name="isCompany" value="${isCompany?string?default('false')}">
        <#if template?has_content>
            <input type="hidden" name="templateId" id="templateId" value="${parameters.templateId?default('')}">
        <#else>
            <input type="hidden" name="caseId" id="caseId" value="${parameters.caseId?default('')}">
            <input type="hidden" name="caseName" id="caseName" value="${parameters.caseName?default('')}">
        </#if>
        <table id="caseFolderTree" class="table table-striped table-bordered table-hover order-column">
            <thead>
            <tr>
                <th>目录</th>
             <#list roleTypeList as category>
                <th style="width: 150px"><label><input type="checkbox" onclick="checkAllCategoryFolder(this, '${category.roleTypeId}')"/>${category.description}授权 </label></th>
            </#list>
            </tr>
            </thead>
            <tbody id="folderBody">
                <#if !isCompany && !parameters.caseId?has_content && folderPath?has_content>
                    <#list folderPath as folder>
                    <tr>
                        <td>
                        ${folder}
                        </td>
                        <#list roleTypeList as category>
                            <td>
                                <#if folder != "3_folder">
                                    <input type="checkbox" role="${category.roleTypeId}" name="roleFolder_${category.roleTypeId}-${folder?index}" value="${folder}_folder" <#if folderRoles?has_content && folderRoles?seq_contains(folder + category.roleTypeId)>checked</#if>>
                                </#if>
                            </td>
                        </#list>
                    </tr>
                    </#list>
                </#if>
            </tbody>
        </table>
            <input type="hidden" id="blankCaseSessionKey" name="blankCaseSessionKey" value="${parameters.blankCaseSessionKey!}"/>
        </form>
        <div class="form-group" align="center">
            <div class="margiv-top-10">
            <#if parameters.blankCaseSessionKey?has_content>
                <a href="javascript:$.caseManage.caseBack(7);" class="btn green">上一步</a>
                <a href="javascript:$.caseManage.saveBlankCaseFolders();" class="btn green"> 完成 </a>
            <#elseif !template?has_content>
                <a href="javascript:$.caseManage.caseBack(0);" class="btn green"> 上一步 </a>
                <a href="javascript:$.caseManage.saveCaseFolders();" class="btn green"> 完成 </a>
            <#else>
                <a href="javascript:$.caseManage.caseTemplateBack(0);" class="btn green"> 上一步 </a>
                <a href="javascript:$.caseManage.saveCaseFolders();" class="btn green"> 完成 </a>
            </#if>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
<#outputformat "UNXML">
var alreadySharedFiles = ${context.existingFolders};
</#outputformat>
function checkSharedFiles(scopeSelector){
    for (var role in alreadySharedFiles) {
        var sharedFiles = alreadySharedFiles[role];
        $(scopeSelector).find("input[role=" + role + "]:not(:checked)").each(function(){
            if(sharedFiles.indexOf($(this).val()) > -1){
                $(this).prop("checked", "checked");
            }
        })
    }
}
<#if !isCompany && !parameters.caseId?has_content && folderPath?has_content>
    $(function(){
        checkSharedFiles("#folderBody");
    })
</#if>
<#if isCompany>
    $(function(){
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
            async: false,
            dataType: 'xml',
            cache:false,
            data:{command:"getFiles", defaultFolderId: "1", folderId : "1", otherModules: "true", partyId: "", fileSharePartyId: "", externalLoginKey:'${externalLoginKey?default('')}'},
            success: function (content) {
                var template = $("#nodeHtml").html(), templateNoCheck = $("#nodeHtmlNoCheck").html(), rowsHtml = "";
                if(content != null){
                    var dataList = xmlToJson(content);
                    if(dataList.Connector.Files != null) {
                        var fileList = dataList.Connector.Files.File;
                        if(fileList != null && fileList.length){
                            for(var i = 0; i < fileList.length; i++){
                                var file = fileList[i].attributes;
                                if(file.fileId == 3){//CASE文档不允许整个共享
                                    rowsHtml += Mustache.render(templateNoCheck, {nodeId: file.fileId, nodeName: file.fileName, nodeParentId: "", nodeType: file.fileType, directory: file.fileType != "file"});
                                }else{
                                    rowsHtml += Mustache.render(template, {nodeId: file.fileId, nodeName: file.fileName, nodeParentId: "", nodeType: file.fileType, directory: file.fileType != "file"});
                                }
                            }
                        }
                    }
                    $("#folderBody").html(rowsHtml);
                    checkSharedFiles("#folderBody");
                    initTreeTable();
                    var openLayer = getLayer();
                    openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
                }
            }
        });
    })
    var folderTable;
    function initTreeTable(){
        folderTable = $("#caseFolderTree").treetable({
            expandable: true,
            /*onNodeCollapse: function() {
                var node = this;
                folderTable.treetable("unloadBranch", node);
            },*/
            onNodeExpand: function() {
                var node = this;
                if(!$("tr[data-tt-id=" + node.id + "]").data("ajaxDone")){
                    $.ajax({
                        type: 'post',
                        url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
                        async: true,
                        dataType: 'xml',
                        cache:false,
                        data:{command:"getFiles", defaultFolderId: "1", folderId : node.id, otherModules: "true", partyId: "", fileSharePartyId: "", externalLoginKey:'${externalLoginKey?default('')}'},
                        success: function (content) {
                            $("tr[data-tt-id=" + node.id + "]").data("ajaxDone", true);
                            var template = $("#nodeHtml").html(), rowsHtml = "";

                            if(content != null){
                                var dataList = xmlToJson(content);
                                if(dataList.Connector.Files != null) {
                                    var fileList = dataList.Connector.Files.File;
                                    if(fileList != null && fileList.length){
                                        for(var i = 0; i < fileList.length; i++){
                                            var file = fileList[i].attributes;
                                            rowsHtml += Mustache.render(template, {nodeId: file.fileId, nodeName: file.fileName, nodeParentId: node.id, nodeType: file.fileType, directory: file.fileType != "file"});
                                        }
                                    }
                                }
                                folderTable.treetable("loadBranch", node, rowsHtml);
                                checkSharedFiles("tr[data-tt-parent-id=" + node.id + "]");
                                var openLayer = getLayer();
                                openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
                            }
                        }
                    });
                }
            }
        });
    }
</#if>
</script>
<script id="nodeHtml" type="text/html">
    <tr data-tt-id="{{nodeId}}" data-tt-parent-id="{{nodeParentId}}" data-tt-branch="{{directory}}">
        <td>
            <span class="{{nodeType}}">{{nodeName}}</span>
        </td>
        <#list roleTypeList as category>
        <td>
            <input type="checkbox" role="${category.roleTypeId}" name="roleFolder_${category.roleTypeId}-{{nodeId}}" value="{{nodeId}}_{{nodeType}}">
        </td>
        </#list>
    </tr>
</script>
<script id="nodeHtmlNoCheck" type="text/html">
    <tr data-tt-id="{{nodeId}}" data-tt-parent-id="{{nodeParentId}}" data-tt-branch="{{directory}}">
        <td>
            <span class="{{nodeType}}">{{nodeName}}</span>
        </td>
        <#list roleTypeList as category>
        <td>

        </td>
        </#list>
    </tr>
</script>
<div class="note note-info">
            <pre>
                温馨提示
                1.在列表目录一列中展示的是企业目录。
                2.可根据CASE的需要，授权企业目录或具体文件给相关参与方。
                3.目录授权给某参与方后，参与方可在授权目录及子目录内执行增删改查操作，请谨慎选择。
                4.当某参与方退出CASE后，原先授权的目录及文件自动取消授权。
            </pre>
</div>