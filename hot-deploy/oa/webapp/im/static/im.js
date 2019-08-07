$.im = {
    tt:{},
    uuid:0,
    sockets:{},
    commonRoom:'C00000',
    initChatListener: function(path){

        //初始化都是默认显示正在聊天Tab 故显示发送消息toolbarBottom隐藏查询历史记录toolbarBottom
        $.im.operateChattingRecord();
        //获取正在进行的消息内容（以字符串形式存放在Local Storage里）
        var chatMessage = amplify.store("chattingRecord");

        //如果Local Storage里有消息则直接调用，不必请求服务器
        if(chatMessage != undefined){
            var chatRecordList = chatMessage.split("∫");
            var content = "";
            for(var i=0;i<chatRecordList.length;i++){
                var chatRecord = chatRecordList[i].split("∮");
                var author_s = $("#author_s").val();
                var author = chatRecord[0];
                var time = chatRecord[1];
                var message = chatRecord[2];
                if(author_s != author){
                    content += '<div class="item out">';
                }else{
                    content += '<div class="item">'
                }
                content +=  '<div class="image"><img src="/im/static/img/user-empty.png" class="img-polaroid" width="50px"></div>'+
                    '<div class="date" style="margin-top: 3px">'+time+'</div>'+
                    '<div class="text">'+
                    '<a href="#" class="msgAuthor">'+author+'</a>'+
                    '<p class="msgContent">'+message+'</p>'+
                    '</div>'+
                    '</div>';
            }

            $('<div></div>').html(content).children().appendTo("#msgRecords");
        }


        //获取上一次消息框状态（展开or收回）
       /* var operateMsgFlag = amplify.store(oriWrapId + "_op");
        if(operateMsgFlag != undefined){
            $("#presence_widgetstatus").attr("op",operateMsgFlag);
            $.im.operateMsg();
        }*/

        var socket = atmosphere;
        var subSocket;
        var transport = 'websocket';
        var fallbackTransport = 'long-polling';
        var room = path?"U" + path : $.im.commonRoom;
        var request = { url: '/im/atmosphere/' + room + "?author=" + $("#author_s").val(),
            contentType : "application/json",
            //logLevel: 'debug',
            transport: transport,
            trackMessageLength: true,
            reconnectInterval: 10000,
            fallbackTransport: fallbackTransport
        };

        /*request.onTransportFailure = function(errorMsg, request) {
            jQuery.atmosphere.info(errorMsg);
            if (window.EventSource) {
                request.fallbackTransport = "sse";
                transport = "see";
            }
        };*/

        request.onOpen = function(response) {
            transport = response.transport;
            $.im.uuid = response.request.uuid;
        };
        request.onClose = function(response) {
            subSocket.push(atmosphere.util.stringifyJSON({ author: $("#author_s").val(), message: 'disconnecting' }));
        };

        request.onMessage = function (response) {
            var responesMessage = response.responseBody;
            try {
                var json = atmosphere.util.parseJSON(responesMessage), author = json.author, msg = json.message;
                if("上线" == msg || "下线" == msg){
                    showInfo(author + msg);
                    var onlineUsers = json.users, deptTree = $.treeInline['dataScopeDeptTree2'].tree, staffNodes = deptTree.getNodesByParam("dataType", "staff", null);

                    if(staffNodes.length){
                        for(var i in staffNodes){
                            var node = staffNodes[i];
                            var imStatus = node.imStatus || '';
                            if($.inArray(node.id, onlineUsers) > -1){
                                node.imStatus = 'online';
                                node.iconSkin = 'icon01';
                            }else{
                                node.imStatus = 'offline';
                                node.iconSkin = 'icon02';
                            }
                            deptTree.updateNode(node);
                        }
                    }

                    var deptNodes = deptTree.getNodesByParam("dataType", "dept", null);
                    if(deptNodes.length){
                        for(var i in deptNodes){
                            var dept = deptNodes[i];
                            var deptStaff = deptTree.getNodesByParam("dataType", "staff", dept), totalStaff = 0, onlineStaff = 0;
                            if(deptStaff.length){
                                totalStaff = deptStaff.length;
                                for(var j in deptStaff){
                                    if(deptStaff[j].imStatus == 'online'){
                                        onlineStaff ++;
                                    }
                                }
                            }
                            dept.name = dept.oriName + "<span style='color: lightseagreen'>(<span" + (onlineStaff > 0 ? " style='color: orangered'" : "") + ">" + onlineStaff + "</span>/" + totalStaff + ")</span>";
                            deptTree.updateNode(dept);
                        }
                    }

                    return;
                }
                var author_s = $("#author_s").val();
                var msgTo = author;
                var msgTime = new Date(json.time);
                $("#msgRecord").find(".date").text( moment(msgTime).format('YYYY-MM-DD hh:mm:ss a'));
                $("#msgRecord").find(".msgAuthor").text(json.author);
                $("#msgRecord").find(".msgContent").text(json.message);

                if(author_s == author){
                    $("#msgRecord > div").clone().css("display","block").addClass("item out").appendTo(".userWin.active .msgRecords") ;
                    msgTo = $(".userWin.active").attr("id");
                }else if(msg){
                    if(author && !$("#" + author).length){
                        $.im.createUserChatWin(author);
                    }
                    $("#chatWindow").show();
                    if($("#chatWindow .roster_title_collapsed").css("display")=="block"){
                        if($.im.tt){
                            clearTimeout($.im.tt);
                        }
                        $.im.tt = setTimeout(function(){$.im.changeColor();} ,500);
                    }
                    $("#msgRecord > div").clone().css("display","block").addClass("item").appendTo(".userWin.active .msgRecords") ;
                }

                //滚动到最底
                var $panels = $("#chatWindow .panels");
                if($panels.length > 0){
                    $panels.get(0).scrollTop = $panels.get(0).scrollHeight;
                }

                //将正在聊天的信息放入local storage里 (expires有效时间)
                var msgRecordList = "";
                var msgRecords_length = $(".userWin.active").find("div.item").length;
                for(var i=0;i<msgRecords_length;i++){
                    var record = $(".userWin.active").find("div.item:eq("+i+")");
                    var time = record.find(".date").text();
                    var msgAuthor = record.find(".msgAuthor").text();
                    var message = record.find(".msgContent").text();
                    msgRecordList += "∫"+msgAuthor+"∮"+time+"∮"+message
                }
                var chattingRecordValue = msgRecordList.substr(1,msgRecordList.length);
                amplify.store("chattingRecord_" + author_s + "_" + msgTo,chattingRecordValue,{expires:3600000});
            } catch (e) {
                return;
            }
        };

        subSocket = socket.subscribe(request);
        $.im.sockets[room] =subSocket;

    //初始化聊天窗口
        if(!$("#chat-tabs").hasClass("yui3-tabview-content")){
            YUI({
                base:'/images/lib/yui/'
            }).use('tabview', 'escape', 'plugin', function (Y) {
                var Removeable = function(config) {
                    Removeable.superclass.constructor.apply(this, arguments);
                };

                Removeable.NAME = 'removeableTabs';
                Removeable.NS = 'removeable';

                Y.extend(Removeable, Y.Plugin.Base, {
                    REMOVE_TEMPLATE: '<a class="yui3-tab-remove yui3-tab-label" style="background: none;background-color: lightgrey;color:red;" title="关闭">x</a>',

                    initializer: function(config) {
                        var tabview = this.get('host'),
                            cb = tabview.get('contentBox');

                        cb.addClass('yui3-tabview-removeable');
                        cb.delegate('click', this.onRemoveClick, '.yui3-tab-remove', this);

                        // Tab events bubble to TabView
                        tabview.after('tab:render', this.afterTabRender, this);
                    },

                    afterTabRender: function(e) {
                        // boundingBox is the Tab's LI
                        e.target.get('boundingBox').append(this.REMOVE_TEMPLATE);
                    },

                    onRemoveClick: function(e) {
                        e.stopPropagation();
                        var tab = Y.Widget.getByNode(e.target);
                        tab.remove();
                        //最后一个删除后隐藏窗口
                        if(!$.im.chatTab.get('selection')){
                            $("#chatWindow").hide();
                        }
                    }
                });

                var tabview = new Y.TabView({srcNode:'#chat-tabs', plugins: [Removeable]});
                tabview.render();
                $.im.chatTab = tabview;
            });
        }
    },
    sendMsg: function(room, user){
        if(room == $.im.commonRoom){//一对一
            var $msg = $("#" + user + " textarea[name=chatMsg]");
            var message = $msg.val();
            var author =  $("#author_s").val();
            if(message){
                message = {from: author, to: user, message: message};
                $.im.sockets[room].push(atmosphere.util.stringifyJSON(message));
                $msg.val("");
                $msg.focus();
            }
        }else{//群组
            var message = { author: author, message: message, uuid: $.im.uuid };
            $.im.sockets[room].push(atmosphere.util.stringifyJSON(message));
        }
    },
    operateChattingRecord: function (){
            $("#toolbarBottom_search").hide();
            $("#toolbarBottom").show();
            var $panels = $("#panels");
            if($panels.length > 0){
                $panels.get(0).scrollTop = $panels.get(0).scrollHeight;
            }
        },
    operateMsg: function (wrapId, hideWidth, showWidth){
//消息框的展开和收回的切换
        var oriWrapId = wrapId;
            wrapId = wrapId?"#" + wrapId : "";
            hideWidth = hideWidth||"120px";
            showWidth = showWidth||"300px";
        var widgetStatus = $(wrapId + " .presence_buddylist_header");
            var op = widgetStatus.attr(oriWrapId + "_op");
            if(!op || op == "toMsgDetail"){
                if($.im.tt){
                    clearTimeout($.im.tt);
                    $(wrapId + " .buddy_list_min_text").removeClass("buddy_list_min_text_active");
                    widgetStatus.removeClass("presence_buddylist_header_active");
                }
                $(wrapId + " .chat_buddy_list_pane").addClass("chat_pan_active");
                $(wrapId + " .roster_title_collapsed").hide();
                $(wrapId + " .chat_buddy_list_content_wrapper").css("display","block").css("width", showWidth)  ;
                $(wrapId + " .roster_title_expanded").css("display","block").css("width", showWidth) ;
                widgetStatus.css("width", showWidth);
                $(wrapId + " .chat_buddy_list_main_box").css("width", showWidth);
                $(wrapId + ".presence_chat_widget").addClass("presence_chat_widget_detail");

                widgetStatus.attr(oriWrapId + "_op","toMsg");
                amplify.store(oriWrapId + "_op", op);
                var $panels = $(wrapId + " .panels");
                if($panels.length > 0){
                    $panels.get(0).scrollTop = $panels.get(0).scrollHeight;
                }
            }else{
                $(wrapId + " .chat_buddy_list_pane").removeClass("chat_pan_active");
                $(wrapId + ".presence_chat_widget").removeClass("presence_chat_widget_detail");
                $(wrapId + " .roster_title_collapsed").show();
                $(wrapId + " .chat_buddy_list_content_wrapper").hide();
                $(wrapId + " .roster_title_expanded").hide() ;
                widgetStatus.css("width",hideWidth);
                $(wrapId + " .chat_buddy_list_main_box").css("width",hideWidth);

                widgetStatus.attr(oriWrapId + "_op","toMsgDetail");
                amplify.store(oriWrapId + "_op",op);

            }
        },
    changeColor: function (){
            //当有消息时，消息图标不断切换颜色以提示有新消息
            var wrapId = "#chatWindow";
            var buddyText = $(wrapId + " .buddy_list_min_text");
            if(buddyText.hasClass("buddy_list_min_text_active")){
                buddyText.removeClass("buddy_list_min_text_active");
            }else{
                buddyText.addClass("buddy_list_min_text_active");
            }

            var widgetStatus = $(wrapId + " .presence_buddylist_header");
            if(widgetStatus.hasClass("presence_buddylist_header_active")){
                widgetStatus.removeClass("presence_buddylist_header_active");
            }else{
                widgetStatus.addClass("presence_buddylist_header_active");
            }
            if($.im.tt){
                clearTimeout($.im.tt);
            }
            $.im.tt = setTimeout(function(){$.im.changeColor();}, 500);
        },
    searchMsgRecord:function (param){

        //查询历史记录（1：消息记录tab（"all"） ；2：消息记录tab下的精确查询("condition")）
        var conditionFlag = param;
        var firstMsgRecordTime = $("#msgRecords").find(".date:first").text();
        var  startTime = "";
        var  endTime = "";
        if(conditionFlag == "all"){
            $("#startTime").val("开始时间");
            $("#startTime").css("color","rgb(192, 192, 192);");
            $("#endTime").val("结束时间");
            $("#endTime").css("color","rgb(192, 192, 192);");

            var chatRecordTOs = amplify.store("chatHistoryRecords");
            if(chatRecordTOs != undefined){
                $.im.getChattingRecordValue(chatRecordTOs);
            }else{
                $.im.ajax_getHistoryRecord(conditionFlag,firstMsgRecordTime,startTime,endTime)
            }
        }else{
            startTime = $("#startTime").val();
            if(startTime == "开始时间"){
                startTime = "";
            }
            endTime = $("#endTime").val();
            if(endTime == "结束时间"){
                endTime = "";
            }
            $.im.ajax_getHistoryRecord(conditionFlag,firstMsgRecordTime,startTime,endTime)
        }
    },
    ajax_getHistoryRecord:function (param,firstMsgRecordTime,startTime,endTime){
        var conditionFlag = param;
        /*$.ajax({
         type:"post",
         dataType:"html",
         url:appCtx+"/chat/getHistoryRecord",
         async:true,
         data:{"firstMsgRecordTime":firstMsgRecordTime,"startTime":startTime,"endTime":endTime,"conditionFlag":conditionFlag},
         success:function(data){
         $("#chatRecords").html("");
         $('<div></div>').html(data).children().appendTo("#chatRecords");
         $("#toolbarBottom").attr("style","display:none");
         $("#toolbarBottom_search").attr("style","display:block");
         document.getElementById('panels').scrollTop = document.getElementById('panels').scrollHeight;
         if(conditionFlag == "all"){
         var chattingRecordValue = $("#chatRecords").html();
         amplify.store("chatHistoryRecords",chattingRecordValue,{expires:3600000});
         }
         }
         })*/

        $.ajax({
            type:"post",
            dataType:"json",
            url: "/im/atmosphere/chat/getHistoryRecord",
            async:true,
            data:{"firstMsgRecordTime":firstMsgRecordTime,"startTime":startTime,"endTime":endTime,"conditionFlag":conditionFlag},
            success:function(data){
                var chatRecordTOs = data.data;
                $.im.getChattingRecordValue(chatRecordTOs);
                if(conditionFlag == "all"){
                    var chattingRecordValue = chatRecordTOs;
                    amplify.store("chatHistoryRecords",chattingRecordValue,{expires:3600000});
                }
            }
        })
    },
    getChattingRecordValue:function (chatRecordTOs){
//ajax获取到的数据组合成历史记录的html
        var content ='';
        if(chatRecordTOs != null && chatRecordTOs.length>1){
            for(var i=0;i<chatRecordTOs.length;i++){
                var chatRecordTO = chatRecordTOs[i];
                var time = chatRecordTO.formatTime;
                var name = chatRecordTO.chatRecord.name;
                var message = chatRecordTO.chatRecord.message;
                var author_s = $("#author_s").val();
                if(author_s != name){
                    content += '<div class="item">';
                }else{
                    content += '<div class="item out">'
                }

                content +=  '<div class="image"><img src="/im/static/img/user-empty.png" class="img-polaroid" width="50px"></div>'+
                    '<div class="date" style="margin-top: 3px">'+time+'</div>'+
                    '<div class="text">'+
                    '<a href="#" class="msgAuthor">'+name+'</a>'+
                    '<p class="msgContent">'+message+'</p>'+
                    '</div>'+
                    '</div>';
            }
            content += '<div style="text-align: center; margin-top: 20px;">以上是历史记录</div><hr/>';
        }else{
            content += '<div style="text-align: center; margin-top: 20px;">暂无历史记录</div>';
        }

        $("#chatRecords").html("");
        $('<div></div>').html(content).children().appendTo("#chatRecords");
        $("#toolbarBottom").hide();
        $("#toolbarBottom_search").css("display","block");
        var $panels = $("#panels");
        if($panels.length > 0){
            $panels.get(0).scrollTop = $panels.get(0).scrollHeight;
        }
    },
    chatTabMap : {},
    createUserChatWin: function(user){
        $("#chatWindow").show();
        var staffNode = $.treeInline['dataScopeDeptTree2'].tree.getNodeByParam("id", user, null), staffName = user;
        if(staffNode){
            staffName = staffNode.name;
        }

        if(!$("#chatWindow").hasClass("presence_chat_widget_detail")){
            $.im.operateMsg('chatWindow', '200px', '500px');
        }
        $(".userWin.active").removeClass("active");
        var userWin = $("#" + user);
        if(userWin.length){
            userWin.addClass("active");
            $.im.chatTab.selectChild($.im.chatTabMap[user]);
        }else{
            var winHtml = '<div id="' + user + '" class="userWin active">' +
                '<fieldset class="scheduler-border"><legend class="scheduler-border">消息记录</legend><div class="block messaging" style="height: 260px;">' +
                '<div class="msgRecords panels" style="height: 260px;overflow-y: auto"></div>' +
                '</div></fieldset><fieldset class="scheduler-border"><legend class="scheduler-border">发送消息(ctrl+回车)</legend>' +
                '<div class="toolbar bottom" style="height: 60px;">' +
                '<div style="display: block;">' +
                '<textarea rows="" cols="" class="fmDataMultiLine" name="chatMsg" style="width: 395px; margin-left: 5px;margin-right: 15px; min-height: 60px; border:1px solid lightskyblue"></textarea>' +
                '<span class="glyphicon glyphicon-new-window tipl" style="cursor: pointer;vertical-align: middle;font-size:2.8em;color:lightseagreen;" title="发送消息" onclick="$.im.sendMsg(\'' + $.im.commonRoom + '\',\'' + user + '\')"></span>' +
                '</div>' +
                '</div>' +
                '</div></fieldset>';
            //$("#chatDetails").prepend(winHtml);
            var maxIndex = $.im.chatTabMap.max || -1;
            maxIndex ++;
            $.im.chatTab.add({"label": staffName, "content": winHtml, "index": maxIndex}, maxIndex);
            $.im.chatTab.selectChild(maxIndex);
            $.im.chatTabMap.max = maxIndex;
            $.im.chatTabMap[user] = maxIndex;
            $.im.restoreLocalHistory(user);
            $("#" + user + " textarea").keypress(function(e){
                if(e.ctrlKey && e.which == 13 || e.which == 10) {
                    $.im.sendMsg($.im.commonRoom, user);
                }
            })
        }
    },
    restoreLocalHistory: function(user){
        var author_s = $("#author_s").val();
        //获取正在进行的消息内容（以字符串形式存放在Local Storage里）
        var chatMessage = amplify.store("chattingRecord_" + author_s + "_" + user);

        //如果Local Storage里有消息则直接调用，不必请求服务器
        if(chatMessage){
            var chatRecordList = chatMessage.split("∫");
            var content = "";
            for(var i=0;i<chatRecordList.length;i++){
                var chatRecord = chatRecordList[i].split("∮");
                var author = chatRecord[0];
                var time = chatRecord[1];
                var message = chatRecord[2];
                if(author_s != author){
                    content += '<div class="item">';
                }else{
                    content += '<div class="item out">'
                }
                content +=  '<div class="image"><img src="/im/static/img/user-empty.png" class="img-polaroid" width="50px"></div>'+
                    '<div class="date" style="margin-top: 3px">'+time+'</div>'+
                    '<div class="text">'+
                    '<a href="#" class="msgAuthor">'+author+'</a>'+
                    '<p class="msgContent">'+message+'</p>'+
                    '</div>'+
                    '</div>';
            }

            $(".userWin.active .msgRecords").append(content);
        }
    }

}







