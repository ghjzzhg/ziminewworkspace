<script type="text/javascript" src="/images/jquery/ui/js/jquery-ui-1.10.3.js"></script>
<link href="/images/jquery/ui/js/jquery-ui.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/treetable/css/jquery.treetable.css?t=20170915" rel="stylesheet" type="text/css"/>
<link href="/images/lib/treetable/css/jquery.treetable.theme.default.css?t=20170915" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/treetable/jquery.treetable.js?t=20170915" type="text/javascript"></script>
<#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<style>
    table.dataTable, table.dataTable th, table.dataTable td {
        -webkit-box-sizing: content-box;
        -moz-box-sizing: content-box;
        box-sizing: inherit;
    }

    .file-tool{
        float:right;
        font-size: 14px;
        background: whitesmoke;
        position: absolute;
        right: 0px;
        top:10px;
    }

    .file-name-td{
        position: relative;
    }

    .file-tool > a {
        text-decoration:none;
        padding: 0 5px;
    }
    .file-container{
        background: transparent;
    }

    #folderList td{
        padding: 8px;
        font-size: 14px;
        white-space: nowrap;
    }
    #example thead th{
        border-bottom: 0;
    }
    #example .group td{
        background: lightseagreen;
        color: white;
        font-size: 18px;
    }
</style>
<script language="JavaScript">
    var readOnly = ${readOnly?default('false')};
    var accessFolderId = '${accessFolderId?default('')}';
    var otherModules = "${otherModules?default('')}";
    var fileSharePartyId = "${fileSharePartyId?default('')}";//文件/文件夹实际所属人
    var filePathList = unescapeHtmlText("${filePathList?default('')}");
    <#if otherModules?has_content && otherModules == "1">
        <#assign loginUserPartyId = parameters.userLogin.partyId>
        <#assign mainAccounts = delegator.findByAnd("PartyRelationship", {"partyIdTo" : loginUserPartyId, "partyRelationshipTypeId": "ORG_SUB_ACCOUNT"}, null, false)/>
        <#if mainAccounts?size &gt; 0 >
            <#assign groupId = mainAccounts[0]["partyIdFrom"]>
        <#else>
            <#assign groupId = loginUserPartyId >
        </#if>

        var isFolderOwner = fileSharePartyId == '${groupId}';
    <#else>
        var isFolderOwner = true;
    </#if>
    /**
     * 页面文件夹和文件列表初始化
     */
    $(function () {
		ajaxSearchFiles();
    })

    /**
     * 拼接文件夹和查询文件列表
     * @param data 文件信息集合
     */
    /*function appendFolder(data){
        var folderHtml = "";
        var folderList = data.Connector.ResourceTypes.ResourceType;
        if(folderList != null ){
            if(folderList.length){
                for(var i = 0; i < folderList.length; i++){
                    var name = folderList[i].attributes.name;
                    var folderId = folderList[i].attributes.folderId;
                    if(folderId == "1"){
                        folderHtml += '<li class="nav-item start active open">'
                        $("#defaultFolder").val(folderId);
                    }else{
                        folderHtml += '<li class="nav-item start">'
                    }
                    folderHtml +=  '<a href="javascript:setDefault(' + "'" + folderId + "'" +  ",'" + name + "'" + ')" class="nav-link ">' +
                            '<i class="icon-home"></i>' +
                            '<span class="title">' + name + '</span><input type="hidden" id="typeId" value="' + folderId + '">' +
                            ' <span class="selected"></span></a></li>';
                }
            }else{
                $("#defaultFolder").val(folderList.attributes.folderId);
                folderHtml +=  '<li class="nav-item start active open"><a href="javascript:setDefault(' + "'" + folderList.attributes.folderId + "'" +  ",'" +  folderList.attributes.name  + "'" + ')" class="nav-link ">' +
                        '<span class="title">' + folderList.attributes.name + '</span><input type="hidden" id="typeId" value="' + folderList.attributes.folderId + '">' +
                        ' <span class="selected"></span></a></li>';
            }
            $("#folderList").html(folderHtml)
        }
        showFileList("1","",fileSharePartyId)
    }*/
    /**
     * 拼接文件夹和查询文件列表
     * @param data 文件信息集合
     */
    function appendFolder(data){
        var folderHtml = "";
        var folderList = data.Connector.ResourceTypes.ResourceType;
        if(folderList != null ){
            if(folderList.length){
                for(var i = 0; i < folderList.length; i++){
                    var name = folderList[i].attributes.name;
                    var folderId = folderList[i].attributes.folderId;
                    folderHtml += '<tr data-tt-id="' + folderId + '" data-tt-parent-id="" data-tt-branch="' + (folderId != 2) + '">'
                    if(folderId == "1"){
                        $("#defaultFolder").val(folderId);
                    }
                    folderHtml +=  '<td><a href="javascript:setDefault(' + "'" + folderId + "'" +  ",'" + name + "'" + ')" class="nav-link ">' +
                            '<i class="icon-home"></i>' +
                            '<span class="title">' + name + '</span><input type="hidden" id="typeId" value="' + folderId + '">' +
                            ' <span class="selected"></span></a></td>';
                    folderHtml += "</tr>";
                }
            }else{
                $("#defaultFolder").val(folderList.attributes.folderId);
                folderHtml += '<tr data-tt-id="' + folderList.attributes.folderId + '" data-tt-parent-id="" data-tt-branch="true">'
                folderHtml +=  '<td><a href="javascript:setDefault(' + "'" + folderList.attributes.folderId + "'" +  ",'" +  folderList.attributes.name  + "'" + ')" class="nav-link ">' +
                        '<span class="title">' + folderList.attributes.name + '</span><input type="hidden" id="typeId" value="' + folderList.attributes.folderId + '">' +
                        ' <span class="selected"></span></a></td>';
                folderHtml += "</tr>";
            }
            $("#folderList").html(folderHtml)
        }
        initTreeTable();
        showFileList("1","",fileSharePartyId)
    }

    var folderTable;
    function initTreeTable(){
        folderTable = $("#folderList").treetable({
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
                        data:{command:"getFolders", defaultFolderId: "1", folderId : node.id, partyId: "", fileSharePartyId: "", externalLoginKey:'${externalLoginKey?default('')}'},
                        success: function (content) {
                            $("tr[data-tt-id=" + node.id + "]").data("ajaxDone", true);
                            var template = $("#nodeHtml").html(), rowsHtml = "";

                            if(content != null){
                                var dataList = xmlToJson(content);
                                if(dataList.Connector.Folders != null) {
                                    var folderList = dataList.Connector.Folders.Folder;
                                    if(folderList){
                                        if(folderList.length){
                                            for(var i = 0; i < folderList.length; i++){
                                                var folder = folderList[i].attributes, title = folder.name, name = (title.length > 4 ? title.substring(0, 4) + "..." : title);
                                                rowsHtml += '<tr data-tt-id="' + folder.id + '" data-tt-parent-id="' + folder.parent + '" data-tt-branch="true">'
                                                rowsHtml +=  '<td><a href="javascript:showFileList(' + "'" + folder.id + "'" +  ",'" +  name  + "','" + fileSharePartyId + '\')" title="' + title + '" class="nav-link ">' +
                                                        '<span class="title">' + name + '</span>' +
                                                        '</a></td>';
                                                rowsHtml += "</tr>";
                                            }
                                        }else{
                                            var folder = folderList.attributes, title = folder.name, name = (title.length > 4 ? title.substring(0, 4) + "..." : title);
                                            rowsHtml += '<tr data-tt-id="' + folder.id + '" data-tt-parent-id="' + folder.parent + '" data-tt-branch="true">'
                                            rowsHtml +=  '<td><a href="javascript:showFileList(' + "'" + folder.id + "'" +  ",'" +  name  + "','" + fileSharePartyId + '\')" title="' + title + '" class="nav-link ">' +
                                                    '<span class="title">' + name + '</span>' +
                                                    '</a></td>';
                                            rowsHtml += "</tr>";
                                        }
                                    }
                                }
                                folderTable.treetable("loadBranch", node, rowsHtml);
                            }
                        }
                    });
                }
            }
        });
    }
    /**
     * 设置默认页面
     * @param folderId 文件夹ID
     * @param name 文件夹类别
     */
    function setDefault(folderId){
        $("#defaultFolder").val(folderId);
        $("#folderId").val(folderId);
        $("#allFolder").find("li").each(function(){
            var typeId = $(this).find("#typeId").val();
            if(typeId == folderId){
                $(this).attr("class","nav-item start active open");
            }else{
                $(this).attr("class","nav-item start");
            }
        })
        showFileList(folderId,"",fileSharePartyId);
//        showOrHidDivByFolderType(folderId);
    }

    /**
     *拼接路径和ID 并查询
     * @param partyId 文件归属人
     * @param id 路径id
     * @param folderName 文件夹名称
     */
    function showFileList(id,folderName,partyId){
        ajaxSearchFiles(id, partyId);

//        showOrHidDivByFolderType(id);
    }

    function checkFileStatus(currentFolder, rootId, fileName){
        if(fileName.length > 50){
            showError("文件名称长度请小于50个字符");
            return ;
        }
        var exists = false;
        $.ajax({
            type: 'post',
            url: "checkFileStatus",
            async: false,
            dataType: 'json',
            data:{rootFolderId:rootId,currentFolder:currentFolder, fileName:fileName, fileSharePartyId: fileSharePartyId, otherModules:otherModules},
            success: function (content) {
                exists = true;
            }
        });
        return exists;
    }

    /**
     *查询文件夹中的文件列表
     *
     */
    function ajaxSearchFiles(){
    	var chatRoomId = "";
    	$.ajax({
            type: 'post',
            url: "/ckfinder/control/GetCaseCooperationRecordJsons2?externalLoginKey=${externalLoginKey}",
            dataType: 'json',
            async: false,
		    data:{caseId:${parameters.caseId}},
            success: function (content) {
                if(content.chatRoomIds != null && content.chatRoomIds != "" ){
                	chatRoomId = content.chatRoomIds;
                }else{
					chatRoomId = "1";
                }
            	
            }
            
        });
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/GetFileList",
            async: true,
            dataType: 'json',
            cache:false,
		    data:{chatRoomId:chatRoomId},
            success: function (content) {
            	$("img[id^='photo_']").attr("src","");
            	createTableData(content);
            }
            
        });
    }

    var datable;
    /**
     * 初始化table
     */
    function initDataTable(){
        return false;
        datable = $('#example').DataTable({
            language:{
                "sEmptyTable": "当前无文件"
            },
            "paging":   false,
            "ordering": false,
            "info":     false,
            "searching": false,
            "bDestory": true,
            "columnDefs": [
                { "width": "30px", "targets": 0 },
                { "width": "15px", "targets": 1 },
                {"visible": false, "targets": 3},
                {"width": "130px", "targets": 4},
                {"width": "80px", "targets": 5},
                {"width": "170px", "targets": 6}
                ],
            "order": [[2, 'asc']],
            "displayLength": 25,
            "drawCallback": function(settings) {
                var api = this.api();
                var rows = api.rows({
                    page: 'current'
                }).nodes();
                var last = null;

                api.column(3, {
                    page: 'current'
                }).data().each(function(group, i) {
                    if (last !== group) {
                        $(rows).eq(i).before('<tr class="group" style="background: lightseagreen;color: white;font-size: 18px;"><td colspan="6"><i class="fa fa-chevron-right"></i>&nbsp;' + group + '</td></tr>');
                        last = group;
                    }
                });
            }
        });
        /*var height = $("#example").height() + 20;
        var width = $("#fileListWrapper").width();
        var windowheight = $(window).height() - 160;
        if(windowheight < height ){
            height = windowheight;
            width = width + 15
        }
        if(otherModules != null && otherModules == "1"){
            var exampleHeight = $("#example").height() + 20;
            if(exampleHeight > height){
                height = exampleHeight;
            }
        }
        $("#example").parent().attr("style","height:" + height + "px;width:" + width + "px;" )*/
    }

    /**
     *打开上传文件页面
     */
    function showUploadFilePage(){
        var folderPathHid = $("#folderPathHid").val();//隐藏的文件夹名称和对应的ID
        var folderIds = "";
        if(folderPathHid != null && folderPathHid != "") {
            folderPathHid = folderPathHid.substring(0, folderPathHid.length - 1);
            //如果打开了多层文件夹，则是用|进行拼接的，这里需要将拼接的数据分割
            var folderList = folderPathHid.split("|");
            for (var i = 0; i < folderList.length; i++) {
                var folder = folderList[i];
                var folderName = folder.substring(0, folder.indexOf("~"));
                var folderId = folder.substring(folder.lastIndexOf("~") + 1, folder.lastIndexOf("?"));
                var partyId = folder.substring(folder.lastIndexOf("?") + 1, folder.length);
                folderIds += folderId + ";";
            }
        }
        displayInLayer('上传文件', {requestUrl: 'showUploadFilePage',data:{rootId:rootId,folderId:folderId, fileSharePartyId: fileSharePartyId, otherModules:otherModules,folderStructure:folderIds},width:'500px',height:'550px', shade: 0.2, layer:{end: function(name){
//            if(otherModules != null && otherModules == "1"){
//                filePathList += setFilePath() + name + ",";
//            }
            ajaxSearchFiles(folderId,"");
        }}})
    }

    function showOrHidCreateTool(flag , id){
        if(flag){
            $("#" + id).show();
        }else{
            $("#" + id).hide();
        }
    }

    /**
     *创建table
     */
    function createTableData(dataList){
        var fileHtml = "", folderPermissions = "";
        if(dataList.Connector.Files != null){
            var fileList = dataList.Connector.Files.File;
            folderPermissions = dataList.Connector.Files.folder.attributes.parentFolderPermissions;
            showOrHidCreateTool(setFilePermissions(folderPermissions, 3), "createFolder");
            showOrHidCreateTool(setFilePermissions(folderPermissions, 4), "createFile");
            showOrHidCreateTool(setFilePermissions(folderPermissions, 5), "downloadFiles");
            if(fileList != null && fileList.length){//多条数据时
                for(var i = 0; i < fileList.length; i++){
                    var file = fileList[i].attributes;
                    fileHtml += splitTableHtml(file, i);
                }
            }else if(fileList != null && fileList.fileName != ""){//只有单个数据时
                fileHtml = splitTableHtml(fileList.attributes, 0);
            }else{
                fileHtml =  '';

            }

//            $("a.attachment-image").fancybox({
//                'overlayShow':false,
//                'transitionIn':'elastic',
//                'transitionOut':'elastic'
//            });
        }else{
            if(dataList.Connector.Error != null){
                var num = dataList.Connector.Error.attributes.number
                if(num == '601'){
                    $("#newFileCreate").hide();
                    showInfo("文件夹不存在，请联系管理员")
                }
            }
            fileHtml = "";
        }
        //如果列表控件不为null则清空数据
        if(datable != null){
            datable.clear();
            datable.destroy();
        }
        //如果拼接html不为空则添加到table中
        if(fileHtml != ''){
            $("#fileTable").html(fileHtml)
            $("#fileTable").find("tr").each(function(){
                var $tr = $(this), groupName = $tr.attr("groupName"), prevGroupName = $tr.prev("tr").attr("groupName");
                if(!prevGroupName || groupName != prevGroupName){
                    $tr.before('<tr class="group" style="background: lightseagreen;color: white;font-size: 18px;"><td colspan="6"><i class="fa fa-chevron-right"></i>&nbsp;' + groupName + '</td></tr>');
                }
            })


        }else{
            $("#fileTable").html("<tr><td colspan='6' style='text-align:center'>无记录</td></tr>");
        }

        //如果列表控件不为null则重新加载控件，否则初始化控件
        initDataTable()
        return folderPermissions;
    }

    /**
     * 拼接创建table的html
     * @param file 文件信息
     * @i 编号
     * @return 返回table的html
     */
    function splitTableHtml(file, i){
        var fileHtml = "";
        var fileType = file.fileType;
        var linkFile;//文件的链接，文件夹和文件需要进行区分
        var fileFlag;//删除文件和文件夹的链接需要进行区分
        var icon;//文件夹或者文件的图标显示
        var historyHtml = "";//历史文件信息
        var downloadFileHtml = ""; //下载文件信息
        var imageHtml = "";//图片信息
        var checkboxHtml = "";
        var shareHtml = "";
        var renameHtml = "";
        var removeHtml = "";
        var remarksHtml = "";
        //如果是文件则下载，如果是文件夹则打开进入文件夹
        if(fileType != "file"){
            var remarks = file.remarks;
            if(remarks != null && remarks != ""){
                if(remarks.length > 10){
                    var subremarks = remarks.substring(0,10);
                    remarksHtml = "<span style='color: #adadad;' title='" + remarks + "'>[" + subremarks + "...]</span>"
                }else{
                    remarksHtml = "<span style='color: #adadad;'>[" + remarks + "]</span>"
                }
            }
            linkFile = '<a href="javascript:showFileList(' +  "'" + file.fileId + "'" + ",'" + file.fileName + "'"  + ",'" + file.partyId + "'" +')">';
            fileFlag = "'folder','" + file.fileId + "'";
            icon = "<i class='font-green fa fa-folder-o'></i>&nbsp;";
//            checkboxHtml = '<input type="checkbox" name="foldCheck_' + file.fileId + '" value="' + file.fileId + '" fileName="' + file.fileName  + '">';
        }else{
            var type = file.fileName.substring(file.fileName.lastIndexOf(".") + 1, file.fileName.length);
           /* icon = "<i class='fa fa-file-o'></i>  ";*/
            fileType = type;
           if(fileType=="doc" || fileType=="docx")
           {
               icon = "<i class='font-green fa fa-file-word-o'></i>&nbsp;";
           }else if(fileType=="xls" || fileType=="xlsx")
           {
               icon = "<i class='font-green fa fa-file-excel-o'></i>&nbsp;";
           }else if(fileType=="gif" || fileType=="png" || fileType=="bmp" || fileType=="jpg" || fileType=="jpeg")
           {
               icon = "<i class='font-green fa fa-file-image-o'></i>&nbsp;";
           }else if(fileType=="zip")
           {
               icon = "<i class='font-green fa fa-file-zip-o'></i>&nbsp;";
           }else if(fileType=="ppt" || fileType=="pptx")
           {
               icon = "<i class='font-green fa fa-file-powerpoint-o'></i>&nbsp;";
           }else if(fileType=="pdf")
           {
               icon = "<i class='font-green fa fa-file-pdf-o'></i>&nbsp;";
           }else{
               icon = "<i class='font-green fa fa-file-text-o'></i>&nbsp;";
           }
//            if("png,jpg,jpeg,gif,bmp".indexOf(type.toLowerCase()) > -1) {
//                linkFile = '<a class="attachment-image" href="javascript:showPhotoPage(' +  "'" + file.fileId + "'" +')" id="file_' + file.fileId + '">';
//                imageHtml ='<img id="photo_' + file.fileId + '" style="display: none;" border=0 src="showPhoto?fileId=' + file.fileId + '&time=' + (new Date()) + '">';
//            }else if("pdf".indexOf(type.toLowerCase()) > -1){
//                linkFile = '<a href="javascript:viewPdfInLayer(' +  "'" + file.fileId + "'"  +')" id="file_' + file.fileId + '">';
//            }else{
                linkFile = '<a href="javascript:viewPdfInLayer(' +  "'" + file.fileId + "'"  +')" id="file_' + file.fileId + '">';
//            }
            fileFlag = "'file','" + file.fileId + "'";
            if(file.fileHistory == "1"){
                historyHtml = '<a style="text-decoration:none" href="javascript:showHistoryList(' + fileFlag + ');" title="历史"><i class="fa fa-history"></i> </a>';
            }
            downloadFileHtml = '<a style="text-decoration:none" href="javascript:downloadFile(' +  "'" + file.fileId + "'" +')" title="下载"><i class="fa fa-download"></i> </a>';
            checkboxHtml = '<input type="checkbox" name="fileCheck_' + file.fileId + '" value="' + file.fileId + '" fileName="' + file.fileName  + '">';
        }
        //分享文件信息
        if(otherModules && isFolderOwner && setFilePermissions(file.filePermissions, 0)){
            shareHtml = '<a href="javascript:showShareFilePage(' + fileFlag + ',' + "'" + file.fileName + "'" + ');" title="分享"><i class="fa fa-share-alt"></i> </a>';
        }
        <#assign loginUserPartyId = parameters.userLogin.partyId>
        <#assign isChildAcount = delegator.findByAnd("PartyRelationship", {"partyIdFrom" : loginUserPartyId, "partyRelationshipTypeId": "ORG_SUB_ACCOUNT"}, null, false)/>
        <#if isChildAcount?size &gt; 0 >
            if(fileType!= 'folder'){
        		shareHtml = '<a href="javascript:showShareFilePagePrivate(' + fileFlag + ',' + "'" + file.fileName + "'" + ');" title="分享"><i class="fa fa-share-alt"></i> </a>';
        	}
        </#if>
        
        
        //修改文件名
        if(setFilePermissions(file.filePermissions, 1)) {
            renameHtml = '<a href="javascript:showRename(' + fileFlag + ',' + "'" + file.fileName + "'" + ');" title="重命名"> <i class="fa fa-eraser"></i> </a>';
        }
        //删除文件
        if(setFilePermissions(file.filePermissions, 2)) {
            removeHtml = '<a href="#nowhere" onclick="delFileOrFolder(event,' + fileFlag + ');" title="删除"> <i class="fa fa-remove"></i> </a>';
        }
        var classHtml = ""
        if((i % 2) == 0){
            classHtml = " role='row' class='odd'";
        }
        //拼接html在页面显示history
        var fileSubName = file.fileName;
//        if(fileSubName.length > 10){
//            fileSubName = fileSubName.substring(0,10) + "...";
//        }
        fileHtml +=  '<tr ' + classHtml + ' groupName="' + file.fullName + '"><td>' + (i + 1) + '</td><td align="center">' + checkboxHtml + '</td>' +
                '<td class="file-name-td">'
                + icon + linkFile + '<span title="' + file.fileName  + '">' + fileSubName + '</span ></a>&nbsp;'+ remarksHtml + imageHtml + '<div class="file-tool" style="display: none;" id="fileTool' + fileType + file.fileId +  '">'
                + shareHtml + downloadFileHtml + historyHtml + renameHtml + removeHtml + '</div></td><td>' + file.createFullName + '</td><td>' + file.fileVersion +
                '</td><td>' + file.createTime + '</td></tr>';
        return fileHtml;

    }

    /**
     * 111111，第一位：分享；第二位：重命名；第三位：删除；第四位：新增文件夹；第五位：新增文件，第六位：下载。
     * @param filePermissions 权限
     * @param typeFlag 父文件夹还是文件列表的标识 1表示是父文件夹
     * @param id 文件的id,父文件夹不需要
     */
    function setFilePermissions(filePermissions, i){
        var flag = filePermissions.charAt(i);
        return !readOnly && flag == "1"
    }

    function showOrHidDivByFolderType(id){
//        var folderType = $("#defaultFolder").val();
        if(id == "2" && otherModules == "" || fileSharePartyId == '${parameters.userLogin.partyId}'){
            $("#newFileCreate").hide();
            $("div[id^='fileTool']").each(function(){
                $(this).remove()
            });
        }else{
            $("#newFileCreate").show();
        }
    }

    /**
     * 打开文件编辑页面
     * @param id 文件Id
     */
    function openFileEdit(id){
        displayInLayer('文档编辑', {requestUrl: 'openFileEdit',data:{fileId:id},width:'1000px',height:'780px', shade: 0, layer:{end: function(){
        }, cancel:function(index, layero){
            var prepareLeave = layero.find("iframe")[0].contentWindow.prepareLeave;
            if(prepareLeave){
                prepareLeave();
            }
        }}})
    }

    /**
     * 图片预览
     * @param id 文件Id
     */
    function showPhotoPage(id){
        var height = $('#photo_'+id).height();
        if(height > 600){
            $('#photo_'+id).height(580);
            height = 600;
        }
        var width = $('#photo_'+id).width();
        if(width > 1000){
            $('#photo_'+id).width(1000);
            width = 1000;
        }

        layer.open({
            type: 1,
            title: false,
            area: [height, width],
            closeBtn: 1,
            skin: 'layui-layer-nobg', //没有背景色
            shadeClose: true,
            content: $('#photo_'+id)
        });
    }

    /**
     * 打开文件分享页面
     * @param fileType 标记文件还是文件夹，暂时无用
     * @param fileName 文件名称家文件ID
     */
    function showShareFilePage(fileType, fileId,name){
        displayInLayer2(name + "    分享设置", {requestUrl: "ManageFileShare",width:800,data:{moduleType: 'case', moduleId: '${parameters.caseId?default('')}', fileId:fileId, fileType:fileType}, layer:{zIndex: 99999999}});
    }
    
    function showShareFilePagePrivate(fileType, fileId,name){
        displayInLayer2(name + "    分享设置", {requestUrl: "ManageFileSharePrivate",width:800,data:{moduleType: 'case', moduleId: '${parameters.caseId?default('')}', fileId:fileId, fileType:fileType}, layer:{zIndex: 99999999}});
    }

    /**
     * 显示文件的历史版本
     * @param flag 标记文件还是文件夹，暂时无用
     * @param fileName 文件名称家文件ID
     */
    function showHistoryList(flag, fileName){
        var fileId = fileName.substring(fileName.indexOf("-") + 1, fileName.length);
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/showFileHistoryList?externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'json',
            data:{fileId:fileId, otherModules: otherModules, fileSharePartyId: fileSharePartyId},
            success: function (content) {
                var fileList = content.fileList;
                var fileVersion = "";
                for(var i = 0; i < fileList.length; i++){
                    fileVersion += '<a href="javascript:downloadFile(' +  "'" + fileList[i].historyFileId + "'" +')">' +fileList[i].version + '</a><br>';
                }
//                fileVersion = fileVersion.substring(0,fileVersion.length - 1);
                layer.tips(fileVersion, '#file_'+fileId, {
                    tips: [2, '#E0E0E0'],
                    shade: [0.001, '#000'],
                    shadeClose: true,
                    time:20000
                });
            }
        });
    }

    var editFileDialog;
    /**
     * 根据url进行区分重命名文件还是重命名文件夹
     * @param url 链接
     * @param fileId 文件名或者文件夹路径
     * @param fileName 文件名或者文件夹路径
     */
    function showRename(url, fileId,fileName){
        var fileSuffix = "";
        if(url == "file"){
            fileSuffix = fileName.substring(fileName.lastIndexOf("."),fileName.length);
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        var folderIds = "";
        var rootId = $("#defaultFolder").val();
        var folderPathHid = $("#folderPathHid").val();
        if(folderPathHid != null && folderPathHid != "") {
            folderPathHid = folderPathHid.substring(0, folderPathHid.length - 1);
            //如果打开了多层文件夹，则是用|进行拼接的，这里需要将拼接的数据分割
            var folderList = folderPathHid.split("|");
            for (var i = 0; i < folderList.length; i++) {
                var folder = folderList[i];
                var folderName = folder.substring(0, folder.indexOf("~"));
                var folderId = folder.substring(folder.lastIndexOf("~") + 1, folder.lastIndexOf("?"));
                var partyId = folder.substring(folder.lastIndexOf("?") + 1, folder.length);
                folderIds += folderId + ";";
            }
        }
        var height = 240
        if(url == "file"){
            height = 180
        }
        displayInLayer('修改名称', {requestUrl: 'showRename',height: height, width: 400,data:{fileId:fileId,fileSuffix: fileSuffix, fileName:fileName, fileFlag: "edit", fileType: url,folderIds:folderIds, rootId:rootId}, shade: 0.2, end: function(value){
            $("#newFileName").val(value)
            if(url == "folder"){
                renameFolder(fileId);
            }else {
                $("#fileSuffix").html(fileSuffix);
                renameFile(fileId);
            }
        }, layer: {
            zIndex:99999999,
            success:function(layero, index) {
                getLayer().setTop(layero);
            }
        }})
    }

    /**
     *修改文件夹名称
     * @param fileName 文件名名称
     */
    function renameFolder(fileId){
        var folderInfo = $("#newFileName").val();
        var newFileName = folderInfo.substring(0, folderInfo.indexOf("|"));
        var newfoldeRemark = folderInfo.substring(folderInfo.indexOf("|") + 1, folderInfo.length);
        if(newFileName == ""){
            showInfo("请输入新名称！");
            return;
        }
        if(/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test(newFileName))
        {
            showError("文件名不能包含特殊字符");
            return;
        }
        var folderId = $("#folderId").val();//选中文件夹ID
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'xml',
            data:{command:"RenameFolder",CKFinderCommand:"true",folderId:fileId,newFolderName:newFileName, otherModules: otherModules, fileSharePartyId: fileSharePartyId, newfoldeRemark: newfoldeRemark},
            success: function (content) {
                var data = xmlToJson(content);
                if(data.Connector.RenamedFolder.attributes != null){
                    if(data.Connector.RenamedFolder.attributes.errorFlag != null){
                        if(data.Connector.RenamedFolder.attributes.errorFlag == '601'){
                            showError("文件夹重复，请重新命名！");
                        }
                    }
                }
                ajaxSearchFiles(folderId, "");
                $("#newFileName").val("");
            }
        });

    }

    /**
     *修改文件名称
     * @param fileName 文件名称
     */
    function renameFile(fileId){
        var newFileName = $("#newFileName").val();
        var fileSuffix = $("#fileSuffix").html();
        newFileName += fileSuffix;
        if(newFileName == ""){
            showInfo("请输入新名称！");
            return;
        }
        var folderId = $("#folderId").val();//选中文件夹ID
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'xml',
            data:{command:"RenameFile",CKFinderCommand:"true",fileId:fileId,newFileName:newFileName, otherModules: otherModules, fileSharePartyId: fileSharePartyId},
            success: function (content) {
                var data = xmlToJson(content);
                if(data.Connector.RenamedFile.attributes != null){
                    if(data.Connector.RenamedFile.attributes.errorFlag != null){
                        if(data.Connector.RenamedFile.attributes.errorFlag == '601'){
                            showError("文件重复，请重新命名！");
                        }
                    }
                }
                ajaxSearchFiles(folderId, "");
                $("#newFileName").val("");
            }
        });
    }

    /**
     * 根据url进行区分删除文件还是删除文件夹
     * @param url 链接
     * @param fileName 文件名或者文件夹路径
     */
    function delFileOrFolder(e, url, fileName) {
        var confirmTop = e.pageY;
        if(url == "folder"){
            var confirmIndex = getLayer().confirm("确认是否删除文件夹?", {
                btn: ['确定', '取消'], offset: confirmTop + 'px'
            }, function () {
                delFolder(fileName);
                getLayer().close(confirmIndex);
            });
        }else {
            var confirmIndex = getLayer().confirm("确认是否删除文件?", {
                btn: ['确定', '取消'], offset: '100px'
            }, function () {
                delFile(fileName);
                getLayer().close(confirmIndex);
            });
        }
    }

    /**
     * 删除文件
     * @param fileInfo 文件名
     */
    function delFile(fileInfo) {
        var folderId = $("#folderId").val();//选中文件夹ID
        var files = fileInfo.split(",");
        var fileInfos = "";
        for(var i = 0; i < files.length; i++){
            var id = files[i].substring(files[i].indexOf("-") + 1, files[i].length);
            fileInfos = "&files[" + i + "][fileId]="+id;
        }
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?"+fileInfos + "&externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'xml',
            data:{command:"DeleteFiles",CKFinderCommand:"true", otherModules: otherModules, fileSharePartyId: fileSharePartyId},
            success: function () {
                ajaxSearchFiles(folderId, "");
            }
        });
    }

    /**
     * 删除文件夹
     * @param id 文件夹ID
     */
    function delFolder(id) {
        var folderId = $("#folderId").val();//选中文件夹ID
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'xml',
            data:{command:"DeleteFolder", CKFinderCommand:"true", folderId:id, otherModules: otherModules, fileSharePartyId: fileSharePartyId},
            success: function () {
                ajaxSearchFiles(folderId, "");
            }
        });
    }

    /**
     *打开增加文件夹弹出框
     */
    var rowDialog;
    function addRow(){
        var folderIds = "";
        var rootId = $("#defaultFolder").val();
        var folderPathHid = $("#folderPathHid").val();
        if(folderPathHid != null && folderPathHid != "") {
            folderPathHid = folderPathHid.substring(0, folderPathHid.length - 1);
            //如果打开了多层文件夹，则是用|进行拼接的，这里需要将拼接的数据分割
            var folderList = folderPathHid.split("|");
            for (var i = 0; i < folderList.length; i++) {
                var folder = folderList[i];
                var folderName = folder.substring(0, folder.indexOf("~"));
                var folderId = folder.substring(folder.lastIndexOf("~") + 1, folder.lastIndexOf("?"));
                var partyId = folder.substring(folder.lastIndexOf("?") + 1, folder.length);
                folderIds += folderId + ";";
            }
        }
        displayInLayer('新建文件夹', {requestUrl: 'showRename',height: 260, width: 300,maxmin: true,data:{fileFlag: "add",folderIds: folderIds,rootId :rootId}, shade: 0.2, end: function(value){
            if(value){
                var folderName = value.substring(0, value.indexOf("|"));
                var foldeRemarks = value.substring(value.indexOf("|") + 1, value.length);
                $("#folderName").val(folderName);
                $("#foldeRemarks").val(foldeRemarks);
                addFolder();
            }
        }})
    }

    /**
     *增加文件夹
     */
    function addFolder(){
        var rootFolderId = $("#defaultFolder").val();//默认文件夹分类（个人文档）
        var folderName = $("#folderName").val();//选中文件夹路径（/a/）
        var folderId = $("#folderId").val();//选中文件夹ID
        var foldeRemarks = $("#foldeRemarks").val();//文件夹备注
        $.ajax({
            type: 'post',
            url: "/ckfinder/control/action?externalLoginKey=${externalLoginKey}",
            async: true,
            dataType: 'xml',
            data:{command:"CreateFolder",rootFolderId:rootFolderId, CKFinderCommand:"true", currentFolder:folderId, NewFolderName: folderName, otherModules: otherModules, fileSharePartyId: fileSharePartyId, foldeRemarks: foldeRemarks},
            success: function (content) {
                $("#folderName").val("");
                $("#foldeRemarks").val("");
                var data = xmlToJson(content);
                if(data.Connector.NewFolder.attributes != null){
                    if(data.Connector.NewFolder.attributes.errorFlag != null){
                        if(data.Connector.NewFolder.attributes.errorFlag == '603'){
                            showError("文件夹重复，请重新命名！");
                        }
                    }
                }
                ajaxSearchFiles(folderId, "");
            }
        });
    }

    /**
     * 下载文件
     */
    function downloadFile(fileId){
        var form=$("<form>");//定义一个form表单
        form.attr("style","display:none");
        form.attr("target","");
        form.attr("method","post");
        form.attr("action","/ckfinder/control/action?externalLoginKey=${externalLoginKey}&command=DownloadFile&fileId="+fileId);
        $("body").append(form);//将表单放置在web中
        form.submit();//表单提交
    }

    /**
     * 显示打开文件路径
     */
    function showFolderPath(){
        var folderPathHid = $("#folderPathHid").val();//隐藏的文件夹名称和对应的ID
        if(folderPathHid != null && folderPathHid != ""){
            folderPathHid = folderPathHid.substring(0,folderPathHid.length - 1);
            var pathHtml = '';
            //如果打开了多层文件夹，则是用|进行拼接的，这里需要将拼接的数据分割
            var folderList = folderPathHid.split("|");
            //拼接文件夹路径在页面显示
            var folderNamePath = "";
            var folderIds = "";
            for(var i = 0; i < folderList.length; i++){
                var folder = folderList[i];
                var folderName = folder.substring(0, folder.indexOf("~"));
                if(!folderName){
                    continue;
                }
                var folderId = folder.substring(folder.lastIndexOf("~") + 1, folder.lastIndexOf("?"));
                var partyId = folder.substring(folder.lastIndexOf("?") + 1, folder.length);
                folderNamePath += folderName + "/";
                folderIds += folderId + ";";
                if(folder != ""){
                    if((i + 1) == folderList.length){
                        pathHtml += '<span>&gt;</span><span>  '+ folderName + '  </span>';
                    }else{
                        pathHtml += '<span>&gt;</span><a href="javascript:showFilePathList(' + "'" + folderNamePath + "'" + ",'" + folderIds + "'" + ",'" + folderId + "'" + ",'" + partyId+ "'" + ');">  ' + folder.substring(0,folder.indexOf("~")) + '  </a>';
                    }
                }
            }
            //点击进入文件夹将显示文件夹路径并显示
            $("#folderShowPath").html(pathHtml);
            $("#folderUl").show();
            $("#headFolderUl").hide();
        }else{
            //如果点击到顶层文件夹下，则不需要显示文件夹路径，并且文件夹路径需清空
            $("#folderShowPath").html("");
            $("#folderUl").hide();
            $("#headFolderUl").show();

        }
    }

    /**
     *拼接文件路径并显示查询
     * @param folderNamePath 路径集合
     * @param folderIds 文件ID集合
     * @param folderId 最后的文件ID
     */
    function showFilePathList(folderNamePath, folderIds, folderId, partyId){
        if(folderNamePath != null && folderIds != null && folderNamePath != "" && folderIds != ""){
            $("#folderPathHid").val("");
            folderNamePath = folderNamePath.substring(0,folderNamePath.length - 1);
            folderIds = folderIds.substring(0,folderIds.length - 1);
            var folder = folderNamePath.split("/");
            var folderIdList = folderIds.split(";");
            if(folder.length == folderIdList.length){
                $("#folderId").val(folderId);
                var folderPathHid = "";
                for(var i = 0; i < folder.length; i++){
                    var folderid = folderIdList[i];
                    var foldername = folder[i];
                    folderPathHid += foldername + "~" + folderid + "?" + partyId + "|";
                }

                $("#folderPathHid").val(folderPathHid);
                ajaxSearchFiles(folderId,partyId);
            }
        }
    }

    /**
     * 上一层：点击返回上次浏览目录
     */
    function returnUp(){
        var folderPathHid = $("#folderPathHid").val();//隐藏的连续文件夹名称和对应的ID
        folderPathHid = folderPathHid.substring(0,folderPathHid.length - 1);
        var folderId = "";
        var party = "";
        var name = "";
        //如果文件夹路径中存在|，则表示不是顶层文件夹，需检索该上层文件夹信息。如果没有则表示是顶层文件夹
        if(folderPathHid.indexOf("|") >= 0){
            var subfolder = folderPathHid.substring(0, folderPathHid.lastIndexOf("|"));
            if(subfolder.indexOf("|") >= 0) {
                //如果还存在|则表示还有上层文件夹，需要对文件夹名称和ID进行处理
                $("#folderPathHid").val(subfolder);
                folderId = subfolder.substring(subfolder.lastIndexOf("~") + 1,subfolder.lastIndexOf("?"));
                party = subfolder.substring(subfolder.lastIndexOf("?") + 1,subfolder.length);
                name = subfolder.substring(subfolder.lastIndexOf("|") + 1,subfolder.lastIndexOf("~"));
                showFileList(folderId, name, party);
            }else{
                //进入else表示只有一层文件夹，直接取得文件信息
                $("#folderPathHid").val("");
                folderId = subfolder.substring(subfolder.lastIndexOf("~") + 1,subfolder.lastIndexOf("?"));
                party = subfolder.substring(subfolder.lastIndexOf("?") + 1,subfolder.length);
                name = subfolder.substring(0,subfolder.lastIndexOf("~"));
                showFileList(folderId, name, party);
            }
        }else{
            //如果没有顶层文件夹，则要将folderPathHid清空
            $("#folderPathHid").val("");
            var deId = $('#defaultFolder').val();
            setDefault(deId,"");
        }
        if(folderId == null || folderId == ""){
            folderId = $("#defaultFolder").val();
        }
        $("#folderId").val(folderId);
    }

    function downloadCheckFile() {
        var files = "";
        $("input[name^='fileCheck_']:checked").each(function(){
            var fileId = $(this).val();
            files += fileId + ",";
        })
        if(files != "" && files.length > 0){
            files = files.substring(0, files.length - 1);
            downloadFile(files);
        }else{
            showInfo("请选择文件进行下载！");
        }
    }

    function fileChecked(){
        if ($("#allFileCheckBox").attr("checked")) {
            $(":checkbox").attr("checked", true);
        } else {
            $(":checkbox").attr("checked", false);
        }
    }

    function setFilePath(){
        var hidPath = $("#folderPathHid").val();
        var fullPath = "/";
        if(hidPath != ""){
            hidPath = hidPath.substring(0,hidPath.length - 1);
            var paths = hidPath.split("|");
            for(var i = 0; i < paths.length; i++){
                var file = paths[i];
                fullPath += file.substring(0, file.indexOf("~")) + "/";
            }
        }
        return fullPath;
    }
    $("#searchForm").validationEngine("attach", {promptPosition: "bottomLeft"});

    function searchFileListByName() {
        var validation = $('#searchForm').validationEngine('validate');
        if(validation) {
            var fileName = $("#fileName").val();
            var folderId = $("#folderId").val();
            var rootId = $("#defaultFolder").val();
            if (fileName != null && fileName != "") {
                displayInLayer('文档搜索', {
                    requestUrl: '/ckfinder/control/searchFileListByName?externalLoginKey=${externalLoginKey}',
                    data: {fileName: fileName, folderId: folderId, rootFolderId: rootId},
                    width: '1000px',
                    height: '630px',
                    shade: 0
                })
            }
        }else {
            return validation;
        }
    }
</script>
<#--选择按钮-->
<div id="	" class="col-xs-12">
    <div style="color:#FFF;float:right;" id="newFileCreate">
        <input type="text" name="createFile" id="createFile" data-size-limit="${fileSize}" style="width:1px;height:1px;">
    <#--<span class="btn red btn-outline btn-file" id="createFile" onclick="showUploadFilePage()" style="margin: 5px">
        <span class="fileinput-new"> 上传文件 </span>
    </span>-->
        <span class="btn red btn-outline btn-file" id="createFolder" onclick="addRow()" style="margin: 5px;display:none">
            <span class="fileinput-new"> 新建文件夹 </span>
        </span>
        <span class="btn red btn-outline btn-file" id="downloadFiles" onclick="downloadCheckFile()" style="margin: 5px;display:none">
            <span class="fileinput-new"> 下载 </span>
    </span>
    </div>
<#--文件路径-->
    <div class="col-sm-6 col-md-8">
        <ul class="folder-level" id="headFolderUl">
            <li>
                <i class="fa fa-home" style="font-size:18px" title="根目录"></i>
            </li>
        </ul>
        <ul class="folder-level" style="display:none;" id="folderUl">
            <li>
                <a href="javascript:returnUp();">返回</a>
                <span>></span>
                <a href="javascript:setDefault($('#defaultFolder').val());"  class="fa fa-home" style="font-size:18px" title="根目录"></a>
            </li>
            <li style="margin-left: 5px;" id="folderShowPath">
            </li>
        </ul>
    </div>
<#--文件列表-->
    <table id="example" class="table table-striped table-hover order-column dataTable no-footer" cellspacing="0" style="width: 100%">
        <thead>
        <tr style="background: #f7f7f7;border-radius: 2px;border: 1px solid #d2d2d2;color: #888;overflow: hidden;">
            <th style="width:40px;padding: 0 0 0 5px;vertical-align: middle">序号</th>
            <th align="center" style="width:50px"><input type="checkbox" name="allFileCheckBox" onclick="fileChecked()" id="allFileCheckBox"></th>
            <th>文件名</th>
            <th style="width:150px">创建人</th>
            <th style="width:135px">版本号</th>
            <th style="width:100px">创建时间</th>
        </tr>
        </thead>
        <tbody id="fileTable">
        </tbody>
    </table>
<#--提示框-->
    <div id="addFolder" style="display: none;" title="新建文件夹">
        <fieldset>
            <label for="name">名称：</label>
            <input type="text" name="folderName" id="folderName" maxlength="20" class="text ui-widget-content ui-corner-all">
            </br>
            <label for="name">备注：</label>
            <input type="text" name="foldeRemarks" id="foldeRemarks" maxlength="50"  style="width:80%;" class="text ui-widget-content ui-corner-all">
        </fieldset>
    </div>
    <div id="editFile" style="display: none;" title="重命名">
        <fieldset>
            <label for="name">请输入新名称：</label>
            <input type="text" style="width: 60%;" name="newFileName" id="newFileName" maxlength="20"  class="text ui-widget-content ui-corner-all">
            <span id="fileSuffix"></span>
            <input type="text" name="newfoldeRemarks" id="newfoldeRemarks" maxlength="50"  style="width:80%;" class="text ui-widget-content ui-corner-all">
        </fieldset>
    </div>
<#--隐藏域-->
    <input type="hidden" id="folderId">
    <input type="hidden" id="folderPathHid">
    <input type="hidden" name="defaultFolder" id="defaultFolder">
    <iframe frameborder="0" scrolling="no" style="background-color:transparent;position: absolute;z-index: -1;width: 100%;height: 100%; top: 0;left:0;"></iframe>
</div>