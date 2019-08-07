<script type="text/javascript" src="/zxdoc/static/OfficeControlFunctions.js?t=20161208"></script>
<style type="text/css">
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
<script type="text/javascript">
    var IsFileOpened = false;  //当前是否有文档打开
    var thisDocumentId = ""; //当前文档的id
    $(function () {
        $.getScript("/zxdoc/static/ntkoofficecontrol.js?t=" + (new Date()).getTime(), function (data, textStatus, jqxhr) {
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
            if(!confirm("是否放弃对当前文档的更改")){
                return;
            }
        }
        $("#ntko-container").hide();
        layer.confirm("是否推送给其他讨论组成员", {
            btn: ["是", "否"],
        }, function(index) {
            sendChatMessage("OPEN*FILE*INSTRUCTION*RECEIVED:" + fileId + ":" + true, $("#chatWithJID").val());
            layer.close(index);
            $("#ntko-container").show();
            lockFileAndEdit(fileId);
        }, function(index) {
            layer.close(index);
            $("#ntko-container").show();
            lockFileAndEdit(fileId);
        })
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
            if(!confirm("是否放弃对当前文档的更改")){
                return;
            }
        }
        loadFileInControl(fileId, false);
    }
    function loadFileInControl(fileId, edit){

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
        var tempFileName = saveFileToUrl(url, fileId);
        /*$.ajax({
            type: 'post',
            url: 'http://localhost:8080/content/control/saveDoc?newFileName=' + tempFileName + '&externalLoginKey=' + $("input[name=externalLoginKey]").val(),
            dataType: 'json',
            success: function () {
                if (unlock) {
                    sendChatMessage($("#userLoginId").val() + "保·存·并·解·锁·了·文·档" + "*" + tempFileName, $("#chatWithJID").val());
                } else {
                    sendChatMessage($("#userLoginId").val() + "已·完·成·对·文·档·的·编·辑" + "*" + tempFileName, $("#chatWithJID").val());
                }
            }
        })*/

        if (unlock) {
            //改为解锁成功后发送消息
//            sendChatMessage($("#userLoginId").val() + "保·存·并·解·锁·了·文·档" + "*" + tempFileName, $("#chatWithJID").val());
        } else {
            sendChatMessage($("#userLoginId").val() + "已·完·成·对·文·档·的·编·辑" + "*" + tempFileName, $("#chatWithJID").val());
        }
        return tempFileName;
    }
    /**
     * 保存并解锁
     */
    function saveAndUnlock(event) {
        //首先保存
        var tempFileName= saveChangedFile(event, "unlock");
        //其次解锁
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
            sendChatMessage($("#userLoginId").val() + "保·存·并·解·锁·了·文·档" + "*" + tempFileName, $("#chatWithJID").val());
        });
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
            if(confirm("是否放弃对当前文档的更改？")){
                loadFileInControl(fileId, true);
            }else{
                return false;
            }
        }else{//保存并解锁后处于只读模式
            loadFileInControl(fileId, false);
        }
    }
    function changeEditorHeight(editorHeight){

        if(!editorHeight){
            editorHeight = $(window).height() * 0.7;
        };

        $("#chats .scroller").height(editorHeight);
        $("#files").height(editorHeight + 70);
        $("#fileList").height(editorHeight + 20);
        $("#members").height(editorHeight + 70);
        $("#chatsHistory").height(editorHeight + 70);
        $("#hisDiv").height(editorHeight - 8);
        $("#ntko-container").height(editorHeight + 70);
        initSlimScroll(".scroller", {start: "bottom", refresh : true});
    }
    /**
     * 全屏
     */
    function fullScreenMode() {
        if(OFFICE_CONTROL_OBJ){
            OFFICE_CONTROL_OBJ.FullScreenMode = true
        }
    }
    /*onbeforeunload = function (event) {
        //离开聊天室
        leaveChatRoom($("#chatWithJID").val());
    }*/

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
<#--<input type="hidden" id="groupId" value="${data.groupId!}"/>-->
<#--<input type="hidden" id="userLoginId" value="${data.userLoginId}"/>-->
<div class="portlet light bordered">
    <div class="portlet-title">
        <div class="caption">
            <i class="fa fa-file-text font-green"></i>
            <span class="caption-subject font-red-sunglo bold" style="cursor: pointer"
                  onclick="changeSize('right')">文档协作</span>
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
        <div id="ntko-container" class="ntko-container"
             style="clear:left;height:600px;z-index:-100;position: relative">

        </div>
        <span class="file-name"></span>
    </div>
</div>
