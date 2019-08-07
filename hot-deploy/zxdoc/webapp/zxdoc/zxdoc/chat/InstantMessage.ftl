<script type="text/javascript" src="/images/lib/mustache/mustache.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/jsjaccore/JSJaC.js"></script>
<script type="text/javascript" src="/zxdoc/static/chatcore.js?t=20171101"></script>
<script type="text/javascript" src="/zxdoc/static/caseChat.js?t=20171021"></script>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>
<link href="/images/lib/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/fancybox/jquery.fancybox.js"></script>

<script id="officeEdit" type="text/html">
    <div class="media" style="margin: 5px" id="{{fileId}}">
        <div class="media-left file-mod">
            <div class="file-icon-{{fileType}}"></div>
        </div>
        <div class="media-body">
            <h4 class="media-heading"><a href="#nowhere" onclick="viewFile('{{fileId}}',false)">{{fileName}}</a></h4>
            <span style="color:#9eacb4">版本:{{fileVersion}} | {{fileUpdateInfo}}</span>
            <button style="display: none;float:right" type="button" class="btn red btn-md sbold"
                    onclick="viewEdit('{{fileId}}')">编辑
            </button>
        </div>
    </div>
</script>
<script id="officeView" type="text/html">
    <div class="media" style="margin: 5px" id="{{fileId}}">
        <div class="media-left file-mod">
            <div class="file-icon-{{fileType}}"></div>
        </div>
        <div class="media-body">
            <h4 class="media-heading"><a href="#nowhere" onclick="viewFile('{{fileId}}',false)">{{fileName}}</a></h4>
            <span style="color:#9eacb4">版本:{{fileVersion}} | {{fileUpdateInfo}}|当前由{{fileLocker}}锁定编辑</span>
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
<#outputformat "UNXML">
    memberNames['${joiner}'] = '${joinerName}';
    memberNames['${sponsorId}'] = '${sponsorName}';
</#outputformat>
    function enterChatRoom(){
        joinChatRoom($("#chatWithJID").val());
    }


    //加入讨论组
    function joinChatRoom(roomJID) {
        $("#liveChat").empty();//重新进入聊天室，历史消息将重新接收
        var p = new JSJaCPresence();
        //roomJID = "aaa@conference.rextec";//临时（应传参数）
        p.setTo(roomJID + '/' + top.oArgs.username);
        p.setShow('chat');
        top.con.send(p);
        return false;
    }

    var IsFileOpened = false;  //当前是否有文档打开
    var thisDocumentId = ""; //当前文档的id
    $(function () {
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

        $('#showprewindow').click();
        $('#autoupload').click();
        $('#btnUpload').hide();
        <#--<#assign captureAuthKey = Static["org.ofbiz.base.util.UtilMd5"].generateCaptureAuthKey()>-->
        <#--InitCapture('${captureAuthKey?default("")}');-->

        //如果是被邀请成员，隐藏邀请按钮
        if($("#hideMem").val()=="true")
        {
            $("#addDiv").attr("hidden","true");
        }

        $("#roomMembers").hide();
    })

    function changeEditorHeight(editorHeight){

        if(!editorHeight){
            editorHeight = $(window).height() - 180
        };

        $("#chats .scroller").height(editorHeight);
        $("#chatsHistory").height(editorHeight + 51);
        $("#hisDiv").height(editorHeight - 8);
        $("#ntko-container").height(editorHeight + 70);
        initSlimScroll(".scroller", {start: "bottom", refresh : true});
    }

    function changeSize(location) {
        if (location == "left") {
            if ($("#chatWindow").attr("class") == "col-md-6 col-sm-4") {
                $("#chatWindow").attr("class", "col-md-12 col-sm-12");
                $("#docWindow").attr("class", "col-md-6 col-sm-8 hide");
            } else if ($("#chatWindow").attr("class") == "col-md-12 col-sm-12") {
                $("#chatWindow").attr("class", "col-md-6 col-sm-4");
                $("#docWindow").attr("class", "col-md-6 col-sm-8");
            }
        } else if (location == "right") {
            if ($("#docWindow").attr("class") == "col-md-6 col-sm-8") {
                $("#docWindow").attr("class", "col-md-12 col-sm-12");
                $("#chatWindow").attr("class", "col-md-6 col-sm-4 hide");
            } else if ($("#docWindow").attr("class") == "col-md-12 col-sm-12") {
                $("#chatWindow").attr("class", "col-md-6 col-sm-4");
                $("#docWindow").attr("class", "col-md-6 col-sm-8");
            }
        }
    }
    /**
     * 关闭聊天室
     */
    function leaveAndClose() {
        $("#ntko-container").hide();
        layer.confirm("确定离开？", {
            btn:["是", "否"]
        }, function() {
            leaveChatRoom($("#chatWithJID").val());
            var openLayer = getLayer();
            var index = openLayer.getFrameIndex(window.name); //先得到当前iframe层的索引
            openLayer.close(index); //再执行关闭
        }, function(index) {
            $("#ntko-container").show();
            layer.close(index);
        });
    }

    /**
     * 搜索聊天记录
     * @param keyWords 关键字
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 分页信息
     */
    function searchHistory(keyWords, startDate, endDate, page) {
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

    var layerindex;
    var layersearch;
    //弹出一个搜索的div
    function addOtherPeople()
    {
        layersearch = layer.open({
            type: 1,
            title:"搜索用户",
            /*offset: ['300px', '250px'],*/
            move: false,
            area: ['240px', '100px'], //宽高
            content: Mustache.render("<div style='width: 200px;height: 48px;margin-left: 20px;margin-top:10px;float: left'> <form id='searchForm' name='searchForm'>" +
                    "<div><input type='text' id='searchData' style='width: 120px;float: left'></div>" +
                    "<div><button type='button' onclick='showText()' style='width: 50px;margin-left:20px;float: left'>搜索</button></div>" +
                    "</form></div>")
        });
    }

    function showText() {
        var data = $("#searchData").val();
        layer.close(layersearch);
        searchOtherPeople(data);
    }

    //邀请其他人进入聊天室
    function searchOtherPeople(data)
    {
        var result = "";
        //当前已经在聊天室的用户
        for (var i = 0; i < $(".members").length; i++) {
            result += $($(".members")[i]).attr("id") + ",";
        }
        result = result.substring(0, result.length - 1);
        $.ajax({
            type: 'post',
            async : false,//此处要同步,必须添加成功再邀请
            url: "addOtherPeople",
            data: {"arr": result, "chatRoomJID": $("#chatWithJID").val(),"data":data},
            dataType: 'json',
            success: function (data) {
                var content = "";
                if (data.members && data.members.length > 0) {
                    var externalLoginKey = getExternalLoginKey();
                    for (var i = 0; i < data.members.length; i++) {
                        var memberData = data.members[i];
                        var parameters = {
                            name: memberData.fullName ? memberData.fullName + "(" + memberData.groupName + ")" : memberData.openFireJid + "(" + memberData.groupName + ")",
                            event: "addAndInvites('" + memberData.openFireJid + "')",
                            externalLoginKey: externalLoginKey,
                            partyId: memberData.partyId,
                            jid:memberData.openFireJid
                        }
                        content += Mustache.render("<div jid='{{jid}}'><img id='clickImg' class='avatar' style='width: 51px; height: 51px;margin:5px 10px;' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey={{externalLoginKey}}'/><a href='javascript:void(0);' onclick='{{event}}'>{{name}}</a></div>", parameters)
                    }
                }else {
                    content = "无可添加人员";
                }
                layerindex = layer.open({
                    type: 1,
                    scrollbar:true,
                    title:"邀请人员",
                    /*offset: ['80px', '500px'],*/
                    move: false,
                    area: ['600px', '300px'], //宽高
                    content: Mustache.render( content, parameters)
                });
            }
        })
    }

    //添加并邀请
    function addAndInvites(JID) {
        //TODO 现邀请，接受后再添加进聊天室
        sendInvite(JID, $("#chatWithJID").val(), $("#chatTheme").text());
        $.ajax({
            type: 'post',
            url: 'AddUserToChatRoom',
            data: {"JID": JID, "roomName": $("#chatWithJID").val()},
            dataType: "json",
            success: function (data) {
                $("div[jid='" + JID + "']").remove();
                //TODO 发送系统消息通知其加入聊天室
                listMembers($("#chatWithJID").val());
                showInfo(data.data);
            }
        })
        layer.close(layerindex);
        layer.close(layersearch);
        $("#addDiv").remove();
    }


    //该js是为了取消移除成员功能的js方法
    function removeChatRoomMember()
    {

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
<script type="text/html" id="pictureMessageAtomHtml">
    <li class={{direction}}>
        <img class='avatar' alt='' src='/metronic-web/layout/img/avatar1.jpg'/>
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
        <div class='media-body'>
            <h4 class='media-heading'>{{fileName}}</h4>
            <span style='color:#9eacb4'>{{versionInfo}}</span>
            <button style='display: none;float:right' type='button' class='btn red btn-md sbold' onclick="viewEdit('{{fileId}}')">编辑</button>
            <button style='display: none;float:right' type='button' class='btn blue btn-md sbold' onclick="viewFile('{{fileId}}', false)">预览 </button>
        </div>
    </div>
</script>
<script type="text/html" id="unEditableDocListHtml">
    <div class='media' style='margin: 5px' id='{{fileId}}'>
        <div class='media-left file-mod'>
            <div class='file-icon-word'></div>
        </div>
        <div class='media-body'>
            <h4 class='media-heading'>{{fileName}}</h4>
            <span style='color:#9eacb4'>{{versionInfo}}|当前由{{lockedBy}}锁定编辑</span>
            <button style='display: none;float:right' type='button' class='btn blue btn-md sbold' onclick=viewFile('{{fileId}}', false)>预览 </button>
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
<style type="text/css">
    .add-member .add-control {
        width: 51px;
        height: 51px;
        background: url(/metronic-web/images/add-member.png) no-repeat 0 0
    }

    .add-member:hover .add-control {
        background-position: 0 -51px;
    }

    .media-body:hover > button {
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
    .portlet.light{
        overflow-y: hidden!important;
    }
</style>
<#--截图演示开始-->
<script language="javascript" src="/zxdoc/static/capture/jquery.md5.js"></script>
<script language="javascript" src="/zxdoc/static/capture/jquery.json-2.3.min.js?v=20150926"></script>
<script language="javascript" src="/zxdoc/static/capture/niuniucapture.js?v=20151108"></script>
<script language="javascript" src="/zxdoc/static/capture/capturewrapper.js?v=20170527"></script>
<#--截图演示结束-->
<input type="hidden" id="groupId" value="${data.groupId!}"/>
<input type="hidden" id="hideMem" value="${data.hideMem!}"/>
<input type="hidden" id="userLoginId" value="${data.userLoginId}"/>
<input type="hidden" id="password" value="${data.password}"/>
<input type="hidden" id="httpBase" value="${data.httpBase}"/>
<input type="hidden" id="jabberServer" value="${data.jabberServer}"/>
<input type="hidden" id="chatWithJID" value="${data.chatRoomJID}"/>
<input type="hidden" id="isLogin" value="${data.isLogin}"/>
<input type="hidden" id="roomName" value="${chatRoomName!}"/>
<input type="hidden" id="joiner" value="${joiner!}"/>
<input type="hidden" id="sponsorName" value="${sponsorName!}"/>
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
<div class="portlet light">
    <div class="portlet-body">
        <div>
            <div id="chatWindow">
                <div class="portlet light bordered chat-portlet">
                    <div class="portlet-title">
                        <div class="caption">
                            <i class="fa fa-weixin font-green"></i>
                            <span class="caption-subject font-red-sunglo bold" style="cursor: pointer">即时消息</span>
                            <#--<div id="ctlDiv">-->
                            <#--&lt;#&ndash;<a id="btnCapture" href="javascript:StartCapture()" class="btn">屏幕截图</a>&ndash;&gt;-->
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
                            <div id="chatTheme" style="display:none;"></div>
                        </center>
                        <div class="actions" id="chat-actions">
                            <div class="portlet-input input-inline">
                                <a class="btn btn-md grey btn-outline" data-section-id="members" title="成员" id="roomMembers">
                                    <i class="fa fa-user" style="font-size:20px"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" id="chats">
                        <div class="scroller" data-always-visible="1" data-rail-visible1="1">
                            <ul class="chats" id="liveChat">

                            </ul>
                        </div>
                        <div class="chat-form">
                            <div class="input-cont">
                                <input class="form-control" type="text" placeholder="请输入消息..." id="msg"/>
                            </div>
                            <div class="btn-cont" style="width: 100px">
                                <a href="javascript:void(0);" title="发送消息" onclick="sendChatMessage($('#msg').val(), $('#chatWithJID').val());"
                                   class="btn blue icn-only">
                                    <i class="fa fa-check icon-white"></i>
                                </a>
                                <a href="javascript:void(0);" title="关闭" onclick="leaveAndClose();"
                                   class="btn red icn-only">
                                    <i class="fa fa-close icon-white"></i>
                                </a>
                            <#--    <a href="javascript:void(0);" title="截图"
                                   class="btn blue icn-only">
                                    <i class="fa fa-picture-o icon-white"></i>
                                </a>-->
                            <#--<a href="javascript:void(0);" onclick="StartCapture();" title="截图"
                               class="btn blue icn-only">
                                <i class="fa fa-picture-o icon-white"></i>
                            </a>-->
                            <#--    <a href="javascript:void(0);" onclick="uploadCaseFile();" title="发送文档"
                                   class="btn blue icn-only">
                                    <i class="fa fa-upload icon-white"></i>
                                </a>-->
                            </div>
                        </div>
                    </div>
                    <div class="portlet-body" style="display: none;" id="members">
                        <div class="row">
                            <div class="col-xs-2" id="addDiv">
                                <a href="javascript:;" style="text-align: center" class="add-member" title="添加新成员"
                                   onclick="addOtherPeople()">
                                    <div class="add-control"></div>
                                </a>
                            </div>
                            <span id="membersContent"></span>
                        </div>
                        <div id="inviteList">

                        </div>
                    </div>
                    <div class="portlet-body" style="display: none;" id="files">
                        <div class="blog-content-2">
                            <div class="blog-single-content" style="padding: 0;">
                                <div class="blog-comments">
                                    <div class="c-comment-list" id="fileList">

                                    </div>
                                    <a style="float:right" href="javascript:void(0);" onclick="uploadCaseFile();" title="发送文档"
                                       class="btn blue icn-only">
                                        <i class="fa fa-upload icon-white"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

        </div>
    </div>
</div>
