<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/zxdoc/static/account.js?t=20170909"></script>
<style type="text/css">
    .fileinput-wrapper .fileinput{
        padding-right: 0;
    }
    .normal-bar{
        margin-top:-10px;
    }
</style>
<div style="text-align: center">
    <form id="avatarForm">
<@fileinput name="atta" thumbnail=true multiple=false thumbnailWidth="200"/>
<#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
    <div style="color: red">(上传所有文件大小请不要超过5兆)</div>
    <button style="margin-top:5px" type="button" class="btn btn-primary" onclick="$.account.uploadUserAvatar()">确定</button>
    </form>
</div>