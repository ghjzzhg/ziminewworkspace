<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml" class="<#if iframe?has_content>iframe</#if>">
<head>
<#include "component://common/webcommon/render360.ftl"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript">
        if(!console) {console={}; console.log = function(){};}
    </script>
<#outputformat "UNXML">
    <title>${layoutSettings.companyName?default('')} <#if (page.titleProperty)?has_content>:${uiLabelMap[page.titleProperty]}<#elseif page.title?has_content>:${(page.title)!}</#if></title>
</#outputformat>

<#if layoutSettings.shortcutIcon?has_content>
    <#assign shortcutIcon = layoutSettings.shortcutIcon/>
<#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
    <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
</#if>
<#if shortcutIcon?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
</#if>
<#if layoutSettings.styleSheets?has_content>
<#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
</#if>
<#if layoutSettings.VT_STYLESHEET?has_content>
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
</#if>
<#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
<#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
    <#list layoutSettings.rtlStyleSheets as styleSheet>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
</#if>
<#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
    <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
        <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
</#if>
<#if layoutSettings.VT_EXTRA_HEAD?has_content>
    <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
    ${extraHead}
    </#list>
</#if>
<#if lastParameters??><#assign parametersURL = "&amp;" + lastParameters></#if>
<#if layoutSettings.WEB_ANALYTICS?has_content>
    <script language="JavaScript" type="text/javascript">
        <#list layoutSettings.WEB_ANALYTICS as webAnalyticsConfig>
    ${StringUtil.wrapString(webAnalyticsConfig.webAnalyticsCode!)}
    </#list>
      </script>
</#if>

    <link href="/metronic-web/layout/css/layout.min.css?t=20161222" rel="stylesheet" type="text/css" />
    <link href="/metronic-web/layout/css/themes/default.min.css" rel="stylesheet" type="text/css" id="style_color" />
    <link href="/metronic-web/layout/css/custom.min.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        #messageList > li > a{
            position: relative;
        }

        #messageList > li > a > .time{
            position: absolute;
            right: 5px;
            top: 5px;
            background: transparent;
        }
    </style>
<#if layoutSettings.javaScripts?has_content>
<#--layoutSettings.javaScripts is a list of java scripts. -->
<#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    <#list layoutSettings.javaScripts as javaScript>
        <#if javaScriptsSet.contains(javaScript)>
            <#assign nothing = javaScriptsSet.remove(javaScript)/>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#if>
    </#list>
</#if>
<#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
        <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
    </#list>
</#if>
    <script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="/zxdoc/static/caseChat.js?t=20171021"></script>
    <script src="/images/lib/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
    <script src="/metronic-web/layout/scripts/quick-sidebar.js" type="text/javascript"></script>
    <script src="/images/lib/pulsate/jquery.pulsate.min.js?t=20161230" type="text/javascript"></script>

</head>
<#if layoutSettings.headerImageLinkUrl??>
    <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}?externalLoginKey=${requestAttributes.externalLoginKey?default('')}">
<#else>
    <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}?externalLoginKey=${requestAttributes.externalLoginKey?default('')}">
</#if>
<#assign organizationLogoLinkURL = "${layoutSettings.organizationLogoLinkUrl!}">
<#assign miniBar = ''>
<#if userLogin?has_content && applicationMenuLocation?has_content>
    <#assign miniBar = 'normal-bar'>
    <#assign cookies = request.getCookies()>
    <#list cookies as cookie>
        <#if cookie.name = "mini-bar">
            <#assign miniBar = cookie.value>
        </#if>
    </#list>
</#if>
<body class="page-container-bg-solid page-boxed" ondragstart="return false">
<#if !iframe?has_content>
<iframe id="qqContact" style="display: none"></iframe>
<script src="/images/lib/ws.js?t=20171010" type="text/javascript"></script>
<script type="text/javascript">
    var docWindowLayerIndex = [];
    function hideVisibleDoc() {
        var chatRoomMap = getLayer().chatRoomIndexMap;
        if(chatRoomMap){
            for(var i in chatRoomMap){
                var roomIndex = chatRoomMap[i];
                if(!$("#layui-layer" + roomIndex + " .layui-layer-max").hasClass(".layui-layer-maxmin")){
                    //非最小化时
                    var hideNtko = window.frames['layui-layer-iframe' + roomIndex].hideNtko;
                    if(hideNtko){
                        hideNtko.apply(this);
                    }
                }
            }
        }
    }
    function showVisibleDoc() {
        var chatRoomMap = getLayer().chatRoomIndexMap;
        if(chatRoomMap){
            for(var i in chatRoomMap){
                var roomIndex = chatRoomMap[i];
                if(!$("#layui-layer" + roomIndex + " .layui-layer-max").hasClass(".layui-layer-maxmin")){
                    //非最小化时
                    var showNtko = window.frames['layui-layer-iframe' + roomIndex].showNtko;
                    if(showNtko){
                        showNtko.apply(this);
                    }
                }
            }
        }
    }
    function unRegisterDocWinIndex(index) {
        if(docWindowLayerIndex.indexOf(index) > -1){
            docWindowLayerIndex.splice(index, 1);
        }
    }
    function showUserSessionExpireAlert(content){
        //关闭openfire连接
        if(top.con){
            top.con.reConnectDisabled = true;
            top.con.disconnect();
        }
        //被踢出时主动退出，避免相同sessionid造成不同账户错位问题
        $.ajax({
            url:"/zxdoc/control/logout",
            type:"GET",
            dataType:"html",
            success:function(){
                //无需任何处理
            },
            error:function(){
                //无需任何处理
            }
        })
        getLayer().closeAll();//为了解决文档协作时，session失效导致文档置于最顶层的问题
        setTimeout(function(){
            if(content){
                top.expireLoginLayer = getLayer().open({
                    type: 1,
                    skin: 'layui-layer-rim', //加上边框
                    area: ['420px', '300px'], //宽高
                    content: content,
                    closeBtn: 0
                });
            }else{
                top.expireLoginLayer = getLayer().open({
                    type: 1,
                    skin: 'layui-layer-rim', //加上边框
                    area: ['420px', '300px'], //宽高
                    content: $("#expireHtml", top.document).html(),
                    closeBtn: 0
                });
            }
        },500);
    }
</script>
<script type="text/html" id="expireHtml">
<#include "component://common/webcommon/sessionExpireLogin.ftl"/>
</script>
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}">
<iframe id="expireSessionCheckIframe" src="<@ofbizContentUrl>${request.contextPath}</@ofbizContentUrl>/control/checkSessionExpirePage?ajax=true" frameborder="0" height="1" width="1" style="position:fixed;bottom: 0"></iframe>
</#if>
<div id="viewerPlaceHolderContainer" style="display: none; overflow: hidden">
    <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
        <tr>
            <td class="page_header">
                文件预览
            </td>
            <td class="page_header">
                <div style="float:right">
                    <span onclick="closeFilePreview()" class="button"><span>关闭</span></span>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">

                <div id="viewerPlaceHolder" style="height:650px;display:block">
                </div>
            </td>
        </tr>
    </table>
</div>
<div id="reportResultWrapper" style="display: none">
    <div class="row-fluid">
        <div class="span12">
            <div class="widget">
                <div class="head dark">
                    <div class="icon"><span class="icos-list"></span></div>
                    <h2>查看</h2>
                    <ul class="buttons">
                        <li><span onclick="closeCurrentTab(this)"><span class="icos-cancel1" title="关闭"></span></span></li>
                    </ul>
                </div>
                <div class="block">
                    <div id="reportResult" style="width: 100%; height: 500px"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="popupViewer" style="display: none">
    <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
        <tr>
            <td class="page_header">
                查看资料
            </td>
            <td class="page_header">
                <div style="float:right;">
                    <span onclick="closeCurrentTab(this)" class="button"><span>关闭</span></span>
                </div>
            </td>
        </tr>
    </table>
    <div id="popupContent" class="main_ct">

    </div>
</div>
<div id="wait-spinner" style="display:none">
    <div id="wait-spinner-image"></div>
</div>
<div class="${miniBar}">
    <div class="hidden">
        <a href="#column-container" title="${uiLabelMap.CommonSkipNavigation}" accesskey="2">
        ${uiLabelMap.CommonSkipNavigation}
        </a>
    </div>
<#--<div id="masthead" style="z-index: 1">
  <ul>
      <li class="menu-switch active glyphicon glyphicon-align-justify"></li>
    <#if layoutSettings.headerImageUrl??>
      <#assign headerImageUrl = layoutSettings.headerImageUrl>
    <#elseif layoutSettings.commonHeaderImageUrl??>
      <#assign headerImageUrl = layoutSettings.commonHeaderImageUrl>
    <#elseif layoutSettings.VT_HDR_IMAGE_URL??>
      <#assign headerImageUrl = layoutSettings.VT_HDR_IMAGE_URL.get(0)>
    </#if>
    <#if headerImageUrl??>
      <#if organizationLogoLinkURL?has_content>
          <li class="org-logo-area"><a href="<@ofbizUrl>${logoLinkURL}</@ofbizUrl>"><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${StringUtil.wrapString(organizationLogoLinkURL)}</@ofbizContentUrl>"></a></li>
          <#else>
          <li class="logo-area"><a href="${logoLinkURL}"><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${StringUtil.wrapString(headerImageUrl)}</@ofbizContentUrl>"/></a></li>
      </#if>
    </#if>
        &lt;#&ndash;<li class="glyphicon glyphicon-th header-menu-switch"></li>&ndash;&gt;
    <#if layoutSettings.middleTopMessage1?has_content && layoutSettings.middleTopMessage1 != " ">
      <li>
          <div class="last-system-msg" style="width: 30em">
              <div style="height:45px; overflow: hidden">
                      <center>${layoutSettings.middleTopHeader!}</center>
                  <div class="last-system-msg-1" style="display: none">
                      <a href="${layoutSettings.middleTopLink1!}">${layoutSettings.middleTopMessage1!}</a>
                  </div>
                  <div class="last-system-msg-2" style="display: none">
                      <a href="${layoutSettings.middleTopLink2!}">${layoutSettings.middleTopMessage2!}</a>
                  </div>
                  <div class="last-system-msg-3" style="display: none">
                      <a href="${layoutSettings.middleTopLink3!}">${layoutSettings.middleTopMessage3!}</a>
                  </div>
              </div>
          </div>
      </li>
    </#if>
    <li class="preference-area">
        <ul>
        <#if userLogin??>
          <#if layoutSettings.topLines?has_content>
              <li>
            <#list layoutSettings.topLines as topLine>
              <#if topLine.text??>
                <div>${topLine.text}<a href="${StringUtil.wrapString(topLine.url!)}${StringUtil.wrapString(externalKeyParam)}">${topLine.urlText!}</a></div>
              <#elseif topLine.dropDownList??>
                <div><#include "component://common/webcommon/includes/insertDropDown.ftl"/></div>
              <#else>
                <div>${topLine!}</div>
              </#if>
            </#list>
          </li>
          <#else>
            <li>${userLogin.userLoginId}</li>
          </#if>
          <li><a href="<@ofbizUrl>logout</@ofbizUrl>" class="glyphicon glyphicon-log-out" style="font-size: 30px; color:lightskyblue"></a></li>
        <#else/>
          <li>${uiLabelMap.CommonWelcome}! <a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
        </#if>
        &lt;#&ndash;-if webSiteId?? && requestAttributes._CURRENT_VIEW_?? && helpTopic??&ndash;&gt;
        &lt;#&ndash;<#if parameters.componentName?? && requestAttributes._CURRENT_VIEW_?? && helpTopic??>
          <#include "component://common/webcommon/includes/helplink.ftl" />
          <li><a style="font-size: 30px; color:lightgreen;" class="glyphicon glyphicon-info-sign <#if pageAvail?has_content>alert</#if>" href="javascript:lookup_popup1('showHelp?helpTopic=${helpTopic}&amp;portalPageId=${parameters.portalPageId!}','help' ,500,500);"></a></li>
        </#if>&ndash;&gt;
        </ul>
    </li>
  </ul>
</div>-->
<#if !iframe?has_content>
    <script id="chatRoomLink" type="text/html">
        <li>
            <a href="javascript:;" chatRoomId="{{chatRoomId}}" onclick="openWhichRoom('{{chatRoomName}}', '{{chatRoomId}}',$(this))">
                <span class="time">{{time}}</span>
                <span class="details">
                        <span class="label label-sm label-icon label-success">
                            <i class="fa fa-commenting"></i>
                        </span> {{chatRoomTitle}} </span>
            </a>
        </li>
    </script>
    <script id="noticeTpl" type="text/html">
        <li>
            <a href="javascript:;" onclick="{{clickAction}};readMsg(this)" data-notice-id="{{noticeId}}">
                <span class="time">{{time}}</span>
                <span class="details">
                        <span class="label label-sm label-icon label-success">
                            <i class="fa fa-commenting"></i>
                        </span> {{title}} </span>
            </a>
        </li>
    </script>
    <script type="text/javascript" src="/images/lib/mustache/mustache.min.js"></script>
    <script type="text/javascript" src="/zxdoc/static/jsjaccore/JSJaC.js"></script>
    <script type="text/javascript" src="/zxdoc/static/chatcore.js?t=20171101"></script>
    <script type="text/javascript">
        window.con = null;
        //TODO:ie9最小化聊天窗口时也会触发beforeunload，导致openfire session关闭，屏蔽之后何时执行DeleteOpenFireSessions？
        $(window).bind('beforeunload',remindInfo);

        function remindInfo() {
            var msgsize =  $("#messageList").find("li").length;
            if(msgsize > 0){
                var roomIds = "";
                $("#messageList").find("li a").each(function () {
                    roomIds += $(this).attr("chatroomid");
                })
                jQuery.ajax({
                    url: "remindInfo",
                    type: "POST",
                    dataType: "json",
                    data:{roomIds: roomIds},
                    success: function(data) {

                    }
                })
            }
        }
        //                /*//TODO:如果当前有聊天窗口，提示用户是否继续刷新
        //                var hasActiveChat = false;
        //                $(".layui-layer[type=iframe] iframe").each(function(){
        //                    if($(this).attr("src").indexOf("ChatHome") > -1){
        //                        hasActiveChat = true;
        //                    }
        //                })
        //                if(hasActiveChat){
        //                    if(!confirm("当前有协作正在进行，确定退出吗")){
        //                        return "确定退出吗?";
        //                    }
        //                }*!/
    </script>
    <script type="text/javascript">
        function uploadAvatar(){
            displayInLayer('上传头像', {requestUrl: 'UploadAvatar',width:'300px',height:'300px', shade: 0.2, end: function(data){
                if(data){
                    $("#user-avatar").attr("src", "/content/control/imageView?dataResourceId=" + data + "&exteralLoginKey=" + getExternalLoginKey());
                }
            }})
        }

        $(function () {
            getMessageList();
            vipShow();
            getPromptInfo();
            isHideImg();
            changeImg();
            openMessageHistory();
        })

        function adjustContentFrame(){
//            $("#content-frame").css("height", '');
            var iframe = document.getElementById("content-frame");
            try{
                var bHeight = $(iframe.contentWindow.document.body).height();
//                var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
//                var height = Math.max(bHeight, dHeight) + 30;//高度降低时回不去原来的高度
                var height = bHeight + 30;
                var windowHeight = document.documentElement.clientHeight - 140;
                if(height < windowHeight){
                    height = windowHeight;
                }
                $("#content-frame").css("height", height);
            }catch (ex){

            }
        }
        function getMessageList() {
            var roomIds = "";
            $("#messageList").find("li a").each(function () {
                var id = $(this).attr("chatroomid");
                if (id) {
                    roomIds += id + ",";
                }
            })
            $("#messageList").empty();
            if(roomIds != ""){
                roomIds.substring(0, roomIds.length - 1);
            }
            jQuery.ajax({
                url: "getMessageList",
                type: "POST",
                dataType: "json",
                data:{roomIds: roomIds},
                success: function(data) {
                    var roomList = data.roomList;
                    if(roomList != null){
                        for(var i = 0; i < roomList.length; i++){
                            var chatRoomTitle = "您收到来自 " + roomList[i].username + " 的消息";
                            var messageTime = new Date(roomList[i].lastChatTime);
                            var link = Mustache.render($("#chatRoomLink").html(), {
                                chatRoomName: roomList[i].username,
                                chatRoomTitle: chatRoomTitle,
                                chatRoomId: roomList[i].chatRoomId,
                                time: (messageTime.getMonth() + 1) + "-" + messageTime.getDate() + " " + messageTime.getHours() + ":" + messageTime.getMinutes()
                            });
                            $("#messageList").prepend(link);
                        }
                    }
                    var noticeList = data.noticeList;

                    if(noticeList != null){
                        for(var i = 0; i < noticeList.length; i++){
                            var notice = noticeList[i];
                            var link = Mustache.render($("#noticeTpl").html(), {
                                clickAction: notice.titleClickAction,
                                title: notice.title,
                                noticeId: notice.id,
                                time: notice.createdStamp.substring(11)
                            });
                            $("#messageList").prepend(link);
                        }
                    }

                    if($("#messageList").find("li").length > 0){
                        $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy").pulsate({
                            color: "#bf1c56"
                        });
                    }else{
                        $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy")
                    }
                }
            });
        }

        function changeImg() {
            var chatMessage = $("#chatMessage");
            if(!chatMessage.length){
                $("body").append('<img src="http://wpa.qq.com/pa?p=2:2881630050:53" id="chatMessage" style="z-index: 100;cursor: pointer;position:fixed;bottom:10px;left:10px;height:80px" onclick="toQQ()">');
                $("#qqMsg").append('<a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=2881630050&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:2881630050:51" alt="点击这里给我发消息" title="点击这里给我发消息"/></a>');
            }
            /*$("#chatMessage").css("position","fixed");
            $("#chatMessage").css("bottom","50px");
            $("#chatMessage").css("left",0);
            $("#chatMessage").css("margin-left","18px");
            $("#chatMessage").css("z-index","100");
            $("#chatMessage").css("display","");*/
        }

        function toQQ() {
//            window.open("http://wpa.qq.com/msgrd?v=3&uin=2881630050&site=qq&menu=yes");
            $("#qqContact").attr("src", "http://wpa.qq.com/msgrd?v=3&uin=2881630050&site=qq&menu=yes");
        }

        $(window).resize(function() {
            changeImg();
        });

        /**
         * 用户未完成case提示
         */
        /*function casePrompt(){
            setInterval(getPromptInfo,10000);
        }*/
        function showLoader(msg) {
            //覆盖showLoader方法，避免定时检测session时出现遮罩层
        }

        function getPromptInfo() {
//            if($("#previousLoginId").length == 0){
                jQuery.ajax({
                    url: "CaseActiveProgressJson",
                    type: "POST",
                    dataType: "json",
                    success: function(content) {
                        var obj = content.activeTasks;
                        var i = 0;
                        if(obj){
                            $("#caseList").html("");
                            $("#caseNum").html("");
                            $(".case-number").text("");
                            for (var prop in obj) {
                                var caseData = obj[prop],caseId = caseData["caseId"], caseValue = caseData["caseTitle"] + ":" + caseData["taskTitle"], progressId = caseData["progressId"];
                                var caseInfo = caseValue;
                                if(caseValue.length > 20){
                                    caseInfo = caseValue.substring(0,20) + "...";
                                }
                                $("#caseList").append('<li id="' + caseId + '-li" style="float: left;width: 100%"><a style="width:100%" href="#nowhere" title="' + caseValue + '">' +
                                        '<span class="subject" onclick="displayInside(' + "'/zxdoc/control/CaseHome?caseId=" + caseId + "'" + ')"><span class="from" style="font-size: smaller;"> ' + caseInfo + '</span><span class="time"></span></span><span class="message"></span></a>' +
                                        '</li>')
                                i++;
                            }
                            $("#caseNum").html(i);
                            $(".case-number").text(i);
                        }
                    }
                })
//            }
        }

        /**
         * 删除case信息提示
         */
        function delCasePrompt(caseId, progressId){
            $.ajax({
                url: "delCasePrompt",
                type: "POST",
                dataType: "json",
                data:{caseId: caseId, progressId: progressId},
                success: function() {
                    getPromptInfo();
                }
            })
        }

        /**
         * 删除case信息提示
         */
        function readMsg(obj){
            var noticeId = $(obj).data("noticeId"), li = $(obj).parent();
            $.ajax({
                url: "readSiteMsg",
                type: "POST",
                dataType: "json",
                data:{id: noticeId},
                success: function() {
                    li.remove();
                    if($("#messageList").find("li").length > 0){
                        $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy").pulsate({
                            color: "#bf1c56"
                        });
                    }else{
                        $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy")
                    }
                }
            })
        }

        function vipShow(){
            //会员到期时间，该功能尚未开发
            $.ajax({
                url: "VIPDue",
                type: "POST",
                data: "json",
                success: function(str) {
                    $(".vip-number").text(str.data);
                    $(".vip-type").text(str.vipType);
                }
            })
        }
        //定时请求刷新
        setInterval(vipShow,500000);

        //是否可以创建聊天
        function isHideImg() {
            $.ajax({
                url: "isHideImg",
                type: "POST",
                data: "json",
                success: function(str) {
                    if(str.data.result)
                    {
                        $("body").append('<img src="/metronic-web/chat.png" id="chatImg" style="z-index: -1;width: 50px; cursor: pointer;position:fixed;bottom:50px;right:50px;z-index:100" onclick="createInstantMessage();" title="即时聊天">');
                        $("#personChatHistory").show();
                        //页面刷新时从聊天服务器下线
                        $.ajax({
                            url: "DeleteOpenFireSessions",
                            type: "post",
                            success: function(data){
                                $.ajax({
                                    url: "LoginOpenFire",
                                    type: "POST",
                                    data: "json",
                                    success: function(data) {
                                        if(data.data.openfireNo != null && data.data.openfireNo != "") {
                                            init(data.data.httpBase, data.data.jabberServer, data.data.userLoginId, data.data.password, true);
                                        }
                                    }
                                });
                            }
                        })
                    }else {
                        //主账户不需要待办事项和case安排
                        $("#header_notification_bar").hide();
                        $("#header_task_bar").hide();
                    }
                }
            })
        }

        //打开历史聊天
        function openMessageHistory() {
            /* var openLayer = getLayer();
             var chatRoomIndexMap = openLayer.chatRoomIndexMap;
             if(chatRoomIndexMap){
                 for(var roomId in chatRoomIndexMap){
                     var layerIndex = chatRoomIndexMap[roomId];
                     if(layerIndex){
                         openLayer.min(layerIndex);
                     }
                 }
             }
             window.searchChatPartyIndex = displayInLayer2('搜索历史聊天对象', {requestUrl: '/zxdoc/control/searchChatHistory',height:'80%',width:'40%'});*/

            $.ajax({
                type:"post",
                url:"chatHistoryJson",
                dataType:"json",
                async:false,
                success:function (data) {
                    var items = data.data;
                    if(items){
                        var itemHtml = "", itemTpl = $("#chatHistoryItem").html();
                        for(var i in items){
                            var item = $.extend(items[i], {externalLoginKey: getExternalLoginKey()});
                            itemHtml += Mustache.render(itemTpl, item);
                        }
                        $("#personalChatHistory").html(itemHtml);
                    }
                }
            })
        }

        //打开或者创建聊天室
        function openOrCreateChatRoom(id) {
            //检查聊天室是否存在
            $.ajax({
                type: "post",
                url: "checkRoomExist",
                data: {personId: id},
                async: true,
                success: function (data) {
                    $(".quick-sidebar-toggler").click();
                    if (data.data == "no") {
                        top.createInstantMessage(id);
                    } else {
                        top.sendPrivateInvite(data.goalName, data.chatRoomJID, data.chatRoomName);
                        top.openInstantMessages(data.chatRoomName, data.chatRoomJID);
                    }
                }
            })
        }

        function reconnectTopSession(){
            if(!con.connected()){
                con.reConnectDisabled = false;
                con.connect(oArgs);
//                        con.disconnect();//最外部聊天连接重连，解决个人聊天接收方关闭窗口后不能再接收邀请消息问题。
            }/*else{
                        con.connect(oArgs);
                    }*/
        }
        //创建即时聊天
        function createInstantMessage(id) {
            if(id){
                $.ajax({
                    url:"createInstantMessage",
                    type:"POST",
                    data:{goalId:id,isInstant:"Y"},
                    success: function (str) {
                        if(str.data == "创建成功")
                        {

                            //获取聊天室的标识
                            var chatRoomJID = str.chatRoomJID;
                            var chatRoomName = str.chatRoomName;
                            //创建成功时，打开聊天室
                            if(chatRoomJID) {
                                top.sendPrivateInvite(str.goalName, chatRoomJID, chatRoomName);
                                top.openInstantMessages(chatRoomName, chatRoomJID);
                            }
                        }else{
                            showError("未知的错误");
                        }
                    }
                })
            }else{//打开搜索器
                var openLayer = getLayer();
                var chatRoomIndexMap = openLayer.chatRoomIndexMap;
                if(chatRoomIndexMap){
                    for(var roomId in chatRoomIndexMap){
                        var layerIndex = chatRoomIndexMap[roomId];
                        if(layerIndex){
                            openLayer.min(layerIndex);
                        }
                    }
                }
                window.searchChatPartyIndex = displayInLayer2('搜索聊天对象', {requestUrl: '/zxdoc/control/searchChatPerson',height:'80%',width:'600px'});
            }
        }

        function viewOfficeInLayer(fileId){
            displayInLayer("文件预览", {
                requestUrl: "/zxdoc/control/TestFileView?dataResourceId=" + fileId + "&externalLoginKey=" + getExternalLoginKey(),
                height: '90%'
            })
        }
    </script>
    <script id="chatHistoryItem" type="text/html">
        <li class="media" onclick="openOrCreateChatRoom('{{partyId}}')">
            <div class="media-status" style="margin-top:0">
                <span class="badge badge-success">{{lastChatTime}}</span>
            </div>
            <img class="media-object" height="45.71px" src="/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey={{externalLoginKey}}" alt="...">
            <div class="media-body">
                <h4 class="media-heading">{{fullName}}</h4>
                <div class="media-heading-sub">{{groupName}} </div>
            </div>
        </li>
    </script>
    <div class="page-header">
        <div class="page-header-top">
            <div class="container">
                <span style="color:lightseagreen;font-size: 25px;position: absolute;left: 70px;top: 10px;"></span>
                <!-- BEGIN LOGO -->
                <div class="page-logo">
                     	<div onclick="window.open('http://www.zixieonline.com')">
                     		<img src="http://www.zhongxi-cpa.com/uploadpic/20188149481756329.gif" style="left:200px;" height="50px" alt="logo"  class="logo-default">
                     	</div>
                </div>
                <!-- END LOGO -->
                <#if userLogin?has_content>
                    <!-- BEGIN RESPONSIVE MENU TOGGLER -->
                    <a href="javascript:;" class="menu-toggler"></a>
                    <!-- END RESPONSIVE MENU TOGGLER -->
                    <!-- BEGIN TOP NAVIGATION MENU -->
                    <div class="top-menu">
                        <ul class="nav navbar-nav pull-right">
                            <li class="dropdown dropdown-extended dropdown-notification dropdown-dark" title="控件测试">
                                <a href="javascript:viewOfficeInLayer('11370');">
                                    <i class="icon-screen-tablet"></i>
                                </a>
                            </li>
                            <li class="dropdown dropdown-extended quick-sidebar-toggler" title="聊天历史" id="personChatHistory" style="display: none">
                                <i class="icon-bubble"></i>
                            </li>
                            <!-- BEGIN NOTIFICATION DROPDOWN -->
                            <li class="dropdown dropdown-extended dropdown-notification dropdown-dark">
                                <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                                    <i class="icon-bell"></i>
                                    <span class="badge badge-default msg-number"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li class="external">
                                        <h3>您有
                                            <strong><span class="msg-number"></span> 条即时</strong> 通知</h3>
                                    </li>
                                    <li>
                                        <ul class="dropdown-menu-list scroller" style="height: 250px;overflow: auto" data-handle-color="#637283" id="messageList">
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <!-- END NOTIFICATION DROPDOWN -->
                            <!-- BEGIN TODO DROPDOWN -->
                            <li class="dropdown dropdown-extended dropdown-tasks dropdown-dark" id="header_task_bar">
                                <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                                    <i class="icon-calendar"></i>
                                    <span class="badge badge-default case-number"></span>
                                </a>
                                <ul class="dropdown-menu extended tasks">
                                    <li class="external">
                                        <h3>您有
                                            <strong id="caseNum"></strong> 项任务安排</h3>
                                    <#--<a href="#">全部</a>-->
                                    </li>
                                    <li>
                                        <ul class="dropdown-menu-list scroller" style="height: 275px;overflow: auto" id="caseList" data-handle-color="#637283">
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <!-- END TODO DROPDOWN -->
                            <!-- BEGIN NOTIFICATION DROPDOWN -->
                            <li class="dropdown dropdown-extended dropdown-notification dropdown-dark" id="header_notification_bar">
                                <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                                    <i class="glyphicon glyphicon-info-sign" ></i>
                                    <span class="badge badge-default vip-number"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li class="external">
                                        <h3>您的<span class="vip-type"></span>还有
                                            <strong><span class="vip-number"></span>天 </strong> 到期</h3>
                                    </li>
                                <#--<li>-->
                                <#--<ul class="dropdown-menu-list scroller" style="height: 250px;overflow: auto" data-handle-color="#637283" id="messageList">
                                    <li>
                                        <a href="javascript:;">
                                                  <span class="task">
                                                      <span class="desc">开通会员套餐 </span>
                                                  </span>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="javascript:;">
                                                  <span class="task">
                                                      <span class="desc">查看消费记录 </span>
                                                  </span>
                                        </a>
                                    </li>
                                </ul>-->
                                <#--</li>-->
                                </ul>
                            </li>
                            <!-- END NOTIFICATION DROPDOWN -->
                            <li class="droddown dropdown-separator">
                                <span class="separator"></span>
                            </li>
                            <!-- BEGIN INBOX DROPDOWN -->
                        <#--<li class="dropdown dropdown-extended dropdown-inbox dropdown-dark" id="header_inbox_bar">
                            <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                                <span class="circle">3</span>
                                <span class="corner"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="external">
                                    <h3>您有
                                        <strong>7 条未读</strong> 消息</h3>
                                    <a href="app_inbox.html">全部</a>
                                </li>
                                <li>
                                    <ul class="dropdown-menu-list scroller" style="height: 275px;" data-handle-color="#637283">
                                        <li>
                                            <a href="#">
                                                      <span class="photo">
                                                          <img src="/metronic-web/layout/img/avatar2.jpg" class="img-circle" alt=""> </span>
                                                      <span class="subject">
                                                          <span class="from"> 王冕 </span>
                                                          <span class="time">刚刚</span>
                                                      </span>
                                                <span class="message"> 《xxx文档》第2版本已经更新，请审阅... </span>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#">
                                                      <span class="photo">
                                                          <img src="/metronic-web/layout/img/avatar3.jpg" class="img-circle" alt=""> </span>
                                                      <span class="subject">
                                                          <span class="from"> 李飞 </span>
                                                          <span class="time">16 分钟 </span>
                                                      </span>
                                                <span class="message"> 您好，能否将... </span>
                                            </a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </li>-->
                            <!-- END INBOX DROPDOWN -->
                            <!-- BEGIN USER LOGIN DROPDOWN -->
                            <li class="dropdown dropdown-user dropdown-dark">
                                <a href="javascript:;" onclick="uploadAvatar()" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                                    <#assign party = delegator.findOne("Party", {"partyId" : userLogin.partyId}, true)>
                                    <#if party.avatar?has_content>
                                        <img id="user-avatar" alt="" style="max-width: 53px" class="img-circle" src="/content/control/imageView?dataResourceId=${party.avatar}&externalLoginKey=${externalLoginKey}">
                                    <#else>
                                        <img id="user-avatar" alt="" style="max-width: 53px" class="img-circle" src="/metronic-web/layout/img/avatar.png">
                                    </#if>
                                    <#if party.partyTypeId == "PERSON">
                                        <#assign person = delegator.findOne("Person", {"partyId": party.partyId}, true)>
                                        <#assign userNick = person.getString("fullName")?default(person.getString("firstName"))>
                                    <#else>
                                        <#assign loginPartyGroup = delegator.findOne("PartyGroup", {"partyId": party.partyId}, true)>
                                        <#assign partnerGroupName = loginPartyGroup.getString("partnerGroupName")?default('')>
                                        <#if partnerGroupName?has_content>
                                            <#assign userNick = partnerGroupName>
                                        <#else>
                                            <#assign userNick = loginPartyGroup.getString("groupName")>
                                        </#if>

                                    </#if>
                                    <span class="username username-hide-mobile">${userNick}</span>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-default">
                                    <li>
                                        <a href="#nowhere" onclick="displayInside('PersonalInfo')">
                                            <i class="icon-user"></i> 个人信息 </a>
                                    </li>
                                    <li>
                                        <a href="#nowhere" onclick="displayInside('UserCalendar')">
                                            <i class="icon-calendar"></i> 我的日程 </a>
                                    </li>
                                <#--<li>
                                    <a href="#nowhere">
                                        <i class="icon-envelope-open"></i> 收件箱
                                        <span class="badge badge-danger"> 3 </span>
                                    </a>
                                </li>
                                <li>
                                    <a href="#nowhere">
                                        <i class="icon-rocket"></i> 任务
                                        <span class="badge badge-success"> 7 </span>
                                    </a>
                                </li>-->
                                    <li class="divider"> </li>
                                <#--<li>
                                    <a href="page_user_lock_1.html">
                                        <i class="icon-lock"></i> 锁屏 </a>
                                </li>-->
                                    <li>
                                        <a href="${request.contextPath}/control/logout">
                                            <i class="icon-key"></i> 退出 </a>
                                    </li>
                                </ul>
                            </li>
                            <!-- END USER LOGIN DROPDOWN -->
                            <!-- BEGIN QUICK SIDEBAR TOGGLER -->
                        <#--<li class="dropdown dropdown-extended quick-sidebar-toggler">
                            <span class="sr-only">切换右侧栏</span>
                            <i class="icon-logout"></i>
                        </li>-->
                            <!-- END QUICK SIDEBAR TOGGLER -->
                        </ul>
                    </div>
                    <!-- END TOP NAVIGATION MENU -->
                <#else>
                    <div class="top-menu">
                        <a href="/zxdoc/control/main">登录</a>
                    </div>
                </#if>
            </div>
        </div>
        <!-- BEGIN QUICK SIDEBAR -->
        <a href="javascript:;" class="page-quick-sidebar-toggler">
            <i class="icon-login"></i>
        </a>
        <div class="page-quick-sidebar-wrapper" data-close-on-body-click="false">
            <div class="page-quick-sidebar">
                <ul class="nav nav-tabs">
                    <li class="active">
                        <a href="javascript:;" data-target="#quick_sidebar_tab_1" data-toggle="tab"> 最近沟通
                        <#--<span class="badge badge-danger">2</span>-->
                        </a>
                    </li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane active page-quick-sidebar-chat" id="quick_sidebar_tab_1">
                        <div class="page-quick-sidebar-chat-users" data-rail-color="#ddd" data-wrapper-class="page-quick-sidebar-list">
                            <h3 class="list-heading">即时聊天</h3>
                            <ul class="media-list list-items" id="personalChatHistory">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- END QUICK SIDEBAR -->
        <!-- BEGIN HEADER MENU -->
        <div class="page-header-menu">
            <div class="container">
                <!-- BEGIN MEGA MENU -->
                <!-- DOC: Apply "hor-menu-light" class after the "hor-menu" class below to have a horizontal menu with white background -->
                <!-- DOC: Remove data-hover="dropdown" and data-close-others="true" attributes below to disable the dropdown opening on mouse hover -->
                <div class="hor-menu  " style="background-color: #444d58;">
                <#--</div>放在messages.ftl中结束，让page-header包含header及菜单-->
<#else>
                    <script type="text/javascript">
                        $(window).load(function() {//使用load是因为图片加载在ready之后
                            var openLayer = getLayer();
                            openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
                        });
                    </script>
</#if>