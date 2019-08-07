//获取好友列表
function getFriends() {
    var to = oArgs.username + '@' + oArgs.domain;
    var q = new JSJaCIQ();
    q.setTo(to);
    q.setType('get');
    q.setQuery('jabber:iq:roster');
    con.send(q);
    return false;
}
//接受好友请求
function allow(JID) {
    var p = new JSJaCPresence();
    p.setTo(JID + "");//发送请求的人
    p.setType('subscribed');//同意添加
    /*一些处理好友添加请求的类型
     unavailable -- Signals that the entity is no longer available for communication.
     subscribe -- The sender wishes to subscribe to the recipient's presence.
     subscribed -- The sender has allowed the recipient to receive their presence.
     unsubscribe -- The sender is unsubscribing from another entity's presence.
     unsubscribed -- The subscription request has been denied or a previously-granted
     */
    //发送类型为subscribed的Presence请求，代表接受添加好友
    con.send(p);
    p.setType('subscribe');
    //发送类型为subscribe的Presence请求，表示向对方发送添加好友请求，（Spark客户端自动默认处理确认添加,JSJaC需再次确认）
    con.send(p);
    setNickName(JID, JID.split("@")[0]);
    return false;
}
//设置昵称（为实现）
function setNickName(JID, nickName) {
    JID = "ofoaaccount02@rextec";
    nickName = "hahahahahha";
    var q = new JSJaCIQ();
    q.setType("set");
    q.setQuery("jabber:iq:roster");
    q.setQueryItem(q.getQuery(), JID, nickName);
    con.send(q);
    return false;
}
//添加好友
//TODO 添加了新的好友之后好友列表只能显示刚添加的好友，改正
function addFriends() {
    var inputArea = "<input type='text' id='inputArea' placeholder='用户ID'/>"
    inputArea += "<input type='button' value='发送' onclick='addFriend()'/>"
    $("#to").html(inputArea);
}
function addFriend() {
    var p = new JSJaCPresence();
    var to = "@" + oArgs.domain;
    if ($("#inputArea").val()) {
        if ($("#inputArea").val().indexOf(to) >= 0) {
            to = $("#inputArea").val().trim();
        } else {
            to = $("#inputArea").val().trim() + to;
        }
    } else {
        return false;
    }
    p.setTo(to);//对方的jid
    p.setType('subscribe');
    con.send(p);
    $("#to").html("");
    $(".chats").append(messageAtom("", "发送成功，等待对方回应", "system", ""));
    getFriends();
    return false;
}