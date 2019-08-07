var webSocket;

// Create a new instance of the websocket
webSocket = new WebSocket('ws://' + window.location.host + '/zxdoc/ws/msg');

webSocket.onopen = function (event) {
    console.log(event);
};

webSocket.onmessage = function (event) {
    console.log(event);
    if(event){
        var data = event.data, json = true;
        try {
            data = JSON.parse(data);
        } catch (e) {
            json = false;
        }

        if(!json){
            if("kickedByOtherSession" == data){
                showUserSessionExpireAlert();
            }
        }else if(Array.isArray(data)){

        }else{
            if(data.type == "task"){
                createTaskLi(data);
            }else{
                var link = Mustache.render($("#noticeTpl").html(), {
                    clickAction: data.titleClickAction,
                    title: data.title,
                    noticeId: data.id,
                    time: data.createdStamp.substring(11)
                });
                $("#messageList").prepend(link);
                if($("#messageList").find("li").length > 0){
                    $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy").pulsate({
                        color: "#bf1c56"
                    });
                }else{
                    $(".msg-number").text($("#messageList").find("li").length)
                }
            }
        }
    }
};

webSocket.onerror = function (event) {
    console.log(event);
};
webSocket.onclose = function (event) {
    console.log(event);
    showUserSessionExpireAlert();
};

function createTaskLi(data){
    var caseNum = $("#caseNum").html();
    if(caseNum){
        caseNum = parseInt(caseNum);
    }else{
        caseNum = 0;
    }
    var title = data.title, oriTitle = title, clickAction = data.titleClickAction;
    if(title.length > 20){
        title = title.substring(0,20) + "...";
    }
    $("#caseList").append('<li style="float: left;width: 100%"><a style="width:100%" href="#nowhere" title="' + oriTitle + '">' +
        '<span class="subject" ' + (clickAction ? ' onclick="' + clickAction + '"' : '') + '><span class="from" style="font-size: smaller;"> ' + title + '</span><span class="time"></span></span><span class="message"></span></a>' +
        '</li>')
    $("#caseNum").html(caseNum + 1);
    $(".case-number").text(caseNum + 1);
}