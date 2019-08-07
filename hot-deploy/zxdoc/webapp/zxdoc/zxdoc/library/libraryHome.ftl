<link href="${request.contextPath}/static/case-home.min.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.min.js"></script>-->
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/zxdoc/static/jsjaccore/JSJaC.js"></script>
<script type="text/javascript" src="/zxdoc/static/chatcore.js?t=20171101"></script>
<script type="text/javascript" src="/zxdoc/static/caseChat.js?t=20171021"></script>
<style type="text/css">
    .btn-group.library-type label {
        margin-left: 10px !important;
    }

    .   library-index {
    }

    .library-index > div {
        margin: 5px;
        padding-bottom: 10px;
        border-bottom: 1px solid lightgrey;
    }

    .library-index .files div {
        padding: 5px;
    }

    .library-index img {
        vertical-align: bottom;
    }

    .author-text {
        display: inline-block;
    }

    .author-text div {
        padding: 5px;
    }

    .author-img {
        display: inline-block;
        padding-left: 5px;
    }
</style>
<#--查询资料--点击刷新功能-->
<script type="text/javascript">
    function searchFile(type) {
//        location.href = "LibraryHome?type=" + type;
        closeCurrentTab();
        displayInside("LibraryHome?type=" + type);
    }
    function listDocument(userLoginId, type) {
        closeCurrentTab();
        displayInside("LibraryList?userLoginId=" + userLoginId + "&type=" + type);
    }
    //打开即时聊天
    function openLibraryChat(id)
    {
        closeCurrentTab();
       $.ajax({
           url:"checkIsExist",
           type:"POST",
           data:{"goalId":id},
           dataType:"json",
           success: function (data) {
                if(data.data=="no")
                {
                    createOpenfire(id);
                }else {
                    top.sendInvite(data.goalName, data.chatRoomJID, data.chatRoomName);
                    top.sendInvite(data.username, data.chatRoomJID, data.chatRoomName);
                    openInstantMessages(data.chatRoomName, data.chatRoomJID);
                }
           }
       })
    }

    function createOpenfire(id)
    {
        closeCurrentTab();
        //先一步创建一个协作主题，然后在打开
        $.ajax({
            url:"createInstantMessage",
            type:"POST",
            data:{"goalId":id},
            dataType:"json",
            success: function (data) {
                if(data.data == "创建成功")
                {
                    /*for(var i = 0; i < data.members.length; i ++){
                        top.sendInvite(data.members[i], data.chatRoomJID, data.chatRoomName);
                    }*/
                    //获取聊天室的标识
                    var chatRoomJID = data.chatRoomJID;
                    var chatRoomName = data.chatRoomName;
                    //创建成功时，打开聊天室
                    if(chatRoomJID) {
                        sendInvite(data.goalName, data.chatRoomJID, data.chatRoomName);
                        openInstantMessages(chatRoomName, chatRoomJID);
                    }
                }else{
                    showError("未知的错误");
                }
            }
        })
    }

    function searchFileListByName() {
        var fileName = $("#fileName").val();
        var dataType = $("#libraryDataType").val();
        if (fileName != null && fileName != "") {
            displayInLayer('文档搜索', {
                requestUrl: 'searchLibraryFileListByName',
                data: {fileName: fileName,libraryDataType:dataType,isLibrary:"Y"},
                width: '1000px',
                height: '630px',
                shade: 0
            })
        }
    }
</script>
<script type="text/javascript">

</script>
<div class="portlet light">
    <div class="portlet-title">
        <div>
            <div class="btn-group library-type" data-toggle="buttons">
                <input type="hidden" id="libraryDataType" value="${parameters.libraryData.type?default('')}">
                <label <#if parameters.libraryData.type?default('')=="">class="btn blue active"
                       <#else>class="btn blue"</#if> onclick="searchFile('')">
                    <input type="radio" class="toggle"> 全部 </label>
            <#if parameters.libraryData.listEnum?has_content>
                    <#list parameters.libraryData.listEnum as list1>
                        <label <#if parameters.libraryData.type?default('')=="${list1.enumId?default('')}">class="btn blue active"
                               <#else>class="btn blue"</#if> onclick="searchFile('${list1.enumId?default('')}   ')">
                            <input type="radio" class="toggle"> ${list1.description?default('')} </label>
                    </#list>
                </#if>
            </div>
            <#include "component://ckfinder/webapp/ckfinder/searchFileBut.ftl"/>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
        <#if parameters.libraryData.resultMap?has_content>
            <#list parameters.libraryData.resultMap.keySet() as myKey>
                <#assign partyInfo = parameters.libraryData.partyInfoByUserLogin[myKey]>
                <div class="col-xs-6 col-md-3 library-index" style="<#if myKey?index%4 ==0>clear:left</#if>">
                    <div>
                        <div>
                            <div class="author-img">
                                <img class="avatar" height="80px" style="width: 80px" alt="" src="/content/control/partyAvatar?partyId=${partyInfo.partyId}&externalLoginKey=${externalLoginKey}"/>
                            </div>
                            <div class="author-text">
                                <div>${partyInfo.fullName?default(partyInfo.groupName!)}&nbsp;<#if partyInfo.statusId == 'PARTY_IDENTIFIED'><i class="font-green fa fa-vimeo" title="已认证"></i></#if>&nbsp;<#if partyInfo.ishide == 'false'><i class="glyphicon glyphicon-comment font-blue" style="cursor: pointer" onclick="openLibraryChat('${partyInfo.partyId}')"></i></#if></div>
                                <div class="font-blue">注册会计师&nbsp;<i class="fa fa-thumbs-o-up"></i></div>
                                <a href="javascript:listDocument('${myKey?default('')}','${parameters.libraryData.type?default('')}');"
                                   class="font-red">${parameters.libraryData.resultMap[myKey]?size}
                                    <span class="fong-grey">篇文档</span></a>
                            </div>
                        </div>
                        <div class="files">
                            <#assign leftEmptyDivNum = 3>
                            <#list parameters.libraryData.resultMap[myKey] as list>
                                <#assign nameLength = StringUtil.xmlDecodedLength(list.dataResourceName!)>
                                <div>
                                    <#if list.dataResourceName?index_of(".")!=-1>
                                        <#assign fileType = (list.dataResourceName?split("."))[list.dataResourceName?split(".")?size-1] >
                                        <#if fileType=="doc" || fileType=="docx">
                                            <i class="font-green fa fa-file-word-o"></i>
                                        <#elseif fileType=="xls" || fileType=="xlsx">
                                            <i class="font-green fa fa-file-excel-o"></i>
                                        <#elseif fileType=="gif" || fileType=="png" || fileType=="bmp" || fileType=="jpg" || fileType=="jpeg">
                                            <i class="font-green fa fa-file-image-o"></i>
                                        <#elseif fileType=="zip">
                                            <i class="font-green fa fa-file-zip-o"></i>
                                        <#elseif fileType=="ppt" || fileType=="pptx">
                                            <i class="font-green fa fa-file-powerpoint-o"></i>
                                        <#elseif fileType=="pdf">
                                            <i class="font-green fa fa-file-pdf-o"></i>
                                        <#else>
                                            <i class="font-green fa fa-file-text-o"></i>
                                        </#if>
                                    <#else>
                                        <i class="font-green fa fa-file-text-o"></i>
                                    </#if>
                                    <a href="javascript:viewPdfInLayer('${list.dataResourceId?default('')}')"
                                       <#if nameLength gt 12>title="${list.dataResourceName!}"</#if>>
                                        <#if nameLength gt 12>
                                        ${StringUtil.xmlDecodedSubstring(list.dataResourceName!, 0, 12)}
                                        <#else>
                                        ${list.dataResourceName!}
                                        </#if>
                                    </a>
                                    <#--<a style="text-decoration:none" href="/content/control/downloadUploadFile?dataResourceId=${list.dataResourceId?default('')}&externalLoginKey=${externalLoginKey?default('')}" title="下载"><i class="fa fa-download"></i> </a>-->
                                    &nbsp;<a style="text-decoration:none" href="javascript:searchScore('${list.id?default('')}')" title="下载"><i class="fa fa-download"></i> </a>
                                </div>
                                <#assign leftEmptyDivNum = leftEmptyDivNum - 1>
                                <#if list_index==2><#break></#if>
                            </#list>
                            <#list 0 .. leftEmptyDivNum as i>
                                <#if i < leftEmptyDivNum>
                                <div>&nbsp; </div>
                                </#if>
                            </#list>
                        </div>
                    </div>
                </div>
            </#list>
        </#if>
        </div>
    </div>
</div>