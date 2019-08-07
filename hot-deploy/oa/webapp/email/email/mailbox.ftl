<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<@treeInline id="email" url="GetEmailBox" onselect="onSelectFolder" edit=false/>

<style type="text/css">
    .ztree li{
        line-height: 20px;
    }
    .ztree li span.button.pIcon01_ico_open{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/1_open.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.pIcon01_ico_close{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/1_close.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.pIcon02_ico_open, .ztree li span.button.pIcon02_ico_close{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/2.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon01_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/3.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon02_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/4.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon03_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/5.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon04_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/6.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon05_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/7.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon06_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/8.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
    .ztree li span.button.icon07_ico_docu{margin-right:2px; background: url(/images/z-tree/css/zTreeStyle/img/diy/9.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
</style>

<script type="text/javascript">

    $(function () {
        $.treeInline['email'].initTreeInline();
    })

    function onSelectFolder(id) {
        var url = "";
        if(id == 'compose'){
            url = "ComposeMail";
        }else if(id == "folders"){
            url = "Folders";
        }else if(id == "inbox"){
            url = "Inbox";
        }else if(id == "draft"){
            url = "Draft";
        }else if(id == "sent"){
            url = "Sent";
        }else if(id == "trash"){
            url = "Trash";
        }else if(id == "contacts" || id == "groups"){
            url = "Groups";
        }else if(id == "internal"){
            url = "InternalContactList";
        }else if(id == "external"){
            url = "ExternalContactList";
        }else if(id == "reject"){
            url = "Reject";
        }else if(id == "filter"){
            url = "Filter";
        }else if(id == "search"){
            url = "SearchMail";
        }else if(id == "signature"){
            url = "Signature";
        }else if(id == "autoReply"){
            url = "AutoReply";
        }else if(id == "settings"){
            url = "Settings";
        }else{
            url = "CustomFolder?id=" + id;
        }
        if(url){
            $.ajax({
                type: 'GET',
                url: url,
                async: true,
                dataType: 'html',
                success: function (content) {
                    $("#mailContent").html(content);
                }
            });
        }
    }
</script>