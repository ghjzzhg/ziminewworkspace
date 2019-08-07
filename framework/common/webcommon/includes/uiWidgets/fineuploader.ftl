<#--https://select2.github.io/-->
<#include "tagsinput.ftl">

<#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
<#assign fileSize = fileSize?number * 1048576>
<#--<link rel='stylesheet' href='/images/lib/fineuploader/v5/fine-uploader.css' />-->
<script type="text/javascript" src='/images/lib/fineuploader/v5/fine-uploader.core.min.js'></script>
<style type="text/css">
    .simple .btn.btn-info{
        padding: 0!important;
    }
    .attach-wrapper .fa.fa-remove{
        color:red;
        margin-left:10px;
        cursor: pointer;
    }
    .attachment-item a{
        text-decoration: none;
        cursor: default;
    }

    .attach-wrapper {
        background-color: yellow;
        border: 1px solid #a5d24a;
        margin-right: 5px;
        padding: 5px;
        float: left;
        background: #cde69c;
    }

    .attach-wrapper a, .attach-wrapper a:active{
        cursor: default;
        text-decoration: none;
    }
</style>
<#--
<#macro fineuploader id name value="" width="100%" simpleMode=false download=false thumbnail=false thumbnailWidth=100 thumbnailHeight=100 sizeLimit=10485760 compressImgWidth="" numberLimit=0 readonly=false showTip=false url="" onComplete="" showLink=true icon="" title="" btnClass="" extension="">
<div>
    <#if showTip>
        <span class="icon-question-sign tipl" style="float: right" title="支持格式(${extension})"></span>
    </#if>
    <span id="attachment_${id}" style="float: right;${cssStyle}" class="${simple ? 'simple' : ''} ${cssClass} ${btnClass}" title="${simple ? '上传' : ''}"></span>
    <input id="attachment_${id}_value" name="${name}" type="hidden" value="${value}"/>
</div>
</#macro>-->
