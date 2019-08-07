<script type="text/javascript" src="/images/lib/mustache/mustache.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/jsjaccore/JSJaC.js"></script>
<script type="text/javascript" src="/zxdoc/static/chatcore.js?t=20171102"></script>
<script type="text/javascript" src="/zxdoc/static/caseChat.js?t=20171120"></script>
<script type="text/javascript" src="/zxdoc/static/OfficeControlFunctions.js?t=20161208"></script>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<#--<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" />-->
<#--<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>-->
<#--<#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>-->
<#--<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>-->
<style type="text/css">
    html, body, body > .normal-bar, .normal-bar > .contentarea, #column-container, #content-main-section, #content-main-section > div, #content-main-section > div > .portlet.light
    , #content-main-section > div > .portlet.light > .portlet-body, #content-main-section > div > .portlet.light > .portlet-body > .row
    ,#chatWindow, #chatWindow > .portlet
    ,#docWindow, #docWindow > .portlet{
        height:100%;
    }
    #chatWindow > .portlet, #docWindow > .portlet{
        position:relative;
    }
    #chats, #files, #members, #chatsHistory, #workspace{
        position: absolute;
        top: 50px;
        bottom: 0;
        left: 0;
        right: 0;
        padding-top:0;
    }

    #ntko-container{
        height: 100%;
        clear:left;
        z-index:-100;
        position: relative
    }
    .file-name{
        color:#6699FF;
        position: absolute;
        padding: 2px;
        font-size:11px;
        bottom:0;
    }
    table.altrowstable {
        font-family: verdana,arial,sans-serif;
        font-size:11px;
        border-width: 1px;
        border-color: #a9c6c9;
        border-collapse: collapse;
        margin-top: 10px;
    }
    table.altrowstable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }
    table.altrowstable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }

    a.delClass{
        color: red;
    }

    .portlet.light>.portlet-title>.actions{
        padding: 5px 0 5px;
    }

    .portlet.light.bordered{
        padding: 5px 10px 0;
        margin-bottom: 0;
    }
</style>
<#assign fileEditLimitSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("zxdoc.properties", "chatFileEditSizeLimitM")>
<script id="officeEdit" type="text/html">
    <div class="media" style="margin: 5px" id="{{fileId}}">
        <div class="media-left file-mod">
            <div class="file-icon-{{fileType}}"></div>
        </div>
        <div class="media-body" style="position:relative">
            <h4 class="media-heading"><a href="#nowhere" fileNameId="{{fileId}}" onclick="viewFile('{{fileId}}',false)">{{fileName}}</a></h4>
            <span style="color:#9eacb4">版本: {{fileVersion}} {{#fileUpdateInfo}} | {{fileUpdateInfo}}{{/fileUpdateInfo}} {{#fileLocker}} | 当前由{{fileLocker}}锁定编辑{{/fileLocker}} </span>
            <span style="position:absolute;right:0; bottom:10px">
            {{#fileSizeOk}}
            {{#editable}}
            <button style="display: none;float:right" type="button" class="btn red btn-md sbold" title="协作区编辑模式打开文件"
                    onclick="viewEdit('{{fileId}}')">编辑
            </button>
            {{/editable}}
            {{#unlockable}}
            <button style="display: none;float:right" type="button" class="btn yellow btn-md sbold" title="直接解锁文件"
                    onclick="unlockEditFile('{{fileId}}','{{fileName}}')">解锁
            </button>
            {{/unlockable}}
            {{#joinable}}
            <button style="display: none;float:right" type="button" class="btn green btn-md sbold" title="协作区只读模式打开文件"
                    onclick="joinFileEdit('{{fileId}}')">协作
            </button>
            {{/joinable}}
            {{/fileSizeOk}}
            {{#fileSizeNotOk}}
                <span class="fa fa-warning" style="color:red;font-size:20px;float:right" title="文件大于${fileEditLimitSize?default("5")}M不能协作编辑"></span>
            {{/fileSizeNotOk}}
            </span>
        </div>
    </div>
</script>
<script id="imageView" type="text/html">
    <div class="media" style="margin: 5px" id="{{fileId}}">
        <div class="media-left file-mod">
            <div class="file-icon-img"></div>
        </div>
        <div class="media-body">
            <h4 class="media-heading">
                <a class="attachment-image" href="/content/control/imageView?fileName={{fileId}}&externalLoginKey=${externalLoginKey}">
                    {{fileName}}
                </a>
            </h4>
        </div>
    </div>
</script>
<script id="pdfView" type="text/html">
    <div class="media" style="margin: 5px" id="{{fileId}}">
        <div class="media-left file-mod">
            <div class="file-icon-pdf"></div>
        </div>
        <div class="media-body">
            <h4 class="media-heading">
                <a href="#nowhere" onclick="viewPdfInLayer('{{fileId}}')">{{fileName}}</a>
            </h4>
        </div>
    </div>
</script>
<script type="text/javascript">
    function viewPdfInLayer(fileId){
        displayInLayer("文件预览", {
            requestUrl: "/content/control/FileHandler?dataResourceId=" + fileId + "&externalLoginKey=" + getExternalLoginKey(),
            height:'90%',respectHeight:true,layer: {
                maxmin: false, zIndex: 99999999,
                success:function(){
                    hideNtko();
                },
                end:function(){
                    showNtko();
                },
                cancel: function(index, layero){
                    var prepareLeave = layero.find("iframe")[0].contentWindow.prepareLeave;
                    if(prepareLeave){
                        prepareLeave();
                    }
                }
            }
        })
    }

    function showChatImage(id){
        getLayer().open({
            type: 1,
            content: $("<div />").append($("<div style='text-align:center;background: white;'/>").append($(id).clone().show())).html(),
            area:['80%','80%'],
            success:function(layero, index){
                hideNtko();
                getLayer().iframeAuto(index);
            },
            end:function(){
                showNtko();
            }
        });
    }

    function activateNtko(){
        if(OFFICE_CONTROL_OBJ){
            OFFICE_CONTROL_OBJ.Activate();
        }
    }
    function hideNtko(){
//        $("#ntko-container").hide();
        $("#ntko-container-shade").show();
    }
    function showNtko(){
//        $("#ntko-container").show();
        $("#ntko-container-shade").hide();
    }
    <#if roomMembers?has_content>
        <#list roomMembers as roomMember>
        memberNames['${roomMember["partyId"]!}'] = '${roomMember["fullName"]!}';
        </#list>
    </#if>

    function enterChatRoom(){
        joinChatRoom($("#chatWithJID").val());
    }
    //加入讨论组
    function joinChatRoom(roomJID) {
        $("#liveChat").empty();//重新进入聊天室，历史消息将重新接收
        $("#chatTheme").text("");
        $("#roomName").val("");
        var p = new JSJaCPresence();
        p.setTo(roomJID + "/" + top.oArgs.username);
        //p.setFrom(oArgs.username + '@' + oArgs.domain + '/' + oArgs.username);
        p.setXNode('');
        p.setShow('chat');
        top.con.send(p);
        // online();
        listMembers(roomJID);
        return false;
    }

    var IsFileOpened = false;  //当前是否有文档打开
    var thisDocumentId = ""; //当前文档的id
    $(function () {
        /*$("#uploadCaseFile").attachment({
            showLink: false,
            wrapperClass: 'btn',
            url: '/ckfinder/control/action?command=fileUpload&jsonResponse=true&currentFolder=${caseFolderId?default('1')}&fileSharePartyId=${fileSharePartyId}&otherModules=1',
            fineUploader:{
                onTruncateComplete: function (data) {
                    var fileRes = data.Connector.Files.file, fileId = fileRes.fileId, fileName = fileRes.fileName, chatWithJID = $("#chatWithJID").val();
                    $.ajax({
                        type: 'POST',
                        url: "addChatFiles",
                        async: false,
                        dataType: 'json',
                        data:{chatRoomId: chatWithJID, dataResourceIds: fileId},
                        success: function (data) {
                            sendChatMessage("FILE*UPLOADED:" + fileId + ":" + fileName, chatWithJID);
                        }
                    });
                }
            }
        });*/
        $("#liveChat").on("click", ".attachment-image", function(){
            var $this = $(this), imgId = $this.attr("href");
            showChatImage(imgId);
        });
        $("#historyChats").on("click", ".attachment-image", function(){
            var $this = $(this), imgId = $this.attr("href");
            showChatImage(imgId);
        });
        $("#chat-actions").on("click", ".btn", function () {
            var $this = $(this), sectionId = $this.data("sectionId");
            $this.siblings().removeClass("green").addClass("grey");
            $this.parent().find("i.font-blue").removeClass("font-blue");
            $this.removeClass("grey").addClass("green");
            $this.find("i").addClass("font-blue");
            $(".chat-portlet .portlet-body").hide();
            $("#" + sectionId).show();
            $this.closest(".portlet-title").find(".caption-subject").text($this.attr("title"));
        });
        changeEditorHeight();
        //登录openfire
//        init($("#httpBase").val(), $("#jabberServer").val(), $("#userLoginId").val(), $("#password").val(), false, $("#chatWithJID").val());
        enterChatRoom();
        $.getScript("/zxdoc/static/ntkoofficecontrol.js?t=" + (new Date()).getTime(), function (data, textStatus, jqxhr) {
        });
        //TODO 演示用图片，演示完成删除
        /*   $(".demoPicture").fancybox({
               //'type': 'iframe',
               'padding': 0,
               'overlayShow': false,
               'transitionIn': 'elastic',
               'transitionOut': 'elastic',
           });*/


        $('#moreparams').hide();

        $('#captureselectSize').click(function () {
            var autoFlag = $("#captureselectSize").attr("checked") == "checked" ? 1 : 0;
            if (autoFlag == 1) {
                $('#moreparams').show();
            }
            else {
                $('#moreparams').hide();
            }
        });
        $('#getimagefromclipboard').click(function () {
            $('#posdetail').hide();
        });
        $('#showprewindow').click(function () {
            $('#posdetail').hide();
        });
        $('#fullscreen').click(function () {
            $('#posdetail').hide();
        });
        $('#specificarea').click(function () {
            $('#posdetail').show();
        });

        var isManage = $("#isManage").val();
        if(isManage=="no")
        {
            $("#addNewMember").remove();
        }

        $('#showprewindow').click();
        $('#autoupload').click();
        $('#btnUpload').hide();
    <#assign captureAuthKey = Static["org.ofbiz.base.util.UtilMd5"].generateCaptureAuthKey(clientDate!)>
        InitCapture('${captureAuthKey?default("")}');
    <#--InitCapture(${captureAuthKey?default("")});-->
        $(window).on('beforeunload', function() {
            if(OFFICE_CONTROL_OBJ){
                OFFICE_CONTROL_OBJ.Close();
            }
            return true;
        });
    })
    //显示、隐藏工具栏，若有需求可调用该方法
    function showOrHide() {
        OFFICE_CONTROL_OBJ.toolbars = !OFFICE_CONTROL_OBJ.toolbars;
    }
    /**
     * 检查控件区域文件状态
     * 无文档打开返回0
     * 只读模式文档返回1
     * 编辑模式文档返回2
     * */
    function checkFileControlStatus(){
        var control = document.all("TANGER_OCX"), doc = control.ActiveDocument, toolbars = control.toolbars;
        if(doc){
            if(toolbars){
                return 2
            }else{
                return 1;
            }
        }else{
            return 0;
        }
    }
    /**
     * 改变文档锁定状态
     * @param lockedBy 由谁锁定（如果是解锁则传空）
     * @param fileId 文档id
     * @param successCall 状态改变成功时操作
     * @param failCall 状态改变失败时操作
     */
    function changeFileStatus(lockedBy, fileId, successCall, failCall) {
        $.ajax({
            type: 'post',
            url: 'ChangeFileStatus',

            data: {'lockedBy': lockedBy, 'fileId': fileId},
            dataType: 'json',
            success: function (content) {
                var data = content.data || {};
                if(data.changeResult === "true"){
                    if(successCall){
                        successCall.call(this, fileId);
                    }
                }else{
                    showError(data.changeResult);
                    if(failCall){
                        failCall.call(this, fileId);
                    }
                }
            }
        })
    }
    /**
     * 文档列表的编辑按钮
     * @param fileId 文档id
     * @param isRefresh 是否是刷新操作（如果由该方法打开的文档则刷新时也会调用该方法）
     * @returns {boolean}
     */
    function viewEdit(fileId, isRefresh) {
        var controlSt = checkFileControlStatus();
        if(controlSt == 2){
            hideNtko();
            var confirmIndex = getLayer().confirm("是否放弃对当前文档的更改?", {
                btn: ['放弃', '取消'],cancel: function(index, layero){
                    showNtko();
                }
            }, function () {
                if(OFFICE_CONTROL_OBJ){
                    OFFICE_CONTROL_OBJ.Close();
                }
                hideNtko();
                layer.confirm("是否推送给其他讨论组成员", {
                    btn: ["是", "否"],closeBtn:0,cancel: function(index, layero){
                        showNtko();
                    }
                }, function(index) {
                    showNtko();
                    sendChatMessage("OPEN*FILE*INSTRUCTION*RECEIVED:" + fileId + ":" + true, $("#chatWithJID").val());
                    layer.close(index);
                    lockFileAndEdit(fileId);
                }, function(index) {
                    showNtko();
                    layer.close(index);
                    lockFileAndEdit(fileId);
                })
                getLayer().close(confirmIndex);
            }, function(index) {
                showNtko();
                layer.close(index);
            })
        }else{
            hideNtko();
            layer.confirm("是否推送给其他讨论组成员", {
                btn: ["是", "否"],closeBtn:0,cancel: function(index, layero){
                    showNtko();
                }
            }, function(index) {
                showNtko();
                sendChatMessage("OPEN*FILE*INSTRUCTION*RECEIVED:" + fileId + ":" + true, $("#chatWithJID").val());
                layer.close(index);
                lockFileAndEdit(fileId);
            }, function(index) {
                showNtko();
                layer.close(index);
                lockFileAndEdit(fileId);
            })
        }
    }
    <#assign hostUrl = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "host-url")>
    /**
     * 在协作右侧控件区域打开文档
     *  * @param fileId 文档id
     *  * @param edit 是否写模式
     * */
    function lockFileAndEdit(fileId){
        //锁定该文档
        changeFileStatus(oArgs.username, fileId, function(){
            sendChatMessage("正·在·编·辑·文·档" + "*" + fileId + "*" + $("a[fileNameId=" + fileId + "]").html(), $("#chatWithJID").val());
            //编辑时解除onclick事件
            $("#editFile").off("click");
            $("#editFile").attr("disabled",true);
            $("#editFile").attr("class", "btn btn-md grey btn-outline");
            //编辑时为保存和保存并解锁按钮绑定事件
            $("#saveChangedFile").off("click").on("click", saveChangedFile);
            $("#saveChangedFile").attr("disabled",false);
            $("#saveChangedFile").attr("class", "btn btn-md green btn-outline");
            $("#saveAndUnlock").on("click", saveAndUnlock);
            $("#saveAndUnlock").attr("disabled",false);
            $("#saveAndUnlock").attr("class", "btn btn-md green btn-outline");
            $("#refreshFile").attr("class", "btn btn-md green btn-outline");

            loadFileInControl(fileId, true);
        });
    }

    function forbidSave(obj) {
        $(obj).attr("disabled",true);
        $(obj).attr("class","btn btn-md grey btn-outline");
        setTimeout(function(){
            $(obj).attr("disabled",false);
            $(obj).attr("class","btn btn-md green btn-outline");
        },10000);
    }


    /**
     * 参与协作
     * */
    function joinFileEdit(fileId){
        var controlSt = checkFileControlStatus();
        if(controlSt == 2){
            hideNtko();
            var confirmIndex = getLayer().confirm("是否放弃对当前文档的更改?", {
                btn: ['放弃', '取消'],cancel: function(index, layero){
                    showNtko();
                }
            }, function () {
                getLayer().close(confirmIndex);
                loadFileInControl(fileId, false);
            }, function(index) {
                showNtko();
                layer.close(index);
            })
        }else{
            loadFileInControl(fileId, false);
        }

    }
    function loadFileInControl(fileId, edit){
        showNtko();
        if(OFFICE_CONTROL_OBJ){
            try {
                OFFICE_CONTROL_OBJ.Close();
                console.log('document closed');
            }catch(e){

            }
        }
        $(".file-name").html("协作文件: 《" + $("a[fileNameId=" + fileId + "]").html() + "》");

        //如果有办法判断当前打开的文件版本与即将打开的文件版本是否一致，可以优化是否有必要重新打开
//        if(!thisDocumentId || thisDocumentId != fileId || OFFICE_CONTROL_OBJ.toolbars != edit){//当前无打开文档或是不一样的文档或者同一份文档但编辑模式不一致
    <#outputformat "UNXML">
        intializePage('${hostUrl}content/control/imageView?fileName=' + fileId + '&externalLoginKey=' + getExternalLoginKey());
    </#outputformat>
        thisDocumentId = fileId;
        OFFICE_CONTROL_OBJ.SetReadOnly(!edit);
        OFFICE_CONTROL_OBJ.toolbars = edit;
//        }
    }


    /**
     * 预览文档：只读模式，弹出框打开，便于打开多个文档用于参照
     * @param fileId 文档id
     * @param isLocked 是否锁定
     * @param isFromOthers 是否由他人推送
     * @param isRefresh 是否是刷新操作（由该方法打开的文档刷新时也会调用该方法）
     * @returns {boolean}
     */
    function viewFile(fileId, isLocked, isFromOthers, isRefresh) {
        viewPdfInLayer(fileId);
    }
    /**
     * 编辑文档
     */
    function editFile() {
        lockFileAndEdit(thisDocumentId);
    }
    /**
     * 保存文档
     * @param unlock 是否解锁（如果是保存并解锁操作则会传递该参数）
     */
    function saveChangedFile(event, unlock) {
        var fileId = thisDocumentId;
    <#outputformat "UNXML">
        var url = '${hostUrl}content/control/FineUploader?externalLoginKey=' + getExternalLoginKey() + "&overwrite=true&fileId=" + fileId;
    </#outputformat>
        if(!unlock){
            window.OnComplete2 = function(){
                sendChatMessage($("#userLoginId").val() + "已·完·成·对·文·档·的·编·辑" + "*" + fileId, $("#chatWithJID").val());
            }
        }
        var tempFileName = saveFileToUrl(url, fileId);
        return tempFileName;
    }
    function OnComplete2(){
        console.log("document saved");
    }
    /**
     * 保存并解锁
     */
    function saveAndUnlock(event) {
        window.OnComplete2 = function(){//定义文档保存后回调内容
            //解锁
            changeFileStatus("", thisDocumentId, function(){
                //保存并解锁后置文档为预览状态并解除保存相关的事件
                OFFICE_CONTROL_OBJ.toolbars = false;
                OFFICE_CONTROL_OBJ.SetReadOnly(true);
                $("#saveChangedFile").off("click");
                $("#saveChangedFile").attr("disabled",true);
                $("#saveChangedFile").attr("class", "btn btn-md grey btn-outline");
                $("#editFile").attr("disabled",false);
                $("#editFile").on("click", editFile);
                $("#editFile").attr("class", "btn btn-md green btn-outline");
                $("#saveAndUnlock").off("click");
                $("#saveAndUnlock").attr("disabled",true);
                $("#saveAndUnlock").attr("class", "btn btn-md grey btn-outline");
                sendChatMessage($("#userLoginId").val() + "保·存·并·解·锁·了·文·档" + "*" + thisDocumentId, $("#chatWithJID").val());
            });
        }
        //保存
        saveChangedFile(event, "unlock");
    }
    /**
     * 解锁
     */
    function unlockEditFile(fileId, fileName) {
        //解锁
        changeFileStatus("", fileId, function(){
            sendChatMessage($("#userLoginId").val() + "保·存·并·解·锁·了·文·档" + "*" + fileName, $("#chatWithJID").val());
        });
    }
    /**
     * 刷新操作
     * @returns {boolean}
     */
    function refreshFile(){
        var fileId = thisDocumentId, fileControlSt = checkFileControlStatus();
        if(fileControlSt == 2){//编辑模式下
            hideNtko();
            var confirmIndex = getLayer().confirm("是否放弃对当前文档的更改?", {
                btn: ['放弃', '取消'],cancel: function(index, layero){
                    showNtko();
                }
            }, function () {
                loadFileInControl(fileId, true);
                getLayer().close(confirmIndex);
            }, function(index) {
                showNtko();
                layer.close(index);
            })
        }else{//保存并解锁后处于只读模式
            loadFileInControl(fileId, false);
        }
    }
    function changeEditorHeight(editorHeight){
        if(!editorHeight){
            editorHeight = $(window).height() * 0.7;
        };

        $("#chats .scroller").height($("#chats").height() - 60);
        /*$("#files").height(editorHeight + 70);
        $("#fileList").height(editorHeight + 20);
        $("#members").height(editorHeight + 70);
        $("#chatsHistory").height(editorHeight + 70);
        $("#hisDiv").height(editorHeight - 8);
        $("#ntko-container").height(editorHeight + 70);*/
        initSlimScroll(".scroller", {start: "bottom", refresh : true});
    }

    function changeSize(location) {
        if (location == "left") {
            /*if ($("#chatWindow").attr("class") == "col-md-6 col-sm-6") {
                $("#chatWindow").attr("class", "col-md-12 col-sm-12");
                $("#docWindow").attr("class", "col-md-6 col-sm-6 hide");
            } else if ($("#chatWindow").attr("class") == "col-md-12 col-sm-12") {
                $("#chatWindow").attr("class", "col-md-6 col-sm-6");
                $("#docWindow").attr("class", "col-md-6 col-sm-6");
            }*/
        } else if (location == "right") {
            if ($("#docWindow").attr("class") == "col-md-6 col-sm-6") {
                $("#docWindow").attr("class", "col-md-12 col-sm-12");
                $("#chatWindow").attr("class", "col-md-6 col-sm-6 hide");
            } else if ($("#docWindow").attr("class") == "col-md-12 col-sm-12") {
                $("#chatWindow").attr("class", "col-md-6 col-sm-6");
                $("#docWindow").attr("class", "col-md-6 col-sm-6");
            }
        }
    }
//     function uploadCaseFile(){
//         hideNtko();
//         var openLayer = getLayer();
//         openLayer.confirm('请选择文件来源', {
//                     btn: ['CASE文档', '企业文档'] //按钮
//                     ,cancel: function(index, layero){
//                         showNtko();
//                     }
//                 }, function (index) {
//                     openLayer.close(index);
//                     showCaseFileUpload('false', '${caseFolderId?default('1')}');
//                 }, function (index) {
//                     openLayer.close(index);
//                     showCaseFileUpload(true, '');
//                 }
//         );
//     }

//     function showCaseFileUpload(readOnly, accessFolderId){
//         var shareFolders = encodeURIComponent('${filePathList}'), chatWithJID = $("#chatWithJID").val();
//         displayInLayer("选择文件", {requestUrl: "/ckfinder/control/OpenFileinputSelection?accessFolderId="+ accessFolderId + "&readOnly=" + readOnly + "&allowLocalUpload=false&fileSharePartyId=${fileSharePartyId}&filePathList=" + shareFolders + "&externalLoginKey=" + getExternalLoginKey(),
//             width:'950px',
//             height:'600px',
// //            zIndex: getLayer().zIndex,
//             layer:{
//                 btn: ['发送'],
//                 success:function(){
//                     hideNtko();
//                 },
//                 end:function(){
//                     showNtko();
//                 },
//                 yes: function(index, layero){
//                     var selectionIframe = layero.parent().find("#layui-layer-iframe" + index).contents(), fileIds=[], fileNames = [];
//                     selectionIframe.find("input[type=checkbox]:checked").each(function(i){
//                         var file = $(this),fileName = file.attr("fileName");
//                         if(file.val() != "on"){
//                             fileIds.push(file.val());
//                             fileNames.push(fileName);
//                         }
//                     });
//                     if(fileIds.length){
//                         $.ajax({
//                             type: 'POST',
//                             url: "addChatFiles",
//                             async: false,
//                             dataType: 'json',
//                             data:{chatRoomId: chatWithJID, dataResourceIds: fileIds.join(",")},
//                             success: function (data) {
//                                 fileIds = data.data.fileIds;
//                                 for(var i in fileIds){
//                                     sendChatMessage("FILE*UPLOADED:" + fileIds[i] + ":" + fileNames[i], chatWithJID);
//                                 }
//                                 closeCurrentTab();//如果设定了yes回调，需进行手工关闭
//                             }
//                         });
//                     }else{
//                         closeCurrentTab();//如果设定了yes回调，需进行手工关闭
//                     }

//                     selectionIframe.find(".fileinput.fileinput-exists").each(function(){
//                         var file = $(this).find("input[type=file]"), fileName = $(this).find(".fileinput-filename").html();
//                         //TODO:临时上传的文件保存后再发消息
//                     });
//                     //TODO:将文件放到文件列表中去
//                 }
//             }
//         });
//     }

	 function uploadCaseFile(){
        hideNtko();
        var openLayer = getLayer();
        openLayer.confirm('请选择文件来源', {
                    btn: ['私有文档', '分享文档'] //按钮
                    ,cancel: function(index, layero){
                        showNtko();
                    }
                }, function (index) {
                    openLayer.close(index);
                    showCaseFileUpload(true, '${partyId?default('')}',1);
                }, function (index) {
                    $("#defaultFolder").val("1");
                    openLayer.close(index);
                    showCaseFileUpload(true, '${partyId?default('')}',2);
                }
        );
    }
	 function uploadCaseFile2(){
	        hideNtko();
	        var openLayer = getLayer();
	        openLayer.confirm('请选择文件来源', {
	                    btn: ['CASE文档'] //按钮
	                    ,cancel: function(index, layero){
	                        showNtko();
	                    }
	                }, function (index) {
	                    openLayer.close(index);
	                    showCaseFileUpload(true, '${partyId?default('')}',3);
	                }
	        );
	    }

    function showCaseFileUpload(readOnly, accessFolderId,id){
        var shareFolders = encodeURIComponent('${filePathList}'), chatWithJID = $("#chatWithJID").val();
        displayInLayer("选择文件", {requestUrl: "/ckfinder/control/OpenFileinputSelection?accessFolderId="+ accessFolderId + "&readOnly=" + readOnly + "&allowLocalUpload=false&defaultFolderId="+id+"&folderId="+id+"&partyId=${parameters.userLogin.partyId}&filePathList=" + shareFolders + "&externalLoginKey=" + getExternalLoginKey(),
            width:'950px',
            height:'600px',
            layer:{
                btn: ['发送'],
                success:function(){
                    hideNtko();
                },
                end:function(){
                    showNtko();
                },
                yes: function(index, layero){
                    var selectionIframe = layero.parent().find("#layui-layer-iframe" + index).contents(), fileIds=[], fileNames = [];
                    selectionIframe.find("input[type=checkbox]:checked").each(function(i){
                        var file = $(this),fileName = file.attr("fileName");
                        if(file.val() != "on"){
                            fileIds.push(file.val());
                            fileNames.push(fileName);
                        }
                    });
                    if(fileIds.length){
                        $.ajax({
                            type: 'POST',
                            url: "addChatFiles",
                            async: false,
                            dataType: 'json',
                            data:{chatRoomId: chatWithJID, dataResourceIds: fileIds.join(",")},
                            success: function (data) {
                                fileIds = data.data.fileIds;
                                for(var i in fileIds){
                                    sendChatMessage("FILE*UPLOADED:" + fileIds[i] + ":" + fileNames[i], chatWithJID);
                                }
                                closeCurrentTab();//如果设定了yes回调，需进行手工关闭
                            }
                        });
                    }else{
                        closeCurrentTab();//如果设定了yes回调，需进行手工关闭
                    }

                    selectionIframe.find(".fileinput.fileinput-exists").each(function(){
                        var file = $(this).find("input[type=file]"), fileName = $(this).find(".fileinput-filename").html();
                        //TODO:临时上传的文件保存后再发消息
                    });
                    //TODO:将文件放到文件列表中去
                }
            }
        });
    }
	
    /**
     * 关闭聊天室
     */
    function leaveAndClose() {
        hideNtko();
        layer.confirm("确定离开？", {
            btn:["是", "否"],closeBtn:0
        }, function(index) {
            if(OFFICE_CONTROL_OBJ){
                OFFICE_CONTROL_OBJ.Close();
            }
            $("#ntko-container").hide();
            $("#ntko-container-shade").remove();
            leaveChatRoom($("#chatWithJID").val());
            layer.close(index);
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
        }, function(index) {
            showNtko();
            layer.close(index);
        });
    }
    /**
     * 全屏
     */
    function fullScreenMode() {
        if(OFFICE_CONTROL_OBJ){
            OFFICE_CONTROL_OBJ.FullScreenMode = true
            setTimeout(function(){
                OFFICE_CONTROL_OBJ.Activate(true);
                OFFICE_CONTROL_OBJ.FullScreenMode = true
            }, 500);
        }
    }
    /*onbeforeunload = function (event) {
        //离开聊天室
        leaveChatRoom($("#chatWithJID").val());
    }*/
    /**
     * 搜索聊天记录
     * @param keyWords 关键字
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 分页信息
     */
    function searchHistory(keyWords, startDate, endDate, page) {
        //设置隐藏的关键字值，用于js中判断
        $("#keyValues").val($("#keyWords").val());
        $("#historyChats").html("");
        var keyWordsT = "";
        var startDateT = "";
        var endDateT = "";
        var limit = 0;
        if(page != null){
            keyWordsT = keyWords;
            startDateT = startDate;
            endDateT = endDate;
            limit = page;
        }else{
            if($("#keyWords").val()){
                keyWordsT = $("#keyWords").val();
            }
            if($("#startDate").val()){
                startDateT = $("#startDate").val();
            }
            if($("#endDate").val()){
                endDateT = $("#endDate").val();
            }
        }
        searchChatHistory($("#chatWithJID").val(), keyWordsT, startDateT, endDateT, limit);
    }
    /**
     * 查找会话信息
     * @param conversationId 会话id
     * @param page 分页信息
     */
    function searchConversation(conversationId, page) {
        $("#historyChats").html("");
        var limit = 0;
        if(page != null){
            limit = page;
        }
        searchChatConversation(conversationId, limit);
    }

    function initchats(){
    }
</script>
<script type="text/html" id="messageAtomHtml">
    <li class={{direction}}>
        <img class='avatar' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey=${externalLoginKey}'/>
        <div class='message'>
            <span class='arrow'></span>
            <a href='javascript:;' class='name'> {{name}}</a>
            <span class='datetime'> {{time}}</span>
            <span class='body'>{{message}}</span>
            <div style="display: {{showConversation}};position: absolute;top: 5px;right: 5px">
                <a href="javascript:void(0);" title="相关会话" onclick="searchConversation('{{conversation}}')" class="btn blue icn-only">
                    相关会话
                </a>
            </div>
        </div>
    </li>
</script>
<#--高亮的html，展示关键字搜索出来的结果-->
<script type="text/html" id="HighlightHtml">
    <li class={{direction}}>
        <img class='avatar' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey=${externalLoginKey}'/>
        <div class='message' style="background-color: #ffb7b7;">
            <span class='arrow'></span>
            <a href='javascript:;' class='name'> {{name}}</a>
            <span class='datetime'> {{time}}</span>
            <span class='body'>{{message}}</span>
            <div style="display: {{showConversation}};position: absolute;top: 5px;right: 5px">
                <a href="javascript:void(0);" title="相关会话" onclick="searchConversation('{{conversation}}')" class="btn blue icn-only">
                    相关会话
                </a>
            </div>
        </div>
    </li>
</script>
<script type="text/html" id="pictureMessageAtomHtml">
    <li class={{direction}}>
        <img class='avatar' alt='' src="/content/control/partyAvatar?partyId={{partyId}}"/>
        <div class='message'>
            <span class='arrow'></span>
            <a href='javascript:;' class='name'> {{name}}</a>
            <span class='datetime'> {{time}}</span>
            <span class='body'>{{{message}}}</span>
            <div style="display: {{showConversation}};position: absolute;top: 5px;right: 5px">
                <a href="javascript:void(0);" title="相关会话" onclick="searchConversation('{{conversation}}')" class="btn blue icn-only">
                    相关会话
                </a>
            </div>
        </div>
    </li>
</script>
<script type="text/html" id="sysMessageAtom">
    <li class='system'>
        <div class='message'>
            <i class='font-red fa fa-bell'></i>
            <span><div class='font-green'>{{{message}}}</div></span>
            <i class='font-red fa fa-bell'></i>
        </div>
    </li>
</script>
<script type="text/html" id="editableDocListHtml">
    <div class='media' style='margin: 5px' id='{{fileId}}'>
        <div class='media-left file-mod'>
            <div class='file-icon-word'></div>
        </div>
        <div class='media-body' style="position:relative">
            <h4 class='media-heading'>{{fileName}}</h4>
            <span style='color:#9eacb4'>{{versionInfo}}</span>
            <span style="position:absolute;right:0;bottom:10px">
                <button style='display: none;float:right' type='button' class='btn red btn-md sbold' onclick="viewEdit('{{fileId}}')">编辑</button>
                <button style='display: none;float:right;' type='button' class='btn blue btn-md sbold' onclick="viewFile('{{fileId}}', false)">预览 </button>
            </span>
        </div>
    </div>
</script>
<script type="text/html" id="unEditableDocListHtml">
    <div class='media' style='margin: 5px' id='{{fileId}}'>
        <div class='media-left file-mod'>
            <div class='file-icon-word'></div>
        </div>
        <div class='media-body' style="position:relative">
            <h4 class='media-heading'>{{fileName}}</h4>
            <span style='color:#9eacb4'>{{versionInfo}}|当前由{{lockedBy}}锁定编辑</span>
            <span style="position:absolute;right:0;bottom:10px">
                <button style='display: none;float:right;' type='button' class='btn blue btn-md sbold' onclick=viewFile('{{fileId}}', false)>预览 </button>
            </span>
        </div>
    </div>
</script>
<script type="text/html" id="pageInfo">
    <a onclick="searchHistory('{{keyWords}}', '{{startDate}}', '{{endDate}}', '{{pagePre}}')" style="display: {{displayPre}};">上一页</a>
    <a onclick="searchHistory('{{keyWords}}', '{{startDate}}', '{{endDate}}', '{{pageNext}}')" style="display: {{displayNext}};">下一页</a>
</script>
<script type="text/html" id="conversationPage">
    <a onclick="searchConversation('{{conversationId}}', '{{pagePre}}')" style="display: {{displayPre}};">上一页</a>
    <a onclick="searchConversation('{{conversationId}}', '{{pageNext}}')" style="display: {{displayNext}};">下一页</a>
</script>

<script type="text/javascript">

    function DocumentOpened(a) {
        $("#fullScreen").on("click", fullScreenMode);
        $("#fullScreen").attr("class", "btn btn-md green btn-outline");
        OFFICE_CONTROL_OBJ.FileNew = false;//禁止本地新建
        OFFICE_CONTROL_OBJ.FileOpen = false;//禁止本地打开
        OFFICE_CONTROL_OBJ.FileSaveAs = false;//禁止本地另存
        OFFICE_CONTROL_OBJ.FileSave = false;//禁止本地保存
        IsFileOpened = true;
        OFFICE_CONTROL_OBJ.activeDocument.saved = true;//saved属性用来判断文档是否被修改过,文档打开的时候设置成ture,当文档被修改,自动被设置为false,该属性由office提供.
        //获取文档控件中打开的文档的文档类型
        switch (OFFICE_CONTROL_OBJ.doctype) {
            case 1:
                fileType = "Word.Document";
                fileTypeSimple = "word";
                break;
            case 2:
                fileType = "Excel.Sheet";
                fileTypeSimple = "excel";
                break;
            case 3:
                fileType = "PowerPoint.Show";
                fileTypeSimple = "ppt";
                break;
            case 4:
                fileType = "Visio.Drawing";
                break;
            case 5:
                fileType = "MSProject.Project";
                break;
            case 6:
                fileType = "WPS Doc";
                fileTypeSimple = "wps";
                break;
            case 7:
                fileType = "Kingsoft Sheet";
                fileTypeSimple = "et";
                break;
            default :
                fileType = "unkownfiletype";
                fileTypeSimple = "unkownfiletype";
        }
        setFileOpenedOrClosed(true);
    }
    function DocumentClosed(){
        $("#fullScreen").off("click");
        $("#fullScreen").attr("class", "btn btn-md grey btn-outline");
        IsFileOpened = false;
        setFileOpenedOrClosed(false);
    }

</script>

<script language="JScript" for=TANGER_OCX event="OnDocumentClosed()">
    DocumentClosed();
</script>
<script language="JScript" for="TANGER_OCX" event="OnDocumentOpened(TANGER_OCX_str,TANGER_OCX_obj)">
    DocumentOpened();
</script>
<style type="text/css">
    .add-member .add-control {
        width: 51px;
        height: 51px;
        background: url(/metronic-web/images/add-member.png) no-repeat 0 0
    }

    .add-member:hover .add-control {
        background-position: 0 -51px;
    }

    .media-body:hover > span > button {
        display: block !important;
    }
    #historyChats .message .body{
        padding-right:85px;
    }
    .offline {
        -webkit-filter: grayscale(100%);
        -moz-filter: grayscale(100%);
        -ms-filter: grayscale(100%);
        -o-filter: grayscale(100%);

        filter: grayscale(100%);

        filter: gray;
    }
</style>
<#--截图演示开始-->
<script language="javascript" src="/zxdoc/static/capture/jquery.md5.js"></script>
<script language="javascript" src="/zxdoc/static/capture/jquery.json-2.3.min.js?v=20161219"></script>
<script language="javascript" src="/zxdoc/static/capture/niuniucapture.js?v=20171025"></script>
<script language="javascript" src="/zxdoc/static/capture/capturewrapper.js?v=20170527"></script>
<#--截图演示结束-->
<input type="hidden" id="groupId" value="${data.groupId!}"/>
<input type="hidden" id="userLoginId" value="${data.userLoginId}"/>
<input type="hidden" id="password" value="${data.password}"/>
<input type="hidden" id="httpBase" value="${data.httpBase}"/>
<input type="hidden" id="jabberServer" value="${data.jabberServer}"/>
<input type="hidden" id="chatWithJID" value="${data.chatRoomJID}"/>
<input type="hidden" id="isLogin" value="${data.isLogin}"/>
<input type="hidden" id="roomName" value="${chatRoomName!}"/>
<input type="hidden" id="isManage" value="${data.hideMem}">
<#--<input type="text" name="uploadCaseFile" id="uploadCaseFile" data-size-limit="${fileSize}" style="width:1px;height:1px;">-->
<div class="lists" style="display: none">
    <h1 style="font-size: 25px">好友列表&nbsp;<span class="icosg-plus1" style="cursor:pointer" title="添加"
                                                onclick="addFriends()"></span></h1>
    <div id="to"></div>
    <ul id="friendList"></ul>
</div>
<div id="err" style="display: none;"></div>
<div class="lists" style="display: none">
<#--聊天室的权限：创建聊天室的时候仅勾选‘使房间仅对成员开放’，然后选定成员加入聊天室-->
    <ul id="chatRoomList"></ul>
</div>
<div class="portlet light" style="margin-bottom:0;padding-bottom:0">
    <div class="portlet-body">
        <div class="row">
            <div class="col-md-6 col-sm-6" id="chatWindow">
                <div class="portlet light bordered chat-portlet">
                    <div class="portlet-title">
                        <div class="caption">
                            <i class="fa fa-weixin font-green"></i>
                            <span class="caption-subject font-red-sunglo bold" style="cursor: pointer"
                                  onclick="changeSize('left')">即时消息</span>
                        <#--<div id="ctlDiv">-->
                        <#--<a id="btnCapture" href="javascript:StartCapture()" class="btn">屏幕截图</a>-->
                        <#--<a id="btnReload" href="javascript:ReloadPlugin()" class="btn" style="display:none">正在进行插件安装，安装成功后请点击这里...</a>-->
                        <#--</div>-->
                        <#--<div id="downloadNotice" style="display:none;">如果控件安装包未自动下载，请&nbsp;<a class="btn"-->
                        <#--id="downAddr"-->
                        <#--style="color:#ff0000;"-->
                        <#--target="_blank"-->
                        <#--href="/zxdoc/static/capture/CaptureInstall.exe"><strong>点击这里</strong></a>。-->
                        <#--</div>-->
                            <iframe id="downCapture" style="display:none;"></iframe>
                        </div>
                        <center>
                            <div id="chatTheme" style="display: none"></div>
                        </center>
                        <div class="actions" id="chat-actions">
                            <div class="portlet-input input-inline">
                                <a class="btn btn-md green btn-outline" data-section-id="chats" onclick="initchats();" title="即时消息">
                                    <i class="font-blue fa fa-commenting" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" data-section-id="members" title="协作成员">
                                    <i class="fa fa-user" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" data-section-id="files" title="协作文件">
                                    <i class="fa fa-file" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" data-section-id="chatsHistory" title="历史消息查询">
                                    <i class="fa fa-search" style="font-size:20px"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" id="chats">
                        <div class="scroller" data-always-visible="1" data-rail-visible1="1">
                            <ul class="chats" id="liveChat">

                            </ul>
                        </div>
                        <div class="chat-form" style="margin-top:10px; margin-bottom:5px;">
                            <div class="input-cont" style="width: 75%">
                                <input class="form-control" type="text" placeholder="请输入消息..." id="msg"/>
                            </div>
                            <div class="btn-cont" style="width: 25%;min-width: 150px">
                                <a href="javascript:void(0);" title="发送消息" onclick="sendChatMessage($('#msg').val(), $('#chatWithJID').val());"
                                   class="btn blue icn-only">
                                    <i class="fa fa-check icon-white"></i>
                                </a>
                                <a href="javascript:void(0);" onclick="StartCapture();" title="截图"
                                   class="btn blue icn-only">
                                    <i class="fa fa-picture-o icon-white"></i>
                                </a>
                                <a href="javascript:void(0);" onclick="uploadCaseFile();" title="发送文档"
                                   class="btn blue icn-only">
                                    <i class="fa fa-upload icon-white"></i>
                                </a>
                                <a style="position:relative;left:132px;top:-41px;" href="javascript:void(0);" onclick="uploadCaseFile2();" title="发送文档"
                                   class="btn blue icn-only">
                                    <i class="fa fa-upload icon-white"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" style="display: none;" id="members">
                        <div class="row" >
                            <div class="col-xs-2"  id="addNewMember">
                                <a href="javascript:;" style="text-align: center" class="add-member" title="添加新成员"
                                   onclick="addMember()">
                                    <div class="add-control"></div>
                                </a>
                            </div>
                        </div>
                        <div class="row" style="overflow: auto;">
                            <div>
                                <span id="membersContent"></span>
                            </div>
                        </div>
                        <div id="inviteList">

                        </div>
                    </div>
                    <div class="portlet-body" style="display: none;" id="files">
                        <div class="blog-content-2">
                            <div class="blog-single-content" style="padding: 0;">
                                <div class="blog-comments">
                                    <div class="c-comment-list" id="fileList" style="margin-bottom: 0; overflow: auto">

                                    </div>
                                    <a style="position: absolute;right: 50px;bottom:10px" href="javascript:void(0);" onclick="uploadCaseFile();" title="发送文档"
                                       class="btn blue icn-only">
                                        <i class="fa fa-upload icon-white"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" style="display: none;" id="chatsHistory">
                        <div class="row">
                            <div class="form-group col-sm-3">
                                <label class="control-label">关键字</label>
                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-user"></i>
                                                    </span>
                                    <input type="text" id="keyWords" class="form-control"/>
                                    <input type="hidden" id="keyValues" value="">
                                </div>
                            </div>
                            <div class="form-group col-sm-3">
                                <label class="control-label">开始日期</label>
                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-calendar"></i>
                                                    </span>
                                    <input type="text" class="form-control"  id="startDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                                </div>
                            </div>
                            <div class="form-group col-sm-3">
                                <label class="control-label">结束日期</label>
                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-calendar"></i>
                                                    </span>
                                    <input type="text" class="form-control"  id="endDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                                </div>
                            </div>
                            <div class="form-group col-sm-3">
                                <label class="control-label">&nbsp;</label>
                                <div class="input-group">
                                    <button type="button" class="btn btn-primary" onclick="searchHistory()">查询</button>
                                </div>
                            </div>
                        </div>
                        <div data-always-visible="1" data-rail-visible1="1" style="overflow-y: auto" id="hisDiv">
                            <ul class="chats" id="historyChats">

                            </ul>
                        </div>
                        <div id="pageList" style="float: right">

                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-sm-6" id="docWindow">
                <div class="portlet light bordered">
                    <div class="portlet-title">
                        <div class="caption">
                            <i class="fa fa-file-text font-green"></i>
                            <span class="caption-subject font-red-sunglo bold" style="cursor: pointer"
                                  onclick="changeSize('right')" title="点击放大">文档协作</span>
                        </div>
                        <div class="actions">
                            <div class="portlet-input input-inline">
                            <#--<a class="btn btn-md green btn-outline" title="工具栏" onclick="showOrHide()">-->
                            <#--<i class="font-blue fa fa-pencil" style="font-size:20px"></i>-->
                            <#--</a>-->
                                <a class="btn btn-md grey btn-outline" title="编辑" id="editFile">
                                    <i class="fa fa-pencil" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" title="保存" id="saveChangedFile" onclick="forbidSave(this)">
                                    <i class="fa fa-save" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" title="保存并解锁" id="saveAndUnlock">
                                    <i class="fa fa-send" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" title="强制刷新" id="refreshFile" onclick="refreshFile()">
                                    <i class="fa fa-refresh" style="font-size:20px"></i>
                                </a>
                                <a class="btn btn-md grey btn-outline" title="全屏" id="fullScreen" onclick="fullScreenMode()">
                                    <i class="fa fa-arrows-alt" style="font-size:20px"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" id="workspace">
                        <div id="ntko-container" class="ntko-container">

                        </div>
                        <iframe id="ntko-container-shade" style="display:none;width:100%;height:100%;border:0;position:absolute;top:0"></iframe>
                        <span class="file-name"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
