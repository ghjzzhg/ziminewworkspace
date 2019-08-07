window.oArgs = new Object();
window.nameAndJID = {}
window.friendsArr = new Array();
window.onlineFriends = new Array();
window.chatRoomMembers = new Array();//当前在线的成员id
window.memberNames = {};
// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

// function displayMessage(message, time) {
//     $("#messages").append("<div class='temp'>" + message + "</div>");
//     setTimeout(function () {
//         $(".temp").first().remove();
//     }, time);
// }

/**
 *
 * @param fromJID 消息的来源:openfireId,即partyId
 * @param message
 * @param direction
 * @param time
 * @param isPicture
 * @param conversationId
 * @returns {*}
 */
function messageAtom(fromJID, message, direction, time, isPicture, conversationId, isHistory) {
    var date = new Date();
    if (direction == "system") {
        return Mustache.render($("#sysMessageAtom").html(), {
            message: message
        })
    } else {
        /*if (fromJID.indexOf("conference") > 0) {
         fromJID = fromJID.split("/")[1] + "@" + oArgs.domain;
         fromJID = $(document.getElementById(fromJID)).text();
         } else {
         var tempJID = fromJID;
         //fromJID = nameAndJID[fromJID.split("/")[0].replace("@", "")];
         fromJID = $(document.getElementById(fromJID)).text();
         if (!fromJID) {//考虑nameAndJID中不存在fromJID的情况（非好友之间的对话）
         //TODO 是显示用户的fullName还是显示账号
         fromJID = tempJID.split("@")[0];
         }
         }*/
        //获取当前活跃的tab标签的title,用于判断是否显示截图
        var tab = $(".font-blue").parent().attr("title");
        if (isPicture && tab == "即时消息") {
            var pictureId = new Date().getTime();
            message = "<a class='attachment-image' href='#" + pictureId + "'><img src='data:image/jpg;base64," + message + "' width='200px'/></a>" +
                "<img id='" + pictureId + "' src='data:image/jpg;base64," + message + "'style='display:none'/>";
        }
        time = time ? time : (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ":" + (date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds());
        var partyId = fromJID.indexOf('@') > -1 ? fromJID.substring(0, fromJID.indexOf('@')) : fromJID;
        var parameters = {
            direction: direction,
            partyId: partyId,
            name: memberNames[partyId],
            time: time,
            message: message,
            showConversation: conversationId ? "inline" : "none",
            conversation: conversationId ? conversationId : ""
            //TODO 头像
        };
        var content = "";
        //即时消息需要显示截图
        if (isPicture && tab == "即时消息") {
            content = $("#pictureMessageAtomHtml").html();
        } else {
            if (tab == "即时消息") {
                content = $("#messageAtomHtml").html();
            }
            //历史信息需要排除截图
            else {
                if (!isPicture) {
                    var keyValues = $("#keyValues").val();
                    if (isHistory && keyValues != null && keyValues != "" && message.indexOf(keyValues) >= 0) {
                        content = $("#HighlightHtml").html();
                    } else {
                        content = $("#messageAtomHtml").html();
                    }
                }
            }
        }
        return Mustache.render(content, parameters);
    }
}
function addIframe() {
    $(".fancybox-overlay").prepend("<iframe src='javascript:false' style='position:absolute; top:0px; left:0px; width:100%; height:100%; z-index:-1; border-width:0px;'></iframe>");
}
$(window).keydown(function (event) {//回车键按下
    //发送聊天消息
    if (event.keyCode == 13 && $("#msg").val() && $("#chatWithJID").val() != "") {
        if ($("#chatWithJID").val().indexOf("conference") < 0) {
            $("#liveChat").append(messageAtom("", $("#msg").val(), "out", "", false));
            scrollToBottom();
        }
        sendChatMessage($("#msg").val(), $("#chatWithJID").val());
    }
    //查找聊天记录
    /* if (event.keyCode == 13 && $("#chatsHistory").css("display") != "none"){
     //if($("#keyWords").val() || $("#startDate").val() || $("#endDate").val()){
     searchHistory();
     //}
     }*/
});
/**
 *
 * @param msg 消息内容
 * @param to 发送给谁
 */
function sendChatMessage(msg, to) {
    $("#msg").val("");
    if (msg.trim()) {
        try {
            top.reconnectTopSession();

            if (to.indexOf("conference") >= 0) {//聊天室发消息
                var joiner = $("#joiner");
                if (joiner.length) {//一对一聊天时每次发送消息都发送提醒，避免对方关闭窗口时无法收到消息
                    var joiner = joiner.val(), jabberDomain = $("#jabberServer").val();
                    top.sendPrivateInvite(joiner + "@" + jabberDomain, $("#chatWithJID").val(), $("#sponsorName").val());
                } else {//协作聊天时，向各方发出信息提醒
                    var currentUser = $("#userLoginId").val(), roomName = $("#roomName").val(), jabberDomain = $("#jabberServer").val();
                    ;
                    for (var id in memberNames) {
                        var joiner = id;
                        if (joiner != currentUser) {
                            top.sendInvite(joiner + "@" + jabberDomain, $("#chatWithJID").val(), roomName);
                        }
                    }
                }
                top.sendMessageInChatRoom(msg, to);

            } else {
                var oMsg = new JSJaCMessage();
                oMsg.setTo(new JSJaCJID(to));
                oMsg.setBody(msg);
                oMsg.setType('chat');
                top.con.send(oMsg);
            }
        } catch (e) {
            console.log(e);
        }
    }
}
// <![CDATA[
function handleIQ(oIQ) {
    var xmlResponse = $.parseXML(oIQ.xml());
    var jsonResponse = xmlToJson(xmlResponse);
    var IQquery = $(xmlResponse).find('query')
    if ($(IQquery).attr("xmlns") == "jabber:iq:roster") {
        $("#friendList").html("");
        friendsArr = [];
        $(IQquery).children().each(function () {
            var li = $(this).attr("jid");
            var liJid = li;
            //从oa数据库中取出jid对应的fullname
            $.ajax({
                type: 'post',
                url: 'GetFullNameByJid',
                data: {"JID": li},
                dataType: 'json',
                success: function (data) {
                    li = data.name;
                    friendsArr.push(li);
                    nameAndJID[liJid.replace("@", "")] = li;
                    li = $("<li></li>").text(li);
                    $(li).attr("style", "cursor:pointer");
                    $(li).attr("class", "offline");
                    $(li).attr("title", liJid);
                    $(li).attr("onclick", "startChat(this)");
                    for (var i = 0; i < onlineFriends.length; i++) {
                        if (onlineFriends[i].indexOf(liJid) >= 0) {
                            $(li).attr("class", "online");
                        }
                    }
                    $("#friendList").append(li);
                }
            })
        })
    }
    else if ($(xmlResponse).find("iq").attr("from") == ("conference." + oArgs.domain)) {
        if (IQquery.attr("xmlns") == "http://jabber.org/protocol/disco#items") {
            $("#chatRoomList").html("");
            $(IQquery).children().each(function () {
                var li = $("<li></li>").text($(this).attr("name") != "" ? $(this).attr("name") : $(this).attr("jid"));
                $(li).attr("style", "cursor:pointer");
                //$(li).attr("onclick", "return joinChatRoom('" + $(this).attr("jid") + "')");
                var hiddenInput = $("<input/>");
                $(hiddenInput).attr("type", "hidden");
                $(hiddenInput).val($(this).attr("jid"));
                $("#chatRoomList").append(li);
                $("#chatRoomList").append(hiddenInput);
                //joinChatRoom($(this).attr("jid"));
            })
        }
    }

    //历史消息查找返回值处理
    var zdlist = jsonResponse.iq.zdlist;
    if (zdlist) {
        var chatRoomId = zdlist.attributes.for;
        var chatRoomIndexMap = getLayer().chatRoomIndexMap;
        if (top == self && chatRoomIndexMap && chatRoomIndexMap[chatRoomId]) {
            //top中的连接获得消息后分发给各窗口处理
            //如果已经存在相对应的聊天窗口。
            $("#layui-layer" + chatRoomIndexMap[chatRoomId] + " iframe")[0].contentWindow.handleIQ(oIQ);
            return;
        }
        handleChatHistory(zdlist, "zdlist");
    }

    var kclist = jsonResponse.iq.kclist;
    if (kclist) {
        var chatRoomId = kclist.attributes.for;
        var chatRoomIndexMap = getLayer().chatRoomIndexMap;
        if (top == self && chatRoomIndexMap && chatRoomIndexMap[chatRoomId]) {
            //top中的连接获得消息后分发给各窗口处理
            //如果已经存在相对应的聊天窗口。
            $("#layui-layer" + chatRoomIndexMap[chatRoomId] + " iframe")[0].contentWindow.handleIQ(oIQ);
            return;
        }
        handleChatHistory(kclist, "kclist")
    }
}

function handleChatHistory(list, tagName) {
    var chatListRaw = list.chat;
    var chatList = chatListRaw.length ? chatListRaw : [chatListRaw];
    var para = list.para.attributes;
    var keyWords = para.keyWords;//(根据keyWords查询返回回来的keyWords);
    if (chatList && chatList.length > 0) {
        $(chatList).each(function () {
            var chatMessage = this.attributes;
            var messageDirection = "in";
            var sendBy = chatMessage.sendBy;
            var conversationId = null;//如果keyWords不为空则传递该参数
            if (keyWords) {
                conversationId = chatMessage.conversationId;
            }
            if (sendBy.split("@")[0] == top.oArgs.username) {
                messageDirection = "out";
            }
            var body = chatMessage.body;
            var sentDate = new Date(parseInt(chatMessage.sentDate)).format("yyyy-MM-dd hh:mm:ss");

            //消息的格式化
            if (body.indexOf("已·完·成·对·文·档·的·编·辑") >= 0) {
                //TODO 成员被移除后就找不到他的名字了，待解决
                // $("#historyChats").append(messageAtom("", $(document.getElementById(sendBy)).html() + "已完成对当前文档的编辑", "system", sentDate, false, conversationId));
            } else if (body.indexOf("正·在·编·辑·文·档") >= 0) {
                // $("#historyChats").append(messageAtom("", $(document.getElementById(sendBy)).html() + "正在编辑文档", "system", sentDate, false, conversationId));
            } else if (body.indexOf("保·存·并·解·锁·了·文·档") >= 0) {
                // $("#historyChats").append(messageAtom("", $(document.getElementById(sendBy)).html() + "保存并解锁了文档", "system", sentDate, false, conversationId));
            } else if (body.indexOf("OPEN*FILE*INSTRUCTION*RECEIVED:") >= 0) {
                //do nothing
            } else if (body.indexOf("P*I*C*T*U*R*E:") >= 0) {
                $("#historyChats").append(messageAtom(sendBy, body.split(":")[1], messageDirection, sentDate, true, conversationId));
            } else if (body.indexOf("FILE*UPLOADED:") >= 0) {
                var uploadedFileId = body.split(":")[1];
                var uploadedFileName = body.split(":")[2];
                var fileName = uploadedFileName.toLowerCase(), fileType = fileName.substring(fileName.lastIndexOf("."));
                var message = "";
                if (fileType == ".doc" || fileType == ".xls" || fileType == ".ppt") {
                    message = $(document.getElementById(sendBy)).html() + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewFile('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                } else {
                    message = $(document.getElementById(sendBy)).html() + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewPdfInLayer('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                }
                // var message = $(document.getElementById(sendBy)).html() + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewFile('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                // $("#historyChats").append(messageAtom(sendBy, message, "system", sentDate, false, conversationId));
            } else if (body.indexOf("MEMBER*REMOVED:") >= 0) {
                var removedMember = body.split(":")[1];
                // $("#historyChats").append(messageAtom(sendBy, removedMember + "已被移出聊天室", "system", sentDate, false, conversationId));
            } else {
                $("#historyChats").append(messageAtom(sendBy, body, messageDirection, sentDate, false, conversationId, true));
            }
        });
        //分页：
        //查询条件
        var cvid = para.cvid;//conversationId
        var count = list.count.attributes.count;
        var startDate = parseInt(para.startDate);
        if (startDate > 0) {
            startDate = new Date(startDate).format("yyyy-MM-dd");
        }
        var endDate = parseInt(para.endDate);
        if (endDate > 0) {
            endDate = new Date(endDate).format("yyyy-MM-dd");
        }
        var limit = parseInt(para.limit);
        var pageHtml = "";
        var pages = count / 20;
        if (parseInt(pages) - pages == 0) {
            pages--;
        }
        var prePage = 0;
        var nextPage = 0;
        var displayPre = "inline";
        var displayNext = "inline";
        if (parseInt(pages) == 0) {//结果只有一页
            displayPre = "none";
            displayNext = "none";
        } else {
            if (limit > 0 && limit < parseInt(pages)) {
                prePage = limit - 1;
                nextPage = limit + 1;
            } else if (limit == 0) {
                displayPre = "none";
                nextPage = limit + 1;
            } else if (limit == parseInt(pages)) {
                displayNext = "none";
                prePage = limit - 1;
            }
        }
        if (tagName == "zdlist") {
            pageHtml += Mustache.render($("#pageInfo").html(), {
                    keyWords: keyWords,
                    startDate: startDate,
                    endDate: endDate,
                    pagePre: prePage,
                    displayPre: displayPre,
                    pageNext: nextPage,
                    displayNext: displayNext
                }) + "&nbsp;";
        } else if (tagName == "kclist") {
            pageHtml += Mustache.render($("#conversationPage").html(), {
                    conversationId: cvid,
                    pagePre: prePage,
                    displayPre: displayPre,
                    pageNext: nextPage,
                    displayNext: displayNext
                }) + "&nbsp;";
        }
        $("#pageList").html(pageHtml);
    }
}

function handleMessage(oJSJaCPacket) {
    //var html = '';
    var messageNode = oJSJaCPacket.getNode();
    var fromJID = oJSJaCPacket.getFromJID();
    var sourceId = fromJID._node + "@" + fromJID._domain;
    var delayNode = $(messageNode).find("delay");//里面记录了消息发送的时间<delay xmlns="urn:xmpp:delay" stamp="2016-07-01T10:00:56.467Z" from="zhangxiao@rextec/Spark"/>
    var stamp = delayNode.attr("stamp"), from = delayNode.attr("from");
    var chatRoomIndexMap = getLayer().chatRoomIndexMap;
    if (top == self && chatRoomIndexMap && chatRoomIndexMap[sourceId]) {
        //top中的连接获得消息后分发给各窗口处理
        //如果已经存在相对应的聊天窗口。
        $("#layui-layer" + chatRoomIndexMap[sourceId] + " iframe")[0].contentWindow.handleMessage(oJSJaCPacket);
        return;
    }

    oArgs = top.oArgs;
    fromJID = fromJID + "";
    var fromName = "";
    var messageBody = oJSJaCPacket.getBody() + "";
    //html += '<div class="msg"><b>Received Message from ' + oJSJaCPacket.getFromJID() + ':</b><br/>';
    //判断reason是否为空
    if (oJSJaCPacket.getReason().htmlEnc() != '') {
        // if (!delayNode.length) {//历史系统消息不显示
            $("#liveChat").append(messageAtom("", oJSJaCPacket.getReason(), "system", "", false));
            scrollToBottom();
        // }
    } else {
        var messageBody = oJSJaCPacket.getBody();
        var arr = oJSJaCPacket.getBody().split("+");
        if (oJSJaCPacket.getBody()) {//收到聊天消息且消息不为空
            if (fromJID.indexOf("conference") >= 0) {//聊天室消息
                var messageTime = "";
                if (stamp) {//这种情况只有是讨论组聊天记录时会存在
                    messageTime = new Date(stamp);
                    var now = new Date();
                    now = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
                    var messageDate = messageTime.getFullYear() + "-" + (messageTime.getMonth() + 1) + "-" + messageTime.getDate();
                    messageTime = messageTime.getHours() + ":" + messageTime.getMinutes() + ":" + messageTime.getSeconds();
                    if (messageDate != now) {
                        messageTime = messageDate + " " + messageTime;
                    }
                }
                if ($("#chatWithJID").val()) {
                    var msgAuthor = fromJID.split("/")[1];
                    fromName = memberNames[msgAuthor];
                    if (msgAuthor == oArgs.username) {//自己发出的消息
                        if (messageBody.indexOf("已·完·成·对·文·档·的·编·辑") >= 0) {
                            // if (!messageTime) {
                                $("#liveChat").append(messageAtom(msgAuthor, fromName + "更新了当前文档", "system", messageTime, false));
                                scrollToBottom();
                            // }
                        } else if (messageBody.indexOf("正·在·编·辑·文·档") >= 0) {
                            var bodyArr = messageBody.split("*");
                            var fileId = bodyArr[1], fileName = bodyArr.length == 3 ? bodyArr[2] : "";
                            var msgHtml = fromName + "正在编辑文档" + (fileName ? "<span style='color:red' title='" + messageTime + "'>[" + fileName + "]</span>&nbsp;<a class='btn btn-xs green' href=\"javascript: joinFileEdit('" + fileId + "');\">协作</a>" : "");
                            $("#liveChat").append(messageAtom(msgAuthor, msgHtml, "system", messageTime, false));
                            if (!messageTime) {
                                listFiles();
                            }
                            scrollToBottom();
                        } else if (messageBody.indexOf("保·存·并·解·锁·了·文·档") >= 0) {
                            if (!messageTime) {
                                listFiles();
                            }
                            $("#liveChat").append(messageAtom(msgAuthor, fromName + "解锁了文档", "system", messageTime, false));
                            scrollToBottom();
                        } else if (messageBody.indexOf("P*I*C*T*U*R*E:") >= 0) {
                            $("#liveChat").append(messageAtom(msgAuthor, messageBody.split(":")[1], "out", messageTime, true));
                            scrollToBottom();
                        } else if (messageBody.indexOf("OPEN*FILE*INSTRUCTION*RECEIVED:") >= 0) {
                            if (!messageTime) {

                            }
                        } else if (messageBody.indexOf("FILE*UPLOADED:") >= 0) {
                            // if (!messageTime) {
                                listFiles();
                                var uploadedFileId = messageBody.split(":")[1];
                                var uploadedFileName = messageBody.split(":")[2];
                                var fileName = uploadedFileName.toLowerCase(), fileType = fileName.substring(fileName.lastIndexOf("."));
                                var message = "";
                                if (fileType == ".doc" || fileType == ".xls" || fileType == ".ppt") {
                                    message = fromName + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewFile('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                                } else {
                                    message = fromName + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewPdfInLayer('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                                }

                                $("#liveChat").append(messageAtom(msgAuthor, message, "system", messageTime, false));
                                scrollToBottom();
                            // }
                        } else if (messageBody.indexOf("MEMBER*REMOVED:") >= 0) {
                            if (!messageTime) {
                                listMembers($("#chatWithJID").val());
                                var removedMember = messageBody.split(":")[1];
                                $("#liveChat").append(messageAtom(msgAuthor, removedMember + "已被移出聊天室", "system", messageTime, false));
                                scrollToBottom();
                            }
                        } else {
                            $("#liveChat").append(messageAtom(msgAuthor, messageBody, "out", messageTime, false));
                            scrollToBottom();
                        }
                    } else {
                        var iframeIndex = getLayer().getFrameIndex(window.name), layero = $(top.document).find("#layui-layer" + iframeIndex);
                        if (!layero.find(".layui-layer-min:visible").length) {
                            layero.find("div.layui-layer-title").css({"background": "lightgreen"});
                        }
                        var bodyArr = messageBody.split("*");
                        if (messageBody.indexOf("已·完·成·对·文·档·的·编·辑") >= 0) {
                            if (!messageTime) {
                                var tempFileName = bodyArr[1];
                                $("#liveChat").append(messageAtom(msgAuthor, fromName + "更新了当前文档", "system", messageTime, false));
                                if (checkFileControlStatus() && tempFileName == thisDocumentId) {//当前已打开相同文档，自动更新
                                    loadFileInControl(tempFileName, false);
                                }
                                scrollToBottom();
                            }
                        } else if (messageBody.indexOf("正·在·编·辑·文·档") >= 0) {
                            // if(!messageTime){
                            var fileId = bodyArr[1], fileName = bodyArr.length == 3 ? bodyArr[2] : "";
                            if (thisDocumentId == fileId) {
                                $("#editFile").off("click");
                                $("#editFile").attr("class", "btn btn-md grey btn-outline");
                            }
                            //锁定文档涉及的DOM操作复杂，此处改为重新从服务器获取
                            listFiles();
                            var msgHtml = fromName + "正在编辑文档" + (fileName ? "<span style='color:red' title='" + messageTime + "'>[" + fileName + "]</span>&nbsp;<a class='btn btn-xs green' href=\"javascript: joinFileEdit('" + fileId + "');\">协作</a>" : "");
                            $("#liveChat").append(messageAtom(msgAuthor, msgHtml, "system", messageTime, false));
                            scrollToBottom();
                            // }
                        } else if (messageBody.indexOf("保·存·并·解·锁·了·文·档") >= 0) {
                            if (!messageTime) {
                                listFiles();
                                var tempFileName = bodyArr[1];
                                if (checkFileControlStatus() && tempFileName == thisDocumentId) {//当前已打开相同文档，自动更新
                                    loadFileInControl(tempFileName, false);
                                }
                                $("#liveChat").append(messageAtom(msgAuthor, fromName + "解锁了文档", "system", messageTime, false));
                                scrollToBottom();
                            }
                        } else if (messageBody.indexOf("P*I*C*T*U*R*E:") >= 0) {
                            $("#liveChat").append(messageAtom(msgAuthor, messageBody.split(":")[1], "in", messageTime, true));
                            scrollToBottom();
                        } else if (messageBody.indexOf("OPEN*FILE*INSTRUCTION*RECEIVED:") >= 0) {
                            var fileId = messageBody.split(":")[1];
                            var isLocked = messageBody.split(":")[2];
                            if (!messageTime) {//非历史消息
                                if (!thisDocumentId || thisDocumentId != fileId) {//未打开文档或打开的不是同一份文档
                                    if(!thisDocumentId ) {
                                        hideNtko();
                                        var confirmIndex = getLayer().confirm(fromName + "向你推送了新的文档，是否接收？", {
                                            btn: ['接收', '取消'],end: function(index, layero){
                                                showNtko();
                                            }
                                        }, function () {
                                            getLayer().close(confirmIndex);
                                            var fileStatus = checkFileControlStatus();
                                            if(fileStatus == 2){
                                                hideNtko();
                                                getLayer().confirm("当前文档未保存，是否保存？", {
                                                    btn: ['保存', '取消'],end: function(index, layero){
                                                        showNtko();
                                                    }
                                                }, function (index) {
                                                    getLayer().close(index);
                                                    saveAndUnlock();
                                                    loadFileInControl(fileId, false);
                                                })
                                            }else{
                                                loadFileInControl(fileId, false);
                                            }
                                        })
                                    }else {
                                        showInfo(fromName + "向你推送了新的文档。");
                                    }
                                } else {
                                    showInfo(fromName + "向你推送了新的文档。");
                                }
                                /*if (!IsFileOpened) {
                                 viewFile(fileId, isLocked, true);
                                 } else if (thisDocumentId != fileId) {
                                 if (onlyView) {
                                 viewFile(fileId, isLocked, true);
                                 } else {
                                 if (confirm(fromName + "向你推送了新的文档，是否保存当前文档并打开新文档？")) {
                                 //TODO 此处保存默认为保存并解锁，考虑其他情况
                                 saveAndUnlock();
                                 viewFile(fileId, isLocked, true);
                                 }
                                 }
                                 }*/
                            }
                        } else if (messageBody.indexOf("FILE*UPLOADED:") >= 0) {
                            //文件上传历史消息显示出来，方便用户点击查看
                            var uploadedFileId = messageBody.split(":")[1];
                            var uploadedFileName = messageBody.split(":")[2];
                            var fileName = uploadedFileName.toLowerCase(), fileType = fileName.substring(fileName.lastIndexOf("."));
                            var message = "";
                            if (fileType == ".doc" || fileType == ".xls" || fileType == ".ppt") {
                                message = fromName + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewFile('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                            } else {
                                message = fromName + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewPdfInLayer('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                            }
                            // var message = fromName + "上传了文件" + "<a href='javascript:void(0);' onclick=\"viewFile('" + uploadedFileId + "')\">" + uploadedFileName + "</a>"
                            $("#liveChat").append(messageAtom(msgAuthor, message, "system", messageTime, false));
                            scrollToBottom();

                            if (!messageTime) {
                                listFiles();
                            }
                        } else if (messageBody.indexOf("MEMBER*REMOVED:") >= 0) {
                            if (!messageTime) {
                                listMembers($("#chatWithJID").val());
                                var removedMember = messageBody.split(":")[1];
                                $("#liveChat").append(messageAtom(msgAuthor, removedMember + "已被移出聊天室", "system", messageTime, false));
                                scrollToBottom();
                            }
                        } else {
                            if (messageBody.indexOf("YOU*HAVE*BEEN*REMOVED") >= 0) {
                                $("#liveChat").append(messageAtom(msgAuthor, "您已被移出聊天室", "system", messageTime, false));
                                scrollToBottom();
                            } else {
                                $("#liveChat").append(messageAtom(msgAuthor, messageBody, "in", messageTime, false));
                                scrollToBottom();
                            }
                        }
                    }
                }
            } else {//普通聊天消息
                if (fromHeader && messageBody.indexOf("ROOM*INVITE:") > -1) {
                    var chatRoomName = messageBody.split(":")[2];
                    var username = messageBody.split(":")[1];
                    var chatRoomId = messageBody.split(":")[1];
                    var exists = getLayer().chatRoomIndexMap && getLayer().chatRoomIndexMap[chatRoomId] || $("#messageList").find("a[chatRoomId='" + chatRoomId + "']").length;
                    if (!exists) {
                        //通过roomId获取邀请人的名字，修复当前邀请人名为登录人的bug
                        $.ajax({
                            type: "post",
                            url: "getInviteUsername",
                            data: {roomId: username, name: chatRoomName},
                            dataType: "json",
                            async: false,
                            success: function (data) {
                                username = data.inviteName;
                            }
                        });
                        var chatRoomTitle = chatRoomTitle = "您收到来自 " + username + " 的消息";
                        if(stamp){
                            messageTime = new Date(stamp);
                        }else{
                            messageTime = new Date();
                        }
                        var msgTimeStr = (messageTime.getMonth() + 1) + "-" + messageTime.getDate() + " " + messageTime.getHours() + ":" + messageTime.getMinutes();
                        var link = Mustache.render($("#chatRoomLink").html(), {
                            chatRoomName: username,
                            chatRoomTitle: chatRoomTitle,
                            chatRoomId: chatRoomId,
                            time: msgTimeStr
                        });
                        $("#messageList").prepend(link);
                        $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy").pulsate({
                            color: "#bf1c56"
                        });
                        ;
                    }
                }
                if (messageBody.indexOf("YOU*HAVE*BEEN*REMOVED") > -1) {
                    var chatRoomId = messageBody.split(":")[1];
                    var chatRoomName = messageBody.split(":")[2];
                    showInfo("您不再是 " + chatRoomName + " 的协作成员");

                    if (getLayer().chatRoomIndexMap && getLayer().chatRoomIndexMap[chatRoomId]) {
                        var roomLayerIndex = getLayer().chatRoomIndexMap[chatRoomId];
                        getLayer().close(roomLayerIndex);
                    }
                }
            }
            //html += oJSJaCPacket.getBody().htmlEnc() + '</div>';
        }
    }
    //document.getElementById('iResp').innerHTML += html;
    //document.getElementById('iResp').lastChild.scrollIntoView();
}

function handlePresence(oJSJaCPacket) {
    var fromJID = oJSJaCPacket.getFromJID();
    var sourceId = fromJID._node + "@" + fromJID._domain;
    var chatRoomIndexMap = getLayer().chatRoomIndexMap;
    if (top == self && chatRoomIndexMap && chatRoomIndexMap[sourceId]) {
        //top中的连接获得消息后分发给各窗口处理
        //如果已经存在相对应的聊天窗口。
        $("#layui-layer" + chatRoomIndexMap[sourceId] + " iframe")[0].contentWindow.handlePresence(oJSJaCPacket);
        return;
    }

    oArgs = top.oArgs;

    var xmlResponse = $.parseXML(oJSJaCPacket.xml());
    //var html = '<div class="msg">';
    var friends = '';
    var friendJID = oJSJaCPacket.getFromJID() + "";
    var arr = (friendJID + "").split("/");
    var isChatRoomPresence = arr.length == 2 && (arr[0].split("@"))[1] == ("conference." + oArgs.domain);
    if (!oJSJaCPacket.getType()) {
        if (isChatRoomPresence && $("#chatWithJID").val()) {
            //TODO 刷新成员列表
            //listMembers($("#chatWithJID"));
            try {
                var selector = arr[1], userName = memberNames[arr[1]];
                if (selector) {
                    $("#liveChat").append(messageAtom("", userName + "加入了聊天室", "system", "", false));
                    scrollToBottom();
                    $("#" + selector).closest("tr").removeClass("offline").addClass("online");
                }
            } catch (e) {
                //不用管
            } finally {
                chatRoomMembers.push(arr[1]);
            }
        } else {
            if (onlineFriends.indexOf(arr[0]) < 0) {
                onlineFriends.push(arr[0]);
                $("li[title='" + arr[0] + "']").attr("class", "online");
            }
        }
    } else {
        if (oJSJaCPacket.getType()) {
            if (oJSJaCPacket.getType() == "unavailable") {
                if (isChatRoomPresence) {
                    try {
                        var selector = arr[1], userName = memberNames[arr[1]];
                        if (selector) {
                            $("#liveChat").append(messageAtom("", userName + "离开了聊天室", "system", "", false));
                            scrollToBottom();
                            $("#" + selector).closest("tr").removeClass("online").addClass("offline");
                        }
                    } catch (e) {
                        //不用管
                    } finally {
                        for (var i = 0; i < chatRoomMembers.length; i++) {
                            if (arr[1] == chatRoomMembers[i]) {
                                chatRoomMembers.splice(i, 1);
                                break;//
                            }
                        }
                    }
                } else {
                    $("li[title='" + arr[0] + "']").attr("class", "offline");
                    for (var i = 0; i < onlineFriends.length; i++) {
                        if (arr[0] == onlineFriends[i]) {
                            onlineFriends.splice(i, 1);
                            break;//
                        }
                    }
                }
            } else if (oJSJaCPacket.getType() == "away") {
                $("#liveChat").append(messageAtom("", nameAndJID[arr[0].replace("@", "")] + "处于离开状态", "system", "", false));
                scrollToBottom();
            } else if (oJSJaCPacket.getType() == "subscribe") {//有添加好友请求
                getFriends();
                var numbers = 0;
                for (var i = 0; i < friendsArr.length; i++) {
                    if ((friendJID + "") == friendsArr[i]) {
                        numbers++;
                        $("#liveChat").append(messageAtom("", nameAndJID[arr[0].replace("@", "")] + "同意添加你为好友", "system", "", false));
                        scrollToBottom();
                        allow(friendJID + "");
                    }
                }
                if (numbers == 0) {
                    var aNode = $("<a></a>").text(friendJID + "请求添加你为好友(点击确认添加)");
                    $(aNode).attr("href", "javascript:void(0);");
                    $(aNode).bind("click", function () {
                        $("#friendsRequest").html("");
                        return allow(friendJID + "");
                    });
                    $("#friendsRequest").append(aNode);
                }
            } else if (oJSJaCPacket.getType() == "error") {
                if (isChatRoomPresence) {
                    if ($(xmlResponse).find('error').attr("code") == "407") {
                        $("#liveChat").append(messageAtom("", "您当前不是成员", "system", "", false));
                        layer.msg('您当前不是成员', function () {
                            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                            parent.layer.close(index); //再执行关闭
                        });
                        scrollToBottom();
                    } else {
                        //displayMessage($(xmlResponse).find('error').attr("code"), 2000)
                    }
                } else {
                    //displayMessage($(xmlResponse).find('error').attr("code"), 2000)
                }
            } else {
                //displayMessage(friendJID + oJSJaCPacket.getType(), 2000);
            }
        }
        else {
            //html += oJSJaCPacket.getShow() + '.</b>';
        }
        //if (oJSJaCPacket.getStatus())
        // html += ' (' + oJSJaCPacket.getStatus().htmlEnc() + ')';
    }
    //处理数据
    //document.getElementById('friendship').innerHTML += friends;
    //document.getElementById('iResp').innerHTML += html;
    //document.getElementById('iResp').lastChild.scrollIntoView();
}

function handleError(e) {
    console.log("An error occured:<br />" + ("Code: " + e.getAttribute('code') + "\nType: " + e.getAttribute('type') + "\nCondition: " + e.firstChild.nodeName).htmlEnc());
//            if (con.connected())
//                con.disconnect();
    if (e.firstChild.nodeName == "service-unavailable" || e.firstChild.nodeName == "not-authorized") {
        con.reConnectDisabled = true;
    }
}

function handleStatusChanged(status) {
    oDbg.log("status changed: " + status);
}

function handleConnected() {
    //document.getElementById('err').innerHTML = '';
    //con.send(new JSJaCPresence());
    online();
    if (!fromHeader) {
        var roomJID = $("#chatWithJID").val();
        joinChatRoom(roomJID);
    }
}

function handleDisconnected() {
    console.log('disconned' + new Date())
    if (!con.reConnectDisabled) {//出现错误时不再重连，避免不停发出请求
        reconnectSession();
        console.log('auto re-connect' + new Date());//自动重连
    }
    /*if(top != self){
     //弹出框中的聊天连接断开时
     layer.confirm("聊天连接已断开，是否重连？", {
     btn:["是", "否"]
     }, function() {
     location.href.reload();
     }, function() {
     var index = top.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
     top.layer.close(index); //再执行关闭
     });
     }else{
     //最外层会话连接断开后自动重连，避免无法接收openfire通知类消息
     con.connect(oArgs);
     console.log('auto re-connect');//自动重连
     }*/
}

function handleIqVersion(iq) {
    con.send(iq.reply([iq.buildNode('name', 'jsjac simpleclient'), iq.buildNode('version', JSJaC.Version), iq.buildNode('os', navigator.userAgent)]));
    return true;
}

function handleIqTime(iq) {
    var now = new Date();
    con.send(iq.reply([iq.buildNode('display', now.toLocaleString()), iq.buildNode('utc', now.jabberDate()), iq.buildNode('tz', now.toLocaleString().substring(now.toLocaleString().lastIndexOf(' ') + 1))]));
    return true;
}
/**
 * 该方法由init调用，最好不要单独调用
 * @param httpbases openfire服务器地址
 * @param domain domain
 * @param username openfire用户名
 * @param pass openfire密码
 * @param chatRoomJID 聊天室JID（如果没有则不传）
 * @returns {boolean}
 */
function doLogin(httpbases, domain, username, pass, chatRoomJID) {
    //document.getElementById('err').innerHTML = '';
    // reset
    try {
        con = new JSJaCHttpBindingConnection({
            httpbase: httpbases,
            oDbg: oDbg
        });
        setupCon(con);
        // setup args for connect method
        oArgs.httpbase = httpbases;
        oArgs.domain = domain;
        oArgs.username = username;
        oArgs.resource = fromHeader ? 'jsjac_simpleclient' : 'jsjac_simpleclient' + chatRoomJID.split("@")[0];//相当于客户端名称(相同resource会引起冲突)
        oArgs.pass = pass;
        oArgs.register = false;
        con.connect(oArgs);
        //if($("#isLogin").val() == "false"){
        //    con.connect(oArgs);
        //    parent.con = con;
        //}else if($("#isLogin").val() == "true"){
        //    con = parent.con;
        //    var roomJID = $("#chatWithJID").val();
        //    console.log(roomJID);
        //    joinChatRoom(roomJID);
        //}
    } catch (e) {
        //document.getElementById('err').innerHTML = e.toString();
        console.log(e);
    } finally {
        return false;
    }
}

function reconnectSession() {
    if (con) {
        con.reConnectDisabled = false;
    }
    doLogin(oArgs.httpbase, oArgs.domain, oArgs.username, oArgs.pass, "");
    //连接重连后，打开的聊天室需要重新进入
    setTimeout(function () {
        var chatRoomLayerMap = getLayer().chatRoomIndexMap;
        if (chatRoomLayerMap) {
            for (var chatRoomId in chatRoomLayerMap) {
                var index = chatRoomLayerMap[chatRoomId], chatIframe = $("#layui-layer" + index + " iframe");
                if (chatIframe.length) {
                    chatIframe[0].contentWindow.enterChatRoom();
                }
            }
        }
    }, 1000);
}


function setupCon(oCon) {
    oCon.registerHandler('message', handleMessage);
    oCon.registerHandler('presence', handlePresence);
    oCon.registerHandler('iq', handleIQ);
    oCon.registerHandler('onconnect', handleConnected);
    oCon.registerHandler('onerror', handleError);
    oCon.registerHandler('status_changed', handleStatusChanged);
    oCon.registerHandler('ondisconnect', handleDisconnected);

    oCon.registerIQGet('query', NS_VERSION, handleIqVersion);
    oCon.registerIQGet('query', NS_TIME, handleIqTime);
}
//离线(调用该方法可离线)
function quit() {
    var p = new JSJaCPresence();
    p.setType("unavailable");
    con.send(p);
    con.disconnect();
}
//离开(调用该方法可处于离开状态)
function away() {
    var p = new JSJaCPresence();
    p.setShow("away");
    con.send(p);
    //con.disconnect();
    return false;
}
//在线状态(调用该方法可在线)
function online() {
    var p = new JSJaCPresence();
    p.setShow("online");
    con.send(p);
    //con.disconnect();
    return false;
}

/**
 *  初始化（主要用于登录）
 * @param httpbases
 * @param domain
 * @param username
 * @param pass
 * @param isFromHeader 是否是有header.ftl调用
 * @param chatRoomJID
 */
function init(httpbases, domain, username, pass, isFromHeader, chatRoomJID) {
    fromHeader = isFromHeader;
    oDbg = new JSJaCConsoleLogger(4);
    /*try {// try to resume a session
     con = new JSJaCHttpBindingConnection({
     httpbase: httpbases,
     'oDbg': oDbg
     });
     setupCon(con);
     if (con.resume()) {
     //document.getElementById('err').innerHTML = '';
     }else{
     doLogin(httpbases, domain, username, pass, chatRoomJID);
     }
     } catch (e) {
     oDbg.log("setup connection error: " + e);
     } // reading cookie failed - never mind*/
    doLogin(httpbases, domain, username, pass, chatRoomJID);
}

onunload = function () {
    if (typeof con != 'undefined' && con && con.connected()) {
        // save backend type
        if (con._hold)// must be binding
            (new JSJaCCookie('btype', 'binding')).write();
        else
            (new JSJaCCookie('btype', 'polling')).write();
        if (con.suspend) {
            con.suspend();
        }
    }
};

function scrollToBottom() {
    if (window.scrollTimer) {
        clearTimeout(window.scrollTimer);
    }
    window.scrollTimer = setTimeout(scrollMsgToBottom, 200);
}

function scrollMsgToBottom() {
    $(".scroller").slimScroll({scrollTo: $(".scroller")[0].scrollHeight});
}

function updateRecentChatHistory() {
    if (window.chatHistoryTimer) {
        clearTimeout(window.chatHistoryTimer);
    }
    window.chatHistoryTimer = setTimeout(openMessageHistory, 2000);//2秒后刷新
}
