<script type="application/javascript">
    $(function () {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });
        $("#chatRoomDetails").validationEngine("attach", {promptPosition: "bottomLeft",focusFirstField : false,scroll:false});
    })
    //创建聊天室
    function createChatRoom() {
        var options = {
            beforeSubmit: function() {
                return $("#chatRoomDetails").validationEngine("validate", {promptPosition: "bottomLeft",focusFirstField : false,scroll:false});
            },
            type: "POST",
            url: "CreateChatRoom",
            dataType: "json",
            success: function(data) {
                for(var i = 0; i < data.members.length; i ++){
                    top.sendInvite(data.members[i], data.chatRoomJID, data.chatRoomName);
                }
                parent.chatRoomInfo = data;
                showInfo(data.data);
                //发送聊天室邀请
                //加入聊天室
                closeCurrentTab();

                //
            }
        }
        $("#chatRoomDetails").ajaxSubmit(options);
    }
</script>
<form id="chatRoomDetails">
    <input type="hidden" name="caseId" value="${caseId}"/>
    <table class="table table-hover table-striped table-bordered">
        <tbody>
        <tr>
            <td><label class="control-label">协作主题<span class="required"> * </span></label></td>
            <td>
                <input type="text" class="form-control validate[required,custom[noSpecial]]" name="chatRoomName"/>
            </td>
        </tr>
        <tr>
            <td><label class="control-label">成员选择</label></td>
            <td>
            <#list caseMemberManagers as caseMemberManager>
                <div class="col-xs-3">
                <input type="hidden" name="partyGroupName_${caseMemberManager.groupPartyId}" value="${caseMemberManager.groupName}"/>
                <label>
                    <#if caseMemberManager.groupPartyId=roleId>
                        <input type="checkbox" value="${caseMemberManager.userLoginId}" checked="checked" disabled />${caseMemberManager.groupRoleType}
                        <input type="hidden" name="chatRoomMember_${caseMemberManager.groupPartyId}" value="${caseMemberManager.userLoginId}">
                    <#else>
                        <input type="checkbox" name="chatRoomMember_${caseMemberManager.groupPartyId}" value="${caseMemberManager.userLoginId}" checked="checked"/>${caseMemberManager.groupRoleType}
                    </#if>
                </label>
                </div>
            </#list>
            </td>
        </tr>
        <tr>
            <td><label class="control-label">描述</label></td>
            <td><textarea name="chatRoomDescription" class="form-control validate[custom[noSpecial]]" placeholder="协作内容描述"></textarea></td>
        </tr>
        </tbody>
    </table>
    <div class="form-group" style="text-align: center">
        <div class="margiv-top-10">
            <input type="button" class="btn green" value="开始协作" onclick="createChatRoom()"/>
        </div>
    </div>
</form>