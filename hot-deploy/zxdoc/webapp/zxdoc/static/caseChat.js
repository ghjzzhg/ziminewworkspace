//列出讨论组（会议室）
function listChatRoom() {
    var q = new JSJaCIQ();
    q.setTo('conference.rextec');
    q.setType('get');
    q.setQuery('http://jabber.org/protocol/disco#items');
    con.send(q);
    return false;
}


//从数据库中取出讨论组成员
function listMembers(roomJID) {
    $.ajax({
        type: "post",
        async: false,
        url: "GetChatRoomInfo",
        data: {"charRoomJID": roomJID},
        dataType: "json",
        success: function (data) {
            //console.log(data);
            var content = "<table class='altrowstable' id='alternatecolor'>";
            for (var i = 0; i < data.data.members.length; i++) {
                var memberData = data.data.members[i];
                var parameters = {
                    id: memberData.openFireJid,
                    name: memberData.fullName,
                    groupName: memberData.groupName,
                    personType: memberData.personType,
                    class: chatRoomMembers.indexOf(data.data.membersJIDS[i].split("@")[0]) >= 0 ? "col-xs-8 online" : "col-xs-8 offline",
                    event: memberData.canRemove ? "removeChatRoomMember(this)" : "",
                    isDelete:memberData.canRemove?"删除":"",
                    partyId: memberData.partyId,
                    externalLoginKey: getExternalLoginKey()
                }

                memberNames[memberData.partyId] = memberData.name;
                content +=  Mustache.render("<tr class='{{class}}' style='text-align: center;width: 500px;'>" +
                    "<td class='col-xs-1'>" +
                    "<img class='avatar' style='width: 30px; height: 30px;' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey={{externalLoginKey}}'/>" +
                    "</td><td class='col-xs-2'><div id='{{id}}' class='members'>{{name}}</div>" +
                    "</td><td class='col-xs-2'><div>{{personType}}</div>" +
                    "</td><td class='col-xs-4'><div>{{groupName}}</div></td>", parameters);
                if(memberData.canRemove) {
                    content += Mustache.render("<td class='col-xs-1'><a href='#nowhere' onclick='{{event}}' id='{{id}}'>{{isDelete}}</a></td></tr>", parameters);
                }else{
                    content += Mustache.render("<td  class='col-xs-1'></td></tr>", parameters);
                }
            }
            content += "</table>";
            $("#membersContent").html(content);
            //由于取文件的操作依赖于成员的name，所以放在后面
            listFiles();
            $("#chatTheme").text(data.data.roomInfo.roomTheme);
            $("#roomName").val(data.data.roomInfo.roomName);
        }
    });
}
//列出文件列表
var listFileTimer;
function listFiles() {
    if($("#chatWithJID").val()){
        if(listFileTimer){
            clearTimeout(listFileTimer);
        }
        listFileTimer = setTimeout(listFiles, 3000);

        function showLoader(msg) {
            //覆盖showLoader方法，避免定时检测session时出现遮罩层
        }
        var oriLoader = window.showLoader;
        window.showLoader = function(){

        }
        $.ajax({
            type: 'post',
            url: 'GetFileList',
            async: false,
            data:{chatRoomId: $("#chatWithJID").val()},
            dataType: 'json',
            success: function (content) {
                window.showLoader = oriLoader;
                //console.log(content.files);
                var finalDocList = "", officeEditHtml = $("#officeEdit").html(), pdfViewHtml = $("#pdfView").html();
                for (var i = 0; i < content.files.fileId.length; i++) {
                    //TODO 从服务器传递文档的类型、可编辑状态canEdit、版本、最后编辑者、最后编辑的时间
                    var fileId = content.files.fileId[i], sizeExceed = content.files.sizeList[i], lockedBy = content.files.lockedBy[i], editable = !lockedBy || lockedBy == top.oArgs.username, unlockable = lockedBy && lockedBy == top.oArgs.username && thisDocumentId != fileId, joinable = lockedBy && lockedBy != top.oArgs.username && thisDocumentId != fileId;
                    var fileName = content.files.fileNames[i].toLowerCase(), lockedId = content.files.lockedBy[i], fileType = fileName.indexOf(".doc") > -1 ? "word" : (fileName.indexOf(".xls") > -1 ? "excel" : fileName.indexOf(".ppt") > -1 ? "point" :fileName.indexOf(".pdf") > -1 ? "pdf" : "image");
                    var contentData = {
                        fileId: fileId,
                        fileType: fileType,
                        fileName: fileName,
                        fileVersion: content.files.versionList[i],
                        fileUpdateInfo: '',
                        editable: editable,
                        unlockable: unlockable,
                        joinable: joinable,
                        fileLocker: memberNames[lockedBy],
                        fileSizeNotOk: sizeExceed,
                        fileSizeOk: !sizeExceed
                    };
                    if(fileType == "word" || fileType == "excel" || fileType == "point"){
                        /*if (!lockedBy) {
                            //由当前登陆人锁定并且右侧文件信息中不展示的情况下，显示解锁按钮
                            if(lockStatus == 'locked' && editFile != content.files.fileId[i]){
                                finalDocList += Mustache.render(officeUnlocked, contentData);
                            }else{
                                finalDocList += Mustache.render(officeEditHtml, contentData);
                            }
                        }else{
                            finalDocList += Mustache.render(officeViewHtml, $.extend(contentData, {fileLocker: memberNames[lockedBy]}));
                        }*/
                        finalDocList += Mustache.render(officeEditHtml, contentData);
                    }else if(fileType == "pdf"){
                        finalDocList += Mustache.render(pdfViewHtml, contentData);
                    }else{
                        finalDocList += Mustache.render(pdfViewHtml, contentData);//图片用弹出框形式打开
                    }
                }
                $("#fileList").html(finalDocList);

                /*$("#fileList a.attachment-image").fancybox({
                    'overlayShow':false,
                    'transitionIn':'elastic',
                    'transitionOut':'elastic',
                    afterLoad: addIframe
                });*/
            }
        });
    }
}
//离开讨论组
function leaveChatRoom(roomJID) {
    $("#chatTheme").text("");
    var p = new JSJaCPresence();
    //roomJID = "aaa@conference.rextec";//临时（应传参数）
    p.setTo(roomJID + '/' + top.oArgs.username);
    p.setType('unavailable');
    p.setStatus('');
    top.con.send(p);
    // top.reconnectTopSession();
    return false;
}

//讨论组发消息
function sendMessageInChatRoom(msg, roomJID) {
    var m = new JSJaCMessage();
    m.setBody(msg);
    m.setTo(roomJID);
    //m.setFrom('');
    m.setType('groupchat');
    top.con.send(m);
    return false;
}


//发送聊天室邀请（写为发送普通消息）
//发送多人聊天邀请
function sendInvite(friendJID, chatRoomJID, roomName) {
    sendInviteMsg("CHATROOM*INVITE:", friendJID, chatRoomJID, roomName);
}
//发送私聊聊邀请
function sendPrivateInvite(friendJID, chatRoomJID, roomName) {
    sendInviteMsg("PRIVATEROOM*INVITE:", friendJID, chatRoomJID, roomName);
}
function sendInviteMsg(prefix, friendJID, chatRoomJID, roomName) {
    try {
        if(!con.connected()){//如果发现未连接，重连
            con.reConnectDisabled = false;
            con.connect(oArgs);
        }
        var inviteBody = prefix + chatRoomJID + ":" + roomName;
        var oMsg = new JSJaCMessage();
        oMsg.setTo(new JSJaCJID(friendJID));
        oMsg.setBody(inviteBody);
        oMsg.setType('chat');
        con.send(oMsg);
        return false;
    } catch (e) {
        //html = "<div class='msg error''>Error: " + e.message + "</div>";
        //document.getElementById('iResp').innerHTML += html;
        //document.getElementById('iResp').lastChild.scrollIntoView();
        console.log(e);
        return false;
    }
}
//聊天室添加成员
function addMember() {
    $("#ntko-container").hide();
    var result = "";
    for (var i = 0; i < $(".members").length; i++) {
        result += $($(".members")[i]).attr("id") + ",";
    }
    result = result.substring(0, result.length - 1);
    $.ajax({
        type: 'post',
        async : false,//此处要同步,必须添加成功再邀请
        url: "GetOtherMembers",
        data: {"arr": result, "chatRoomJID": $("#chatWithJID").val()},
        dataType: 'json',
        success: function (data) {
            var content = "";
            if((data.members&&data.manages.length > 0)||(data.manages&&data.members.length > 0)) {
                if (data.members && data.members.length > 0) {
                    content += "<div style='text-align: left;color: green;margin-left: 10px;'>本方人员</div>";
                    var externalLoginKey = getExternalLoginKey();
                    for (var i = 0; i < data.members.length; i++) {
                        var memberData = data.members[i];
                        var parameters = {
                            name: memberData.fullName ? memberData.fullName : memberData.openFireJid,
                            event: "addAndInvite('" + memberData.openFireJid + "')",
                            externalLoginKey: externalLoginKey,
                            partyId: memberData.partyId,
                            jid: memberData.openFireJid
                        }
                        content += Mustache.render("<div style='text-align: left' jid='{{jid}}'><img class='avatar' style='width: 51px; height: 51px;margin:5px 10px;' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey={{externalLoginKey}}'/><a href='javascript:void(0);' onclick='{{event}}'>{{name}}</a></div>", parameters)
                    }
                }
                if (data.manages && data.manages.length > 0) {
                    var externalLoginKey = getExternalLoginKey();
                    for (var i = 0; i < data.manages.length; i++) {
                        var memberData = data.manages[i];
                        var parameters = {
                            groupName: memberData.groupName,
                            name: memberData.fullName ? memberData.fullName : memberData.openFireJid,
                            event: "addAndInvite('" + memberData.openFireJid + "')",
                            externalLoginKey: externalLoginKey,
                            partyId: memberData.partyId,
                            jid: memberData.openFireJid
                        }
                        content += Mustache.render("<div style='text-align: left;color: red;margin-left: 10px;'>{{groupName}}</div><div style='text-align: left' jid='{{jid}}'><img class='avatar' style='width: 51px; height: 51px;margin:5px 10px;' alt='' src='/content/control/partyAvatar?partyId={{partyId}}&externalLoginKey={{externalLoginKey}}'/><a href='javascript:void(0);' onclick='{{event}}'>{{name}}</a></div>", parameters)
                    }
                }
            }
            else {
                content = "无可添加人员";
            }
            layer.open({
                type: 1,
                title:"邀请人员",
                offset: ['200px', '300px'],
                // move: false,
                area: ['240px', '420px'], //宽高
                content: Mustache.render("<center>" + content + "</center>", parameters),
                end:function (){
                    $("#ntko-container").show();
                }
            });
        }
    })
}

//移除聊天室成员
function removeChatRoomMember(obj){
    $("#ntko-container").hide();
    var memberJID = $(obj).attr("id");
    var roomId = $("#chatWithJID").val();
    hideNtko();
    layer.confirm('是否移除该成员？', {
        offset: ['200px', '300px'],
        btn: ['是','否'],cancel: function(index, layero){
            showNtko();
        }, //按钮
        end:function (){
            showNtko();
        }
    }, function(){
        $.ajax({
            url: "RemoveChatRoomMember",
            data: {"memberJID": memberJID, "roomJID": $("#chatWithJID").val()},
            type: "post",
            async:false,
            success: function(content){
                sendChatMessage("YOU*HAVE*BEEN*REMOVED:" + $("#chatWithJID").val() + ":" + $("#roomName").val(), memberJID);
                sendChatMessage("MEMBER*REMOVED:" + document.getElementById(memberJID).innerHTML, $("#chatWithJID").val());
                $(obj).parent().parent().remove();
                layer.msg(content.msg, {icon: 1});
                //需求变更：删除项目经理所在公司的其他用户
                var xml = content.xmlString;
                if(xml){
                    var xmlResponse = $.parseXML(xml);
                    var jsonResponse = xmlToJson(xmlResponse);
                    var members = jsonResponse.chatRoom.members;
                    removeChatRoomTeamMember(members,roomId,memberJID);
                }
            }
        })
    }, function(index){
        layer.close(index);
    });
}
//删除项目经理所在公司的其他用户
function removeChatRoomTeamMember(members,roomId,memberJID) {
    var member = members.member;
    for(var i=0;i<member.length;i++)
    {
        var memberId = member[i]["#text"];
        $.ajax({
            url: "RemoveChatRoomTeamMember",
            data: {"memberJID": memberId, "roomJID": roomId,parentId:memberJID},
            type: "post",
            success:function(data)
            {}
        })
    }
}
//添加并邀请
function addAndInvite(JID) {
    //TODO 现邀请，接受后再添加进聊天室
    top.sendInvite(JID, $("#chatWithJID").val(), $("#chatTheme").text());
    $.ajax({
        type: 'post',
        url: 'AddUserToChatRoom',
        data: {"JID": JID, "roomName": $("#chatWithJID").val()},
        dataType: "json",
        success: function (data) {
            $("div[jid='" + JID + "']").remove();
            listMembers($("#chatWithJID").val());
            showInfo(data.data);
        }
    })
}

//查找聊天记录
/**
 *
 * @param fromJID 从该获取和该JID的聊天记录
 * @param keyWords 关键字（查询条件）
 * @param start 开始日期
 * @param end 截止日期
 * @param limit 页数（分页用）
 */
function searchChatHistory(fromJID, keyWords, start, end, limit) {
    var iq = new JSJaCIQ();
    iq.setType('get');
    iq.setID(fromJID.split('@')[0]);
    iq.setZdlist(fromJID, keyWords, start, end, limit);
    // iq.setQueryChatRecord(iq.getQuery(), fromJID);
    top.con.send(iq);
}
//查找某条聊天记录的会话记录（根据关键字查找时每条记录都会对应一次会话，该方法可以找出该会话的相关的聊天记录）
/**
 *
 * @param conversationId 会话Id
 * @param limit 页数（分页相关）
 */
function searchChatConversation(conversationId, limit) {
    var iq = new JSJaCIQ();
    iq.setType('get');
    iq.setKclist(conversationId, limit, $("#chatWithJID").val());
    top.con.send(iq);
}