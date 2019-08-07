<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<style type="text/css">
    #toolbarBottom_search .datepicker {
        width: 65px !important;
    }

    .ztree li span.button.switch{
        display: none;
    }

    .ztree li span.button.pIcon00_ico_open:before{
        content: "\e012";
    }
    .ztree li span.button.pIcon00_ico_close:before{
        content: "\e012";
    }
    .ztree li span.button.pIcon01_ico_open:before{
        content: "\e118";
    }
    .ztree li span.button.pIcon01_ico_close:before{
        content: "\e117";
    }
    .ztree li span.button.pIcon01_ico_docu:before {
        content: "\e117";
    }
    .ztree li span.button.pIcon01_ico_docu {
        color: darkgray;
    }

    .ztree li span.button.pIcon01_ico_open, .ztree li span.button.pIcon00_ico_open{
        color: darkgray;
    }
    .ztree li span.button.pIcon01_ico_close, .ztree li span.button.pIcon00_ico_close{
        color: darkgray;
    }

    .ztree li span.button.ico_open:before{
        content: "\e118";
    }
    .ztree li span.button.ico_close:before{
        content: "\e118";
    }
    .ztree li span.button.ico_open{
        color: #0a6ebd;
        vertical-align: middle;
    }
    .ztree li span.button.ico_close{
        color: darkgray;
        vertical-align: middle;
    }
    .ztree li span.button.icon01_ico_docu:before {
        content: "\e008";
    }
    .ztree li span.button.icon01_ico_docu {
        color: #0a6ebd;
    }
    .ztree li span.button.icon02_ico_docu:before {
        content: "\e008";
    }
    .ztree li span.button.icon02_ico_docu {
        color: darkgray;
    }
</style>

<input type="hidden" id="author_s" value="${userLogin.get("partyId")}"/>
<div id="presence_widget" class="presence_chat_widget">
    <div class="chat_dock_pane" id="chat_dock_pane" style="display: block;">
        <div class="float_right" id="chat_dock_bg_pane">
            <div id="presence_chat_container" class="chat_dock_pane_group"></div>
            <div id="presence_chat_dock" class="chat_dock_pane_group"></div>
            <div id="presence_main" class="chat_dock_pane_group chat_buddy_list_pane">
                <div id="presence_buddylist" class="chat_buddy_list_main_box main_box_shadow" style="width: 120px">
                    <div class="chat_buddy_list_main_inner">
                        <div id="presence_widgetstatus" class="presence_buddylist_header" op="toMsgDetail" tabindex="0"
                             onclick="$.im.operateMsg('presence_widget')">
                            <div id="presence_buddylist_status" class="presence_buddylist_header_inner">
                <span id="roster_title_collapsed" class="roster_title_collapsed" style="display:block">
                    <img id="popout_chat_min" src="/im/static/img/1pxDummy.png"
                         class="pop_out_chat minimized float_right" title="弹出" tabindex="0" alt="弹出">
                    <span id="chat_roster_collapsed_disconnect" class="hide">
                        <img class="chat_error float_right" alt="loader"
                             src="/im/static/img/spinner_16_174.gif" width="16" height="12">
                        正在连接...
                    </span>
                    <span id="presence_buddylist_presence_status" class="active">
                        <img class="roster_header_min_icon float_left"
                             src="/im/static/img/1pxDummy.png" alt="null">
                    </span>
                    <img class="chat_buddy_status_img float_right hide"
                         src="/im/static/img/1pxDummy.png" alt="roster_title_collapsed">
                    <span id="buddy_list_notify" class="newMessages_roster hide" title="新消息"></span>
                    <span id="buddy_list_min_text" class="buddy_list_min_text">站内信 </span>
                </span>
                <span id="roster_title_expanded" class="roster_title_expanded" style="width: 300px">
                    <img id="popout_chat" src="/im/static/img/1pxDummy.png"
                         class="pop_out_chat_x float_right" title="弹入" style="display: block;" tabindex="0" alt="弹出">
                    <img class="roster_header_icon float_left" src="/im/static/img/1pxDummy.png"
                         title="站内信" alt="站内信">
                    <div class="buddy_list_max_text">
                        站内信
                    </div>
                </span>
                            </div>
                        </div>
                        <div id="presence_widgetstatus_detail" class="chat_buddy_list_content_wrapper">
                            <div id="chat-tabs-container" class="yui3-skin-sam" style="padding: 10px;">
                                <div id="im-tabs">
                                    <ul>
                                        <li><a href="#contacts">联系人</a>
                                        </li>
                                        <li><a href="#recent">最近</a>
                                        </li>
                                        <li><a href="#groups">群组</a>
                                        </li>
                                        <li><a href="#settings">设置</a>
                                        </li>
                                    </ul>
                                    <div class="panels"
                                         style="overflow: auto;background-color: #ffffff;">
                                        <#--联系人-->
                                        <div id="contacts">
                                            <@treeInline id="dataScopeDeptTree2" url="GetOrganizationTree" defaultExpand=false onselect="onDataScopeDept2" height="330px" edit=false nameHtml=true/>
                                        </div>
                                        <#--最近联系人-->
                                        <div id="recent">
                                        </div>
                                        <#--群组-->
                                        <div id="groups">
                                        </div>
                                        <#--设置-->
                                        <div id="settings">
                                            <div style="padding: 10px">
                                                <div>
                                                <label>声音提示:
                                                    <input type="checkbox" id="soundCtrl">
                                                </label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
    <div id="chatWindow" class="presence_chat_widget" style="display:none; position: fixed;bottom: 42px;right:400px;">
        <div class="chat_dock_pane" style="display: block;">
            <div class="float_right">
                <div class="chat_dock_pane_group chat_buddy_list_pane" style="width: 500px">
                    <div class="chat_buddy_list_main_box main_box_shadow" style="width: 200px">
                        <div class="chat_buddy_list_main_inner">
                            <div class="presence_buddylist_header" op="toMsgDetail" tabindex="0"
                                 onclick="$.im.operateMsg('chatWindow', '200px', '500px')">
                                <div class="presence_buddylist_header_inner">
                <span class="roster_title_collapsed">
                    <img src="/im/static/img/1pxDummy.png"
                         class="pop_out_chat minimized float_right" title="弹出" tabindex="0" alt="弹出">
                    <span class="active">
                        <img class="roster_header_min_icon float_left"
                             src="/im/static/img/1pxDummy.png" alt="null">
                    </span>
                    <img class="chat_buddy_status_img float_right hide"
                         src="/im/static/img/1pxDummy.png" alt="roster_title_collapsed">
                    <span id="buddy_list_notify" class="newMessages_roster hide" title="新消息"></span>
                    <span id="buddy_list_min_text" class="buddy_list_min_text">消息窗口 </span>
                </span>
                <span id="roster_title_expanded" class="roster_title_expanded" style="width: 100%">
                    <img id="popout_chat" src="/im/static/img/1pxDummy.png"
                         class="pop_out_chat_x float_right" title="弹入" style="display: block;" tabindex="0" alt="弹入">
                    <img class="roster_header_icon float_left" src="/im/static/img/1pxDummy.png"
                         title="消息窗口" alt="消息窗口">
                    <div class="buddy_list_max_text">
                        消息窗口
                    </div>
                </span>
                                </div>
                            </div>
                            <div class="chat_buddy_list_content_wrapper yui3-skin-sam" style="height: 450px;">
                                <div id="chat-tabs">
                                    <ul>
                                        <#--<li><a href="#contacts">联系人</a>
                                        </li>-->
                                    </ul>
                                    <div id="chatDetails" class="messaging">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<div id="msgRecord" style="display: none">
    <div>
        <div class="image">
            <img src="/im/static/img/user-empty.png" width="50px"
                 class="img-polaroid">
        </div>
        <div class="date" style="margin-top: 3px"></div>
        <div class="text">
            <a href="#" class="msgAuthor"></a>

            <p class="msgContent"></p>
        </div>
    </div>
</div>
<script type="text/javascript">
    <#-- creating the JSON Data -->
    <#macro fillTree rootCat dataType="" iconSkin="">
        <#if (rootCat?has_content)>
            <#list rootCat as root>
            {
                id: "${root.id}",
                code: "${root.code}",
                oriName: unescapeHtmlText("<#if root.name??>${root.name?js_string}<#else>${root.id?js_string}</#if>"),
                pId: "${root.pId}",
                iconSkin: <#if iconSkin?has_content>"${iconSkin}"<#else>"${root.iconSkin?default('')}"</#if>,
                    <#if dataType?has_content>dataType: "${dataType}",</#if>
                name: unescapeHtmlText("<#if root.name??>${root.name?js_string}<#else>${root.id?js_string}</#if>")
                <#if root_has_next>
                },
                <#else>
                }
                </#if>
            </#list>
        </#if>
    </#macro>
    var deptTreeData = [
    <#if (completedTreeContext?has_content)>
        <@fillTree rootCat = completedTreeContext dataType="dept" iconSkin="pIcon01"/>
    </#if>
    ];

    var staffData = [
    <#if (staffData?has_content)>
        <@fillTree rootCat = staffData dataType="staff" iconSkin="icon02"/>
    </#if>
    ]

    YUI({
        base:'/images/lib/yui/'
    }).use('tabview', function (Y) {
        var tabview = new Y.TabView({srcNode:'#im-tabs'});
        tabview.render();
    });

    function onDataScopeDept2(id, node){
        var type = node.dataType;
        if(type){
            if("dept" == type && node.isParent){
                $.treeInline['dataScopeDeptTree2'].tree.expandNode(node);
            }else if("staff" == type){
                if(!node.imStatus || node.imStatus == 'offline'){
                    showError("对方不在线");
                }else{
                    var partyId = node.id;
                    $.im.createUserChatWin(partyId);
                }
            }
        }
    }

    $.treeInline['dataScopeDeptTree2'].initTreeInline(deptTreeData.concat(staffData));
    $(function(){
        $.im.initChatListener();//初始化聊天
    })

</script>