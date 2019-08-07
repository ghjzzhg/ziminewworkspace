
<#macro fileUpload id="" name="" url="" onComplete="fileRetruen" showLink=true icon="" title="" btnClass="btn green" value="" extension="" readonly=false showTip=false simple="" download=true showThumbnail=false thumbnailWidth="100" thumbnailHeight="100" cssClass="" cssStyle="" dropZones="" sizeLimit=10485760 compressImgWidth="" numberLimit="">
<#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
<#--<%@ attribute name="id" type="java.lang.String" required="true" description="编号"%>-->
<#--<%@ attribute name="name" type="java.lang.String" required="true" description="隐藏域名称"%>-->

<#--<%@ attribute name="url" type="java.lang.String" required="false" description="处理上传的url"%>-->
<#--<%@ attribute name="onComplete" type="java.lang.String" required="false" description="上传处理完成后的js回调函数, 参数为处理逻辑返回的json数据"%>-->
<#--<%@ attribute name="showLink" type="java.lang.Boolean" required="false" description="是否需要生成附件链接"%>-->
<#--<%@ attribute name="icon" type="java.lang.String" required="false" description="上传按钮图标"%>-->
<#--<%@ attribute name="title" type="java.lang.String" required="false" description="上传按钮文字"%>-->
<#--<%@ attribute name="btnClass" type="java.lang.String" required="false" description="button形式时的class，例如 btn-primary、btn-mini等"%>-->
<#--<%@ attribute name="value" type="java.lang.String" required="false" description="隐藏域值"%>-->
<#--<%@ attribute name="extension" type="java.lang.String" required="false" description="附件类型"%>-->
<#--<%@ attribute name="readonly" type="java.lang.Boolean" required="false" description="是否可以修改附件"%>-->
<#--<%@ attribute name="showTip" type="java.lang.Boolean" required="false" description="是否显示帮助信息"%>-->
<#--<%@ attribute name="simple" type="java.lang.Boolean" required="false" description="简洁模式"%>-->
<#--<%@ attribute name="download" type="java.lang.Boolean" required="false" description="是否显示下载按钮"%>-->
<#--<%@ attribute name="showThumbnail" type="java.lang.Boolean" required="false" description="显示缩略图"%>-->
<#--<%@ attribute name="thumbnailWidth" type="java.lang.Integer" required="false" description="缩略图宽度"%>-->
<#--<%@ attribute name="thumbnailHeight" type="java.lang.Integer" required="false" description="缩略图高度"%>-->
<#--<%@ attribute name="cssClass" type="java.lang.String" required="false" description="自定义样式"%>-->
<#--<%@ attribute name="cssStyle" type="java.lang.String" required="false" description="自定义样式"%>-->
<#--<%@ attribute name="dropZones" type="java.lang.String" required="false" description="拖拽上传区域"%>-->
<#--<%@ attribute name="sizeLimit" type="java.lang.Long" required="false" description="附件大小限制"%>-->
<#--<%@ attribute name="compressImgWidth" type="java.lang.Integer" required="false" description="图片上传后压缩比例"%>-->
<#--<%@ attribute name="numberLimit" type="java.lang.Integer" required="false" description="限制附件个数"%>-->
    <#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
<div>
    <#if (!showTi?has_content && (!readonly?has_content || !readonly)) || showTip >
        <span class="icon-question-sign tipl" style="float: right" title="可拖拽文件至上传图标；支持格式(${extension})"></span>
    </#if>
    <span id="attachment_${id}" style="float: right;${cssStyle}" class="${simple?default('')} ${cssClass} ${btnClass}" title="${simple?default('')}"></span>
    <input id="attachment_${id}_value" name="${name}" type="hidden" value="${value}"/>
    <input id="attachment_name_${id}_value" type="hidden" value="${value}"/>
    <#assign attachments=[]>
    <#if value?has_content>
        <#list value?split(",") as file>
            <#assign attachments = attachments + [delegator.findOne("DataResource", {"dataResourceId": file}, true)]>
        </#list>
    </#if>
</div>
<style type="text/css">
    .simple .btn.btn-info{
        padding: 0!important;
    }
    .fa.fa-remove{
        color:red;
        margin-left:10px;
        cursor: pointer;
    }
    .attachment-item a{
        text-decoration: none;
        cursor: default;
    }
        <#if btnClass?has_content>
        #attachment_${id}{
            padding: 0!important;
        }
        </#if>
</style>
<script type="text/javascript">
    var ${id}_uploaderVar,${id}_showLink = ${showLink?c}, ${id}_uploadLoading;
    function init_upload_${id}(){
        var allowedExtensions = [];
        <#if extension?has_content>
            allowedExtensions = "${extension}".replace(/\s+/g, '').split(",");
        </#if>
        var endpoint = 'FineUploader';
        <#if url?has_content>
            endpoint = "${url}";
        </#if>
            <#if !readonly>
            ${id}_uploaderVar = new qq.FineUploaderBasic({
                <#if simple?has_content && !btnClass?has_content>
                    mini:true,
                </#if>
                button: document.getElementById('attachment_${id}'),
                request: {
                    endpoint:endpoint,
                    forceMultipart:true,
                    customHeaders:{'Accept' : 'text/plain, application/json, text/javascript, */*; q=0.01'},
                    params:{'CSRFToken' : getCSRFToken(), 'compressImgWidth' : '${compressImgWidth}'}
                },
                validation: {
                    allowedExtensions: allowedExtensions,
                    sizeLimit:${sizeLimit}
                },
                text: {
                    uploadButton: '${simple?default('上传')}',
                    cancelButton: '取消',
                    failUpload:'上传失败',
                    dragZone: ''
                },
                <#if dropZones?has_content>
                    dragAndDrop:{
                        extraDropzones:$("${dropZones}").get()
                    },
                </#if>
                failedUploadTextDisplay:{
                    mode:'none'
                },
                callbacks: {
                    onSubmit: function(id, fileName){
                        <#if numberLimit?has_content>
                            var oldValue = $("#attachment_${id}_value").val();
                            if(!oldValue){
                                return true;
                            }else{
                                oldValue = oldValue.split(",");
                                if(oldValue.length == ${numberLimit}){//达到上限
                                    showError("最多允许上传" + ${numberLimit} + "个附件.");
                                    return false;
                                }
                            }
                        </#if>
                    },
                    onUpload:function(id, fileName){
                        ${id}_uploadLoading = layer.load(1, {
                            shade: [0.1,'#fff']
                        });
                    },
                    onComplete: function(id, fileName, responseJSON) {
                        layer.close(${id}_uploadLoading);
                        $("#attachment_${id} ul.qq-upload-list").remove();
                        var oldValue = $("#attachment_${id}_value").val();
                        var oldNameValue = $("#attachment_name_${id}_value").val();
                        $("#attachment_${id}_value").val( (oldValue ? oldValue + "," : "") + responseJSON.fileId);
                        $("#attachment_name_${id}_value").val( (oldNameValue ? oldNameValue + "," : "") + responseJSON.fileName);
                        <#if showLink>
                        createUploadItem(responseJSON.fileId, fileName);
                        </#if>
                        showInfo("上传成功");
                        <#if onComplete?has_content>
                            if(window["${onComplete}"]){
                                window["${onComplete}"](responseJSON);
                            }
                        </#if>
                    }
                },
                messages: {
                    typeError: "不支持的文件格式",
                    sizeError: "{file} 文件超过上传限制 {sizeLimit}",
                    onLeave: "文件正在上传, 此时离开将取消上传."
                },
                showMessage: function(message) {
                    console.log(message);
                    if(message.indexOf("不支持的文件格式") > 0){
                        showError(message);
                    }else{
                        showInfo(message);
                    }
                }
            });
        </#if>
        <#if !readonly?has_content || !readonly>
            <#if btnClass?has_content>
                $("#attachment_${id} .btn").addClass("${btnClass}");
            </#if>
            <#if icon?has_content>
                $("#attachment_${id} .btn.btn-mini").attr("class", "${icon}").css({"background-color": "transparent", "border" : 0});
                var $uploadIcon = $("#attachment_${id} .${icon} .icon-upload");
                $uploadIcon.attr("style", $uploadIcon.attr("style") + ";background-image:none!important");
            </#if>
        </#if>
        <#if attachments?has_content && showLink>
            <#list attachments as attachment>
                createUploadItem('${attachment.dataResourceId}', '${attachment.dataResourceName}');
            </#list>
        </#if>
    }

    function createUploadItem(fileId, fileName){
        var attStr = '', downloadStr = "";
        var fileType = fileName.substr(fileName.lastIndexOf('.')+1,fileName.length);
        var removeStr = '<span class="tipl fa fa-remove ' + fileId + '" onclick="remove_upload_${id}(\'' + fileId + '\',\'' + fileName + '\')" title="删除"></span>';
        if(${showThumbnail?c}){
            attStr = '<div class="attachment-thumbnail ' + fileId + '">';
            if("png,jpg,jpeg,gif,bmp".indexOf(fileType.toLowerCase()) > -1){
                attStr += '<a class="attachment-image" title="' + fileName + '" href="#here" ><img class="thumbnail_image" src="/oa/control/imageView?fileName=' + fileId + '" style="width: ${thumbnailWidth?default('100')}px;height: ${thumbnailHeight?default('100')}px"></a>';
            }else {
                attStr += '<a title="' + fileName + '" href="#here"><img class="thumbnail_image" src="/css/themes/virgo/img/files/' + responseJSON.fileType.toUpperCase() + '.png" style="width: ${thumbnailWidth?default('100')}px;height: ${thumbnailHeight?default('100')}px"><div style="text-align:center">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '.' + fileType + '</div></a>';
            }
            attStr += removeStr + "</div>";
        }else{
            if("png,jpg,jpeg,gif,bmp".indexOf(fileType.toLowerCase()) > -1) {
                attStr = '<a class="attachment-image" title="' + fileName + '" style="color: #55a3e5;margin-left:5px" href="#here">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '</a>';
            }else{
                attStr = '<a class="tipl ' + fileId + '" title="' + fileName + '" style="color: #55a3e5;margin-left:5px" href="#here">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '</a>';
            }
            attStr += removeStr;
        }
        $("#attachment_${id}").after('<span id="attachment_attStr_' + fileId + '" class="attachment-item" style="background-color: yellow;border: 1px solid #a5d24a;margin-right: 5px;padding: 5px;float: left;background: #cde69c">' + attStr + '</span>');
    }

    function get_upload_${id}(){
        return $("#attachment_${id}_value").val();
    }
    function remove_upload_${id}(id,name){
        var confirmIndex = layer.confirm("确定删除吗?", {
            btn: ['确定','取消']
        },function(){
            $("." + id).remove();
            $("." + name).remove();
            var oldValue = $("#attachment_${id}_value").val();
            var oldNameValue = $("#attachment_name_${id}_value").val();
            if(!oldValue){
                oldValue = "";
                oldNameValue = "";
            }
            oldValue = oldValue.split(",");
            oldNameValue = oldNameValue.split(",");
            var newValue = "";
            var newNameValue = "";
            for(var i in oldValue){
                var value = oldValue[i];
                if(value && value != id){
                    newValue += value + ",";
                }
            }
            for(var i in oldNameValue){
                var value = oldNameValue[i];
                if(value && value != name){
                    newNameValue += value + ",";
                }
            }
            if(newValue.length > 0){
                newValue = newValue.substring(0, newValue.length - 1);
            }
            if(newNameValue.length > 0){
                newNameValue = newNameValue.substring(0, newNameValue.length - 1);
            }
            $("#attachment_${id}_value").val(newValue);
            $("#attachment_name_${id}_value").val(newNameValue);
            $("#attachment_attStr_"+id).remove();
            layer.close(confirmIndex);
            <#--<#if userLogin?has_content> 将实际文件删除后，导致引用该文件的记录加载出错
                $.ajax({
                    type:'POST',
                    url: 'deleteUplodeFile',
                    data:{'fileId' : id},
                    asyn:true,
                    dataType:'json',
                    success:function (content) {
                        showInfo(content.message);
                        layer.close(confirmIndex);
                    }
                });
                <#else>
                    layer.close(confirmIndex);
            </#if>-->
        })
    }
    init_upload_${id}();
</script>
</#macro>
