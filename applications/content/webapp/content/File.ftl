<script language="javascript">
    function sendFile(){
        displayInTab3("sendFileTbl", "文件上传", {requestUrl: "sendFile",width: "800px",height:450,position:"center"});
    }
</script>
<style>
    .file_widget {
        line-height: 200px;
        position: fixed;
        bottom: 42px;
        top: 65px;
        right: 65px;
        width: 1px;
        direction: ltr;
        height: 32px;
        z-index: 200011;
        font-size: 12px;
        line-height: 17px;
    }

    a:focus {
        outline:none;
    }
</style>
<#--<div class="file_widget">
    <a href="#" name="send" id="send" onclick="javascript:sendFile();" class="smallSubmit"><img src="/images/icons/famfamfam/fileFS.png" width="30px" height="30px"></a>
</div>-->

<div class="presence_chat_widget" style="left: 0px;z-index: 9999999999999">
    <div class="chat_dock_pane" id="chat_dock_pane" style="display: block;">
        <div class="float_right" id="chat_dock_bg_pane">
            <div class="chat_dock_pane_group"></div>
            <div class="chat_dock_pane_group"></div>
            <div class="chat_dock_pane_group chat_buddy_list_pane">
                <div class="chat_buddy_list_main_box main_box_shadow" style="width: 50px;border-bottom: 0;box-shadow: none;">
                    <div class="chat_buddy_list_main_inner">
                        <div class="presence_buddylist_header" op="toMsgDetail" tabindex="0"
                             onclick="sendFile()">
                            <div class="presence_buddylist_header_inner" style="padding-top: 0;text-align: center">
                                <img src="/images/icons/famfamfam/fileFS.png" width="30px" height="30px">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>